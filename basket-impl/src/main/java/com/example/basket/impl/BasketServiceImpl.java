package com.example.basket.impl;

import akka.Done;
import akka.NotUsed;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.example.basket.api.BasketService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.BadRequest;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import org.pcollections.TreePVector;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


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
        return item -> {
            if (baskets.get(id) == null)
                throw new NotFound("Basket not found");
            baskets.compute(id, removeFromBasket(item));
            return CompletableFuture.completedFuture(Done.getInstance());
        };
    }

    /*

 ## Add 3 items of something
 curl -X POST -d '{"count":3, "id":"3666666F-3333-5555-2222-F31111EE0000"}' http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C/items
 ## Add 3 more items of something
 curl -X POST -d '{"count":3, "id":"3666666F-3333-5555-2222-F31111EE0000"}' http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C/items

 # check the basket
 curl http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C

 # remove 2 items of that something
 curl -X DELETE -d '{"count":2, "id":"3666666F-3333-5555-2222-F31111EE0000"}' http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C/items
 # check the basket (only 4 are left)
 curl http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C

 # try to remove 6
 curl -X DELETE -d '{"count":6, "id":"3666666F-3333-5555-2222-F31111EE0000"}' http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C/items
 # check the basket (only 4 are left)
 curl http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C


     */

    private BiFunction<UUID, Basket, Basket> removeFromBasket(BasketItem item) {
        return (id, oldBasket) -> {
            int currentCount = oldBasket.getItems().stream().filter(it -> it.getId().equals(item.getId())).mapToInt(BasketItem::getCount).sum();

            if (currentCount < item.getCount())
                throw new BadRequest("Not enough items");

            TreePVector<BasketItem> newItemList  = TreePVector.from(
                    oldBasket.getItems().stream().filter(it -> !it.getId().equals(item.getId())).collect(Collectors.toList())
            ).plus(new BasketItem(item.getId(), currentCount - item.getCount()));
            return new Basket(id, newItemList);
        };

    }

}
