package com.example.ordermanagement.inventory.enums;

public enum InventoryTransactionType {

    // Manual inventory operations
    STOCK_IN,
    STOCK_OUT,
    STOCK_ADJUSTMENT,

    // Order inventory operations
    STOCK_RESERVED,
    STOCK_RELEASED,
    STOCK_CONFIRMED
}