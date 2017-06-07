package com.example.basket.impl.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

public interface BasketEvent extends Jsonable , AggregateEvent<BasketEvent> {
    AggregateEventShards<BasketEvent> TAG = AggregateEventTag.sharded(BasketEvent.class, 30);

    default AggregateEventTagger<BasketEvent> aggregateTag() {
        return TAG;
    }


    @Value
    class ItemAdded implements BasketEvent{
        private final UUID basketId;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public ItemAdded(UUID basketId, UUID itemId, int count) {
            this.basketId = basketId;
            this.itemId = itemId;
            this.count = count;
        }
    }

    @Value
    class ItemDeleted implements BasketEvent{
        private final UUID basketId;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public ItemDeleted(UUID basketId, UUID itemId, int count) {
            this.basketId = basketId;
            this.itemId = itemId;
            this.count = count;
        }
    }

}
