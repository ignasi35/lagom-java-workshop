package com.example.inventory.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Value;

import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InventoryEvent.ItemProvisioned.class, name = "item-provisioned"),
        @JsonSubTypes.Type(value = InventoryEvent.ItemDepleted.class, name = "item-depleted")
})
public interface InventoryEvent {
    String getName();

    @Value
    class ItemProvisioned implements InventoryEvent{
        private UUID uuid;
        private int totalCount;

        @JsonCreator
        public ItemProvisioned(UUID uuid, int totalCount) {
            this.uuid = uuid;
            this.totalCount = totalCount;
        }

        @Override
        public String getName() {
            return uuid.toString();
        }
    }
    @Value
    class ItemDepleted implements InventoryEvent{
        private UUID uuid;

        @JsonCreator
        public ItemDepleted(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public String getName() {
            return uuid.toString();
        }
    }
}
