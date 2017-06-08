package com.example.inventory.impl.entity;

import java.util.UUID;

/**
 *
 */
public class InsuffucientStock extends Exception {
    public InsuffucientStock(UUID itemId) {
        super(itemId.toString());
    }
}
