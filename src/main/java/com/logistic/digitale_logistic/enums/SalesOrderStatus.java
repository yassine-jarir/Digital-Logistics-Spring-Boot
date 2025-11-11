package com.logistic.digitale_logistic.enums;

public enum SalesOrderStatus {
    CREATED,           // Order created, not yet reserved
    RESERVED,          // All items reserved, ready to ship
    SHIPPED,           // Shipment created and dispatched
    DELIVERED,         // Order delivered to client
    CANCELLED          // Order cancelled
}
