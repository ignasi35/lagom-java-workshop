package com.example.inventory.impl.entity;

import lombok.Value;

import java.util.UUID;


@Value
public class InsuffucientStock extends Exception {
    private UUID itemId;

    public InsuffucientStock(UUID itemId) {
        super(itemId.toString());
        this.itemId = itemId;
    }
}
