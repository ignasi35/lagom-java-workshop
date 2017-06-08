package com.example.inventory.impl;

import akka.Done;
import akka.japi.Pair;
import com.example.inventory.api.ApiDomain.Shipment;
import com.example.inventory.api.InventoryEvent;
import com.example.inventory.api.InventoryEvent.ItemProvisioned;
import com.example.inventory.api.InventoryService;
import com.example.inventory.impl.entity.InventoryCommand;
import com.example.inventory.impl.entity.InventoryCommand.IncreaseInventory;
import com.example.inventory.impl.entity.InventoryEntity;
import com.example.inventory.impl.entity.PEInventoryEvent;
import com.example.inventory.impl.entity.PEInventoryEvent.PEInventoryIncreased;
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

    @Inject
    public InventoryServiceImpl(PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(InventoryEntity.class);
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

    private PersistentEntityRef<InventoryCommand> refFor(UUID id) {
        return registry.refFor(InventoryEntity.class, "main-warehouse-" + id.toString());
    }



























    private Pair<InventoryEvent, Offset> toTopic(Pair<PEInventoryEvent, Offset> pair) {
        PEInventoryIncreased evt = (PEInventoryIncreased) pair.first();
        return Pair.create(new ItemProvisioned(evt.getItemId(), evt.getCountAfterIncreasing()), pair.second());
    }


}
