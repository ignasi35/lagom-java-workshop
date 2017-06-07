package com.example.basket.impl.entity;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

public interface BasketCommand extends Jsonable{

    enum GetBasket implements BasketCommand, PersistentEntity.ReplyType<BasketState> {
        INSTANCE
    }

    @Value
    class AddItem implements BasketCommand, PersistentEntity.ReplyType<Done>{
        private final UUID id;
        private final int count;
        @JsonCreator
        public AddItem(UUID id, int count) {
            this.id = id;
            this.count = count;
        }

    }

    @Value
    class DeleteItem implements BasketCommand, PersistentEntity.ReplyType<Done>{
        private final UUID id;
        private final int count;
        @JsonCreator
        public DeleteItem(UUID id, int count) {
            this.id = id;
            this.count = count;
        }

    }


}
