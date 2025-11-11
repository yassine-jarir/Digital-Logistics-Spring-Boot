# Automatic Client Order Reservation and Backorder System

## Overview

This implementation provides a complete automatic reservation and backorder management system for the logistics application. The system handles inventory reservations, backorder creation, automatic purchase order triggering, and shipment processing with full traceability.

## Key Features

### 1. **Automatic Reservation Logic**
- Checks available stock in the selected warehouse for each sales order line
- **Full Reservation**: If available ≥ requested → reserves all and updates `reservedQuantity`
- **Partial Reservation**: If 0 < available < requested → reserves available, creates backorder for remainder
- **No Stock**: If available = 0 → creates full backorder and triggers automatic PO to supplier
- Updates inventory: increases `qtyReserved`, decreases available (computed column)
- Logs `InventoryMovement` of type `RESERVED`

### 2. **Backorder Management**
- Tracks backorders in dedicated `backorders` table
- **FIFO Processing**: When supplier delivers stock (INBOUND), automatically allocates to oldest backorders first
- Updates `reservedQuantity` and backorder status (`PENDING`, `PARTIALLY_FULFILLED`, `FULFILLED`)
- Links backorders to automatically generated purchase orders

### 3. **Shipment Processing**
- Creates shipments when orders are `RESERVED` or `PARTIALLY_RESERVED`
- **Shipping**: Updates inventory with OUTBOUND movements, decreases `qtyOnHand` and `qtyReserved`
- **Status Flow**: CREATED → RESERVED → SHIPPED → DELIVERED
- Validates that no stock goes negative

### 4. **Safety Features**
- **No Negative Stock**: All operations validate sufficient inventory before processing
- **Transaction Safety**: All operations are wrapped in `@Transactional` for atomicity
- **Audit Trail**: Complete traceability through `InventoryMovement` records
- **Automatic Recovery**: When PO is received, backorders are automatically fulfilled

## Database Schema Changes

### New Tables

#### 1. `backorders` Table
```sql
CREATE TABLE backorders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    so_line_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity_backordered INT NOT NULL,
    quantity_fulfilled INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fulfilled_at TIMESTAMP NULL,
    triggered_po_id BIGINT NULL,
    notes TEXT,
    FOREIGN KEY (so_line_id) REFERENCES sales_order_lines(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (triggered_po_id) REFERENCES purchase_orders(id)
);

CREATE INDEX idx_backorders_status ON backorders(status);
CREATE INDEX idx_backorders_product_warehouse ON backorders(product_id, warehouse_id);
CREATE INDEX idx_backorders_so_line ON backorders(so_line_id);
```

### Schema Updates

#### 2. Update `shipment_lines` Table
```sql
-- Drop old column if exists
ALTER TABLE shipment_lines DROP FOREIGN KEY IF EXISTS fk_shipment_lines_so_line;
ALTER TABLE shipment_lines DROP COLUMN IF EXISTS sales_order_line_id;

-- Add new columns
ALTER TABLE shipment_lines ADD COLUMN product_id BIGINT NOT NULL AFTER shipment_id;
ALTER TABLE shipment_lines CHANGE COLUMN quantity_shipped quantity INT NOT NULL;

-- Add foreign key
ALTER TABLE shipment_lines ADD CONSTRAINT fk_shipment_lines_product 
    FOREIGN KEY (product_id) REFERENCES products(id);
```

## New Components

### Entities
- **`Backorder`**: Tracks backordered items with fulfillment status
- Enhanced **`ShipmentLine`**: Simplified to reference products directly

### Enums
- **`BackorderStatus`**: PENDING, PARTIALLY_FULFILLED, FULFILLED, CANCELLED
- **`SalesOrderStatus`**: CREATED, PARTIALLY_RESERVED, RESERVED, SHIPPED, DELIVERED, CANCELLED
- **`ShipmentStatus`**: PLANNED, SHIPPED, IN_TRANSIT, DELIVERED, CANCELLED
- **`MovementType`**: Added RESERVED to existing INBOUND, OUTBOUND, ADJUSTMENT

