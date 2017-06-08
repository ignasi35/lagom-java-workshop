package com.example.basket.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Source;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.example.basket.api.BasketEvent;
import com.example.basket.api.BasketService;
import com.example.basket.impl.entity.BasketCommand;
import com.example.basket.impl.entity.BasketCommand.*;
import com.example.basket.impl.entity.BasketState;
import com.example.basket.impl.entity.PEBasketEvent;
import com.example.basket.impl.entity.PEBasketEvent.*;
import com.example.inventory.api.InventoryEvent;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the HelloService.
 */
public class BasketServiceImpl implements BasketService {

    private final PersistentEntityRegistry registry;

    @Inject
    public BasketServiceImpl(PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(com.example.basket.impl.BasketEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, Basket> getBasket(UUID id) {
        return request -> refFor(id)
                .ask(GetBasket.INSTANCE)
                .thenApply(state -> toApi(id, state));
    }

    @Override
    public ServiceCall<BasketItem, Done> addItem(UUID id) {
        return item -> refFor(id).ask(new AddItem(item.getId(), item.getCount()));
    }

    @Override
    public ServiceCall<BasketItem, Done> deleteItem(UUID id) {
        return item -> refFor(id).ask(new AddItem(item.getId(), item.getCount()));
    }

    @Override
    public Topic<BasketEvent> basketEvents() {
        return TopicProducer.taggedStreamWithOffset(PEBasketEvent.TAG.allTags(), this::streamForTag);
    }


    private Source<Pair<BasketEvent, Offset>, ?> streamForTag(AggregateEventTag<PEBasketEvent> tag, Offset offset) {
        return registry
                .eventStream(tag, offset)
                .filter(eventOffset ->
                        eventOffset.first() instanceof ItemAdded ||
                                eventOffset.first() instanceof ItemDeleted ||
                                eventOffset.first() instanceof CheckedOut
                )
                .map(this::toTopic);
    }

    // --------------------

    private Pair<BasketEvent, Offset> toTopic(Pair<PEBasketEvent, Offset> pair) {
        BasketEvent res;
        if (pair.first() instanceof PEBasketEvent.ItemAdded) {
            ItemAdded evt = (ItemAdded) pair.first();
            res = new BasketEvent.BasketItemAdded(toApi(evt.getBasketId(), evt.getBasket()), evt.getItemId(), evt.getCount());
        } else if (pair.first() instanceof ItemDeleted) {
            ItemDeleted evt = (ItemDeleted) pair.first();
            res = new BasketEvent.BasketItemRemoved(toApi(evt.getBasketId(), evt.getBasket()), evt.getItemId(), evt.getCount());
        } else{
            // checkout
            CheckedOut evt = (CheckedOut) pair.first();
            res = new BasketEvent.BasketCheckedOut(toApi(evt.getBasketId(), evt.getBasket()));
        }
        return Pair.create(res, pair.second());
    }

    private Basket toApi(UUID basketId, BasketState state) {
        List<BasketItem> items = state.getItems().stream().map(dbi -> new BasketItem(dbi.getId(), dbi.getNumberOfItems())).collect(Collectors.toList());
        return new Basket(basketId, TreePVector.from(items));
    }

    // --------------------
    private PersistentEntityRef<BasketCommand> refFor(UUID id) {
        return registry.refFor(BasketEntity.class, id.toString());
    }
}
