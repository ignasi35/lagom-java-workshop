package com.example.basket.impl;

import akka.Done;
import akka.NotUsed;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.example.basket.api.BasketService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import org.pcollections.TreePVector;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;


public class BasketServiceImpl implements BasketService {

    ConcurrentHashMap<UUID, Basket> baskets = new ConcurrentHashMap<>();

    public BasketServiceImpl() {

    }

    @Override
    public ServiceCall<NotUsed, Basket> getBasket(UUID id) {
        return req -> {
            Basket basket = baskets.get(id);
            if (basket == null)
                throw new NotFound("Basket not found.");
            return CompletableFuture.completedFuture(basket);
        };

    }

    @Override
    public ServiceCall<BasketItem, Done> addItem(UUID id) {
        return item -> {
            baskets.putIfAbsent(id, new Basket(id, TreePVector.empty()));
            baskets.computeIfPresent(id, addIntoBasket(item));
            return CompletableFuture.completedFuture(Done.getInstance());
        };
    }

    private BiFunction<UUID, Basket, Basket> addIntoBasket(BasketItem item) {
        return (id, oldBasket) -> new Basket(id, oldBasket.getItems().plus(item));
    }

    @Override
    public ServiceCall<BasketItem, Done> deleteItem(UUID id) {
        return item ->
                CompletableFuture.completedFuture(Done.getInstance())
                ;
    }


}
