package com.example.inventory.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import java.util.UUID;


public class ApiDomain {

    @Value
    public class Shipment {
        private final UUID id;
        private final String name;
        private final int count;

        @JsonCreator
        public Shipment(UUID id, String name, int count) {
            this.id = id;
            this.name = name;
            this.count = count;
        }
    }

}
