package com.example.inventory.impl;

import akka.Done;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.example.basket.api.BasketEvent;
import com.example.basket.api.BasketService;

import com.example.inventory.api.ApiDomain.Shipment;
import com.example.inventory.api.InventoryEvent;
import com.example.inventory.api.InventoryEvent.*;
import com.example.inventory.api.InventoryService;
import com.example.inventory.impl.entity.InventoryCommand;
import com.example.inventory.impl.entity.InventoryCommand.*;
import com.example.inventory.impl.entity.InventoryEntity;
import com.example.inventory.impl.entity.PEInventoryEvent;
import com.example.inventory.impl.entity.PEInventoryEvent.*;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Implementation of the HelloService.
 */
public class InventoryServiceImpl implements InventoryService {

    private final PersistentEntityRegistry registry;
    private BasketService basketService;

    @Inject
    public InventoryServiceImpl(PersistentEntityRegistry registry, BasketService basketService) {
        this.registry = registry;
        this.basketService = basketService;
        registry.register(InventoryEntity.class);

        // this solution is naïve on the approach and wrong altogether in the implementation
        //  a) when applying a filter in the akka stream, the message is missaligned with the offset
        //  b) updating the inventory should be handled in a way that if a product is out of stock an event is
        //     raised and the shppoing-user is notified so she can decide what to do. Also, the out-of-stock product
        //     must be reimbursed. All this corrective actions are not implemented, on the contrary: when the
        //     InventoryEntity detects there's not enough stock for a given Deacrease operation an exception is throw.
        //     Instead, an event other downstream services can listen to would be more appropriate.
        // I will try to get (a) fixed on further versions of this workshop.
        //
        basketService.basketEvents().subscribe().atLeastOnce(
                Flow.<BasketEvent>create()
                        .filter(b -> b instanceof BasketEvent.BasketCheckedOut)
                        .mapConcat(basketEvent -> {
                            BasketEvent.BasketCheckedOut event = (BasketEvent.BasketCheckedOut) basketEvent;
                            return event.getBasket().getItems();
                        })
                        .mapAsync(1, basketItem ->
                                refFor(basketItem.getId())
                                        .ask(new DecreaseInventory(basketItem.getId(), basketItem.getCount()))
                        ).map(ignored -> Done.getInstance())
        );

    }

    @Override
    public ServiceCall<Shipment, Done> provisionItems() {
        return shipment ->
                refFor(shipment.getId())
                        .ask(new IncreaseInventory(
                                shipment.getName(),
                                shipment.getId(),
                                shipment.getCount()))
                        .thenApply(ignored -> Done.getInstance())
                ;
    }

    @Override
    public Topic<InventoryEvent> inventoryEvents() {
        return TopicProducer.singleStreamWithOffset(
                offset ->
                        registry
                                .eventStream(PEInventoryEvent.INSTANCE, offset)
                                .filter(p -> p.first() instanceof PEInventoryIncreased)
                                .map(this::toTopic)
        );
    }

    private Pair<InventoryEvent, Offset> toTopic(Pair<PEInventoryEvent, Offset> pair) {
        PEInventoryIncreased evt = (PEInventoryIncreased) pair.first();


        return Pair.create(new ItemProvisioned(evt.getItemId(), evt.getCountAfterIncreasing()), pair.second());
    }

    private PersistentEntityRef<InventoryCommand> refFor(UUID id) {
        return registry
                .refFor(InventoryEntity.class, "main-warehouse-" + id.toString());
    }

}
