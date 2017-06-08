package com.example.inventory.impl.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;


@Value
public class InsuffucientStock extends Exception implements Jsonable {
    private UUID itemId;

    @JsonCreator
    public InsuffucientStock(UUID itemId) {
        super(itemId.toString());
        this.itemId = itemId;
    }
}
