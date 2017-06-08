package com.example.inventory.impl.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@Value
public class InventoryState implements Jsonable {

    private final Optional<String> name;
    private final Optional<UUID> id;
    private final Optional<Integer> count;

    @JsonCreator
    public InventoryState() {
        name = Optional.empty();
        id = Optional.empty();
        count = Optional.empty();
    }

    private InventoryState(Optional<String> name, Optional<UUID> id, Optional<Integer> count) {
        this.name = name;
        this.id = id;
        this.count = count;
    }

    public InventoryState increase(int byHowMuch) {
        return new InventoryState(
                name,
                id,
                Optional.of( byHowMuch + count.orElse(0) )
        );
    }

    public InventoryState rename(String newName) {
        return new InventoryState(
                Optional.of(newName),
                id,
                count
        );
    }

    public int getCurrentCount() {
        return count.orElse(0).intValue();
    }
}
