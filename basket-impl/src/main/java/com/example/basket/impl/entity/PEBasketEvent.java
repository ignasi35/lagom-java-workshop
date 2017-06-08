package com.example.basket.impl.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

public interface PEBasketEvent extends Jsonable , AggregateEvent<PEBasketEvent> {
    AggregateEventShards<PEBasketEvent> TAG = AggregateEventTag.sharded(PEBasketEvent.class, 30);

    default AggregateEventTagger<PEBasketEvent> aggregateTag() {
        return TAG;
    }


    @Value
    class ItemAdded implements PEBasketEvent {
        private final UUID basketId;
        private final BasketState basket;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public ItemAdded(UUID basketId, BasketState basket, UUID itemId, int count) {
            this.basketId = basketId;
            this.basket = basket;
            this.itemId = itemId;
            this.count = count;
        }
    }

    @Value
    class CheckedOut implements PEBasketEvent {
        private final UUID basketId;
        private final BasketState basket;

        @JsonCreator
        public CheckedOut(UUID basketId, BasketState basket) {
            this.basketId = basketId;
            this.basket = basket;
        }
    }

    @Value
    class ItemDeleted implements PEBasketEvent {
        private final UUID basketId;
        private final BasketState basket;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public ItemDeleted(UUID basketId, BasketState basket, UUID itemId, int count) {
            this.basketId = basketId;
            this.basket = basket;
            this.itemId = itemId;
            this.count = count;
        }
    }

}