### DTOs
- **`BackorderDTO`**: Backorder information with product and warehouse details
- **`ReservationRequestDTO`**: Request to process reservation for a sales order
- **`ReservationResultDTO`**: Result of reservation with status and backorder list
- **`ShipmentDTO`**: Complete shipment information with lines
- **`ShipmentLineDTO`**: Individual shipment line details

### Repositories
- **`BackorderRepository`**: CRUD and query operations for backorders
- **`ShipmentRepository`**: CRUD operations for shipments (already existed)

### Services

#### `InventoryReservationService`
**Purpose**: Main service for processing automatic reservations and backorders

**Key Methods**:
- `processOrderReservation(Long salesOrderId)`: Main entry point for reservation
- `processLineReservation()`: Handles individual line reservation logic
- `reserveInventory()`: Updates inventory and creates RESERVED movement
- `createBackorder()`: Creates backorder record
- `triggerAutomaticPurchaseOrder()`: Creates PO when no stock available

**Business Rules**:
- Checks available stock (qtyAvailable = qtyOnHand - qtyReserved)
- Reserves maximum available quantity
- Creates backorders for shortfall
- Triggers PO only when no stock available at all
- Updates sales order status based on reservation results

#### `BackorderFulfillmentService`
**Purpose**: Handles automatic backorder fulfillment when stock arrives

**Key Methods**:
- `processPendingBackorders()`: Called automatically when PO is received
- `fulfillBackorder()`: Fulfills individual backorder (full or partial)
- `getBackordersForSalesOrder()`: Retrieves backorders for an order
- `cancelBackorder()`: Cancels a backorder

**Business Rules**:
- FIFO processing (oldest backorders fulfilled first)
- Updates reserved quantity on SO lines
- Creates RESERVED movements for fulfilled backorders
- Automatically updates sales order status when fully reserved

#### `ShipmentService`
**Purpose**: Manages shipment creation and processing

**Key Methods**:
- `createShipment(Long salesOrderId)`: Creates shipment for reserved items
- `shipShipment()`: Processes actual shipment (OUTBOUND movements)
- `markAsDelivered()`: Marks shipment as delivered
- `cancelShipment()`: Cancels a planned shipment

**Business Rules**:
- Only ships reserved quantities
- Creates OUTBOUND movements decreasing qtyOnHand and qtyReserved
- Validates sufficient inventory before shipping
- Updates sales order status through shipment lifecycle

### Controllers

#### `ReservationController` (`/api/client/reservations`)
- **POST** `/process`: Process reservation for a sales order
- **POST** `/{salesOrderId}/process`: Alternative endpoint with path variable

#### `BackorderController` (`/api/client/backorders`)
- **GET** `/sales-order/{salesOrderId}`: Get backorders for a sales order
- **DELETE** `/{backorderId}`: Cancel a backorder

#### `ShipmentController` (`/api/client/shipments`)
- **POST** `/create/{salesOrderId}`: Create shipment for reserved order
- **POST** `/{shipmentId}/ship`: Ship a planned shipment
- **POST** `/{shipmentId}/deliver`: Mark shipment as delivered
- **GET** `/{shipmentId}`: Get shipment details
- **GET** `/sales-order/{salesOrderId}`: Get all shipments for an order
- **DELETE** `/{shipmentId}`: Cancel a planned shipment

## Integration with Existing Code

### PurchaseOrderService Enhancement
The `PurchaseOrderService.receivePurchaseOrder()` method now automatically triggers backorder fulfillment:

```java
// After receiving stock and updating inventory
backorderFulfillmentService.processPendingBackorders(
    productId, warehouseId, receivedQuantity, purchaseOrder
);
```

