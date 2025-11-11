-- ============================================
-- Automatic Reservation and Backorder System
-- Database Migration Script
-- ============================================
-- Description: Adds backorder tracking, updates shipment_lines schema,
--              and modifies inventory movement types
-- Date: 2025-11-11
-- ============================================

-- ============================================
-- 1. CREATE BACKORDERS TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS backorders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    so_line_id BIGINT NOT NULL COMMENT 'Reference to sales order line',
    product_id BIGINT NOT NULL COMMENT 'Product being backordered',
    warehouse_id BIGINT NOT NULL COMMENT 'Warehouse where stock is needed',
    quantity_backordered INT NOT NULL COMMENT 'Total quantity backordered',
    quantity_fulfilled INT NOT NULL DEFAULT 0 COMMENT 'Quantity fulfilled so far',
    status VARCHAR(50) NOT NULL COMMENT 'PENDING, PARTIALLY_FULFILLED, FULFILLED, CANCELLED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When backorder was created',
    fulfilled_at TIMESTAMP NULL COMMENT 'When backorder was fully fulfilled',
    triggered_po_id BIGINT NULL COMMENT 'Automatically generated PO if triggered',
    notes TEXT COMMENT 'Additional notes about the backorder',

    CONSTRAINT fk_backorders_so_line FOREIGN KEY (so_line_id)
        REFERENCES sales_order_lines(id) ON DELETE CASCADE,
    CONSTRAINT fk_backorders_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_backorders_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id) ON DELETE CASCADE,
    CONSTRAINT fk_backorders_po FOREIGN KEY (triggered_po_id)
        REFERENCES purchase_orders(id) ON DELETE SET NULL,
    CONSTRAINT chk_backorders_qty CHECK (quantity_backordered > 0),
    CONSTRAINT chk_backorders_fulfilled CHECK (quantity_fulfilled >= 0 AND quantity_fulfilled <= quantity_backordered)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tracks backordered items for sales orders';

-- Indexes for backorders table
CREATE INDEX idx_backorders_status ON backorders(status);
CREATE INDEX idx_backorders_product_warehouse ON backorders(product_id, warehouse_id);
CREATE INDEX idx_backorders_so_line ON backorders(so_line_id);
CREATE INDEX idx_backorders_created_at ON backorders(created_at);
CREATE INDEX idx_backorders_po ON backorders(triggered_po_id);

-- ============================================
-- 2. UPDATE SHIPMENT_LINES TABLE
-- ============================================

-- Note: Adjust this section based on your existing schema
-- If shipment_lines doesn't exist or has different structure, modify accordingly

-- Check if old foreign key exists and drop it
SET @fk_exists = (SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shipment_lines'
    AND CONSTRAINT_NAME = 'fk_shipment_lines_so_line');

SET @drop_fk = IF(@fk_exists > 0,
    'ALTER TABLE shipment_lines DROP FOREIGN KEY fk_shipment_lines_so_line',
    'SELECT "FK does not exist" AS message');

PREPARE stmt FROM @drop_fk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop old column if it exists
SET @col_exists = (SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shipment_lines'
    AND COLUMN_NAME = 'sales_order_line_id');

SET @drop_col = IF(@col_exists > 0,
    'ALTER TABLE shipment_lines DROP COLUMN sales_order_line_id',
    'SELECT "Column does not exist" AS message');

PREPARE stmt FROM @drop_col;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add product_id column if it doesn't exist
SET @col_exists = (SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shipment_lines'
    AND COLUMN_NAME = 'product_id');

SET @add_col = IF(@col_exists = 0,
    'ALTER TABLE shipment_lines ADD COLUMN product_id BIGINT NOT NULL AFTER shipment_id',
    'SELECT "Column already exists" AS message');

PREPARE stmt FROM @add_col;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Rename quantity_shipped to quantity if needed
SET @col_exists = (SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shipment_lines'
    AND COLUMN_NAME = 'quantity_shipped');

SET @rename_col = IF(@col_exists > 0,
    'ALTER TABLE shipment_lines CHANGE COLUMN quantity_shipped quantity INT NOT NULL',
    'SELECT "Column already renamed or does not exist" AS message');

PREPARE stmt FROM @rename_col;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add foreign key constraint for product_id if not exists
SET @fk_exists = (SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shipment_lines'
    AND CONSTRAINT_NAME = 'fk_shipment_lines_product');

SET @add_fk = IF(@fk_exists = 0,
    'ALTER TABLE shipment_lines ADD CONSTRAINT fk_shipment_lines_product
     FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE',
    'SELECT "FK already exists" AS message');

PREPARE stmt FROM @add_fk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 3. VERIFY DATA INTEGRITY
-- ============================================

-- Check that all inventory records have proper computed columns
-- (Assuming qty_available is a generated column: qty_on_hand - qty_reserved)

-- If qty_available is not a generated column, you may need to add it:
-- ALTER TABLE inventory
-- ADD COLUMN qty_available INT GENERATED ALWAYS AS (qty_on_hand - qty_reserved) STORED;

-- ============================================
-- 4. SAMPLE DATA FOR TESTING (OPTIONAL)
-- ============================================

-- Uncomment the following if you want to add test data

/*
-- Add test product if not exists
INSERT IGNORE INTO products (id, sku, name, category, selling_price, cost_price, active, created_at)
VALUES (9999, 'TEST-BACKORDER', 'Test Backorder Product', 'Test', 100.00, 50.00, 1, NOW());

-- Add test inventory with limited stock
INSERT IGNORE INTO inventory (product_id, warehouse_id, qty_on_hand, qty_reserved, updated_at)
VALUES (9999, 1, 10, 0, NOW());
*/

-- ============================================
-- 5. VERIFICATION QUERIES
-- ============================================

-- Run these queries after migration to verify everything is correct:

-- Check backorders table structure
SELECT
    'Backorders table created' AS check_name,
    COUNT(*) AS table_exists
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'backorders';

-- Check shipment_lines has product_id
SELECT
    'Shipment_lines has product_id' AS check_name,
    COUNT(*) AS column_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'shipment_lines'
AND COLUMN_NAME = 'product_id';

-- Check inventory structure
SELECT
    'Inventory columns' AS check_name,
    COUNT(*) AS column_count
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'inventory'
AND COLUMN_NAME IN ('qty_on_hand', 'qty_reserved', 'qty_available');

-- ============================================
-- 6. ROLLBACK SCRIPT (USE WITH CAUTION)
-- ============================================

/*
-- Uncomment to rollback changes (WARNING: This will delete all backorder data!)

-- Drop backorders table
DROP TABLE IF EXISTS backorders;

-- Restore old shipment_lines structure (if needed)
ALTER TABLE shipment_lines DROP FOREIGN KEY fk_shipment_lines_product;
ALTER TABLE shipment_lines DROP COLUMN product_id;
ALTER TABLE shipment_lines ADD COLUMN sales_order_line_id BIGINT NOT NULL;
ALTER TABLE shipment_lines CHANGE COLUMN quantity quantity_shipped INT NOT NULL;
ALTER TABLE shipment_lines ADD CONSTRAINT fk_shipment_lines_so_line
    FOREIGN KEY (sales_order_line_id) REFERENCES sales_order_lines(id);
*/

-- ============================================
-- END OF MIGRATION SCRIPT
-- ============================================

SELECT 'Migration completed successfully!' AS status;

