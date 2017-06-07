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
})
public interface BasketEvent {

    String getId();

    @Value
    final class BasketCreated implements BasketEvent {
        public final String id;

        @JsonCreator
        public BasketCreated(UUID id) {
            this.id = id.toString();
        }

    }

    @Value
    final class BasketItemAdded implements BasketEvent {
        public final Basket basket;

        @JsonCreator
        public BasketItemAdded(Basket basket) {
            this.basket = basket;
        }

        @Override
        public String getId() {
            return basket.getId().toString();
        }
    }

    @Value
    final class BasketItemRemoved implements BasketEvent {
        public final Basket basket;

        @JsonCreator
        public BasketItemRemoved(Basket basket) {
            this.basket = basket;
        }

        @Override
        public String getId() {
            return basket.getId().toString();
        }

    }


}
