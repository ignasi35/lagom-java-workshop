package com.example.basket.impl.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.UUID;
import java.util.stream.Collectors;

@Value
public final class BasketState implements CompressedJsonable {

    private final PSequence<DomainBasketItem> items;
    private final boolean alreadyOrdered;
    public static BasketState EMTPY_BASKET = new BasketState(TreePVector.empty(), false);

    @JsonCreator
    public BasketState(PSequence<DomainBasketItem> items, boolean alreadyOrdered) {
        this.items = items;
        this.alreadyOrdered = alreadyOrdered;
    }

    public boolean withEnough(UUID id, int count) {
        return items.stream().filter(dbi-> dbi.getId().equals(id)).mapToInt(DomainBasketItem::getNumberOfItems).sum() > count;
    }

    public BasketState addItem(UUID itemId, int addedCount) {
        BasketState newState  = new BasketState(
                items.plus(new DomainBasketItem(itemId, addedCount)),
                alreadyOrdered);
        return newState;
    }

    public BasketState removeItem(UUID itemId, int removedCount) {
        int totalCount = items.stream().filter(dbi-> dbi.getId().equals(itemId)).mapToInt(DomainBasketItem::getNumberOfItems).sum();
        PSequence<DomainBasketItem> newItems =
                TreePVector.from(
                        items.stream().filter(dbi-> !dbi.getId().equals(itemId)).collect(Collectors.toList())
                ).plus(new DomainBasketItem(itemId, totalCount-removedCount));
        return new BasketState(
                newItems,
                alreadyOrdered);
    }
}
