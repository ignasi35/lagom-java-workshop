package com.example.basket.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Value;
import com.example.basket.api.ApiDomain.Basket;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasketEvent.BasketCreated.class, name = "basket-created"),
        @JsonSubTypes.Type(value = BasketEvent.BasketItemAdded.class, name = "basket-item-added"),
        @JsonSubTypes.Type(value = BasketEvent.BasketItemRemoved.class, name = "basket-item-removed"),
        @JsonSubTypes.Type(value = BasketEvent.BasketCheckedOut.class, name = "basket-checked-out")
})
public interface BasketEvent {

    String getId();

    @Value
    final class BasketCreated implements BasketEvent {
        private final String id;

        @JsonCreator
        public BasketCreated(UUID id) {
            this.id = id.toString();
        }

    }

    @Value
    final class BasketCheckedOut implements BasketEvent {
        private final Basket basket;

        @JsonCreator
        public BasketCheckedOut(Basket basket) {
            this.basket = basket;
        }

        @Override
        public String getId() {
            return basket.getId().toString();
        }
    }

    @Value
    final class BasketItemAdded implements BasketEvent {
        private final Basket basket;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public BasketItemAdded(Basket basket, UUID itemId, int count) {
            this.basket = basket;
            this.itemId = itemId;
            this.count = count;
        }

        @Override
        public String getId() {
            return basket.getId().toString();
        }
    }

    @Value
    final class BasketItemRemoved implements BasketEvent {
        private final Basket basket;
        private final UUID itemId;
        private final int count;

        @JsonCreator
        public BasketItemRemoved(Basket basket, UUID itemId, int count) {
            this.basket = basket;
            this.itemId = itemId;
            this.count = count;
        }

        @Override
        public String getId() {
            return basket.getId().toString();
        }

    }


}
