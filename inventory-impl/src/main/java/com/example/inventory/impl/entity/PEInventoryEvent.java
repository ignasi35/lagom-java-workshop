package com.example.inventory.impl.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;


public interface PEInventoryEvent extends Jsonable, AggregateEvent<PEInventoryEvent> {

    AggregateEventTag<PEInventoryEvent> INSTANCE = AggregateEventTag.of(PEInventoryEvent.class, "inventory-events");

    @Override
    default AggregateEventTagger<PEInventoryEvent> aggregateTag() {
        return INSTANCE;
    }

    @Value
    class PEInventoryIncreased implements PEInventoryEvent {
        private int byHowMuch;
        private int countAfterIncreasing;
        private UUID itemId;

        @JsonCreator
        public PEInventoryIncreased(int byHowMuch, int countAfterIncreasing, UUID itemId) {
            this.byHowMuch = byHowMuch;
            this.countAfterIncreasing = countAfterIncreasing;
            this.itemId = itemId;
        }
    }

    @Value
    class PEInventoryDecreased implements PEInventoryEvent {
        private int byHowMuch;
        private int countAfterDecreasing;
        private UUID itemId;

        @JsonCreator
        public PEInventoryDecreased(int byHowMuch, int countAfterDecreasing, UUID itemId) {
            this.byHowMuch = byHowMuch;
            this.countAfterDecreasing = countAfterDecreasing;
            this.itemId = itemId;
        }
    }

    @Value
    class PEInventoryItemLabelled implements PEInventoryEvent {
        private String itemName;
        private UUID itemId;

        @JsonCreator
        public PEInventoryItemLabelled(String itemName, UUID itemId) {
            this.itemName = itemName;
            this.itemId = itemId;
        }
    }


}
