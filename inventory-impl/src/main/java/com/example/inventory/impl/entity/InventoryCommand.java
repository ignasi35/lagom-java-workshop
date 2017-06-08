package com.example.inventory.impl.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

/**
 *
 */
public interface InventoryCommand extends Jsonable{

    @Value
    class IncreaseInventory implements InventoryCommand, PersistentEntity.ReplyType<Integer> {
        private String name;
        private UUID itemId;
        private int count;

        @JsonCreator
        public IncreaseInventory(String name, UUID itemId, int count) {
            this.name = name;
            this.itemId = itemId;
            this.count = count;
        }
    }
    @Value
    class DecreaseInventory implements InventoryCommand, PersistentEntity.ReplyType<Integer> {
        private UUID itemId;
        private int count;

        @JsonCreator
        public DecreaseInventory(UUID itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }
    }
}