This ensures that when a supplier delivers stock, any pending backorders are automatically fulfilled in FIFO order.

## API Usage Examples

### 1. Create Sales Order and Process Reservation

```bash
# Step 1: Create Sales Order (existing endpoint)
POST /api/client/sales-orders
{
  "clientId": 1,
  "warehouseId": 1,
  "lines": [
    {
      "productId": 10,
      "quantity": 100
    },
    {
      "productId": 11,
      "quantity": 50
    }
  ]
}

# Step 2: Process Automatic Reservation
POST /api/client/reservations/process
{
  "salesOrderId": 123
}

# Response:
{
  "salesOrderId": 123,
  "salesOrderNumber": "SO-1234567890",
  "status": "PARTIALLY_RESERVED",
  "fullyReserved": false,
  "hasBackorders": true,
  "backorders": [
    {
      "id": 1,
      "productId": 10,
      "productName": "Product A",
      "productSku": "SKU-001",
      "quantityBackordered": 20,
      "quantityFulfilled": 0,
      "status": "PENDING",
      "triggeredPurchaseOrderId": 456
    }
  ],
  "message": "Order partially reserved. 1 backorder(s) created. Automatic purchase orders triggered where needed."
}
```

### 2. Check Backorders for Sales Order

```bash
GET /api/client/backorders/sales-order/123

# Response:
[
  {
    "id": 1,
    "productId": 10,
    "productName": "Product A",
    "quantityBackordered": 20,
    "quantityFulfilled": 0,
    "status": "PENDING",
    "createdAt": "2025-11-11T10:00:00"
  }
]
```

### 3. Receive Purchase Order (triggers automatic backorder fulfillment)

```bash
POST /api/warehouse-manager/purchase-orders/456/receive
{
  "receivedLines": [
    {
      "poLineId": 789,
      "receivedQuantity": 100
    }
  ]
}

# This automatically:
# 1. Updates inventory (qtyOnHand +100)
# 2. Processes pending backorders (reserves 20 for backorder)
# 3. Updates sales order status to RESERVED
# 4. Leaves 80 units in available stock
```

### 4. Create and Ship Shipment

```bash
# Step 1: Create Shipment
POST /api/client/shipments/create/123

# Response:
{
  "id": 1,
  "shipmentNumber": "SHIP-1234567890",
  "salesOrderId": 123,
  "status": "PLANNED",
  "lines": [
    {
      "productId": 10,
      "productName": "Product A",
      "quantity": 100
    }
  ]
}

# Step 2: Ship the Shipment
POST /api/client/shipments/1/ship
{
  "trackingNumber": "TRACK-12345",
  "carrier": "DHL"
}

# This automatically:
# 1. Creates OUTBOUND movements
# 2. Decreases qtyOnHand and qtyReserved
# 3. Updates shipment status to SHIPPED
# 4. Updates sales order status to SHIPPED

# Step 3: Mark as Delivered
POST /api/client/shipments/1/deliver

# Updates shipment and sales order status to DELIVERED
```

## Workflow Diagram

```
Client Creates Order (CREATED)
         ↓
Process Reservation (automatic)
         ↓
    ┌────┴────┐
    ↓         ↓
Available?   No Stock
    ↓         ↓
Reserve    Create Backorder → Trigger Auto PO
    ↓         ↓
RESERVED   PARTIALLY_RESERVED
    ↓         ↓
         ↓ (wait for PO receipt)
         ↓
    PO Received → Auto Fulfill Backorders
         ↓
     RESERVED (when fully reserved)
         ↓
  Create Shipment (PLANNED)
         ↓
  Ship Shipment (SHIPPED) → OUTBOUND movement
         ↓
  Mark Delivered (DELIVERED)
```

## Safety and Validation

### Stock Validation
- All reservation operations check `qtyAvailable` before proceeding
- Shipment operations validate `qtyOnHand` >= required quantity
- Shipment operations validate `qtyReserved` >= required quantity
- No operation can result in negative stock levels

