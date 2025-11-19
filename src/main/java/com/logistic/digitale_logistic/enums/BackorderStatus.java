package com.logistic.digitale_logistic.enums;

public enum BackorderStatus {
    PENDING,           // Waiting for stock
    PARTIALLY_FULFILLED, // Some quantity fulfilled
    FULFILLED,         // All quantity fulfilled
    CANCELLED          // Backorder cancelled
}

