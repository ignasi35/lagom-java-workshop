package com.example.basket.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.UUID;


public interface ApiDomain {

    @Value
    @JsonDeserialize
    final class Basket {

        public final UUID id;

        @JsonCreator
        public Basket(UUID id, PSequence<BasketItem> items) {
            this.id = id;
        }

        public static Basket with(UUID id, BasketItem... items){
            return new Basket(id, TreePVector.from(Arrays.asList(items))) ;
        }

    }

    @Value
    @JsonDeserialize
    final class BasketItem {

        private final UUID id;
        private final int count;

        @JsonCreator
        public BasketItem(UUID id, int count) {
            this.id = id;
            this.count = count;
        }
    }

}