### Transaction Safety
- All service methods use `@Transactional` annotations
- Database rollback occurs if any step fails
- Inventory movements provide complete audit trail

### Error Handling
- Backorder fulfillment failures don't break PO receipt process
- Clear error messages for invalid state transitions
- Validation of order status before operations

## Configuration

### Safety Stock
In `InventoryReservationService`, the safety stock buffer for automatic POs can be configured:

```java
private int calculatePurchaseQuantity(...) {
    int safetyStock = 10; // Configurable safety stock
    return Math.max(backorderQty, totalPendingBackorders) + safetyStock;
}
```

### Supplier Selection
Currently uses first active supplier. To implement product-specific suppliers:
1. Create `product_suppliers` mapping table
2. Update `getSupplierForProduct()` method
3. Add supplier preference logic

## Testing the Implementation

### Manual Testing Steps

1. **Create Sales Order** with products having limited stock
2. **Process Reservation** - verify partial reservation and backorder creation
3. **Check Inventory** - verify qtyReserved increased, qtyAvailable decreased
4. **Receive PO** - verify automatic backorder fulfillment
5. **Create Shipment** - verify only reserved items included
6. **Ship Order** - verify OUTBOUND movements and stock decrease
7. **Check Final State** - verify sales order status is SHIPPED/DELIVERED

### Database Verification Queries

```sql
-- Check inventory status
SELECT p.sku, p.name, i.qty_on_hand, i.qty_reserved, i.qty_available
FROM inventory i
JOIN products p ON i.product_id = p.id
WHERE i.warehouse_id = 1;

-- Check backorders
SELECT b.*, p.sku, p.name, b.quantity_backordered - b.quantity_fulfilled as pending
FROM backorders b
JOIN products p ON b.product_id = p.id
WHERE b.status IN ('PENDING', 'PARTIALLY_FULFILLED');

-- Check inventory movements for audit trail
SELECT im.*, p.sku, so.order_number, po.po_number
FROM inventory_movements im
JOIN products p ON im.product_id = p.id
LEFT JOIN sales_orders so ON im.sales_order_id = so.id
LEFT JOIN purchase_orders po ON im.purchase_order_id = po.id
ORDER BY im.occurred_at DESC
LIMIT 50;
```

## Future Enhancements

1. **Product-Supplier Mapping**: Create dedicated table for product-supplier relationships
2. **Multiple Warehouses**: Allow fulfillment from alternative warehouses
3. **Backorder Notifications**: Email/SMS notifications when backorders are fulfilled
4. **Priority Orders**: Priority queue for urgent backorders
5. **Partial Shipments**: Allow shipping partial quantities while waiting for backorders
6. **Stock Allocation Rules**: Configurable rules for stock allocation (FIFO, priority, etc.)
7. **Reservation Expiry**: Auto-release reservations after timeout
8. **Dashboard**: Real-time backorder monitoring and analytics

## Troubleshooting

### Issue: Backorders not automatically fulfilled after PO receipt
**Solution**: Check that `BackorderFulfillmentService` is injected in `PurchaseOrderService` and the method is called in `receivePurchaseOrder()`.

### Issue: Negative stock after shipment
**Solution**: Verify that reservation was processed before shipment creation. Check `qtyReserved` matches shipment quantities.

### Issue: Sales order stuck in PARTIALLY_RESERVED
**Solution**: Check for pending backorders. Verify POs have been received and backorder fulfillment ran successfully.

## Support

For issues or questions:
1. Check application logs (INFO level shows reservation flow)
2. Verify database constraints are properly created
3. Ensure MapStruct processors are running (`mvn clean compile`)
4. Check transaction logs for rollbacks

---

**Implementation Date**: November 2025  
**Version**: 1.0  
**Compatible with**: Spring Boot 3.x, Java 17+

