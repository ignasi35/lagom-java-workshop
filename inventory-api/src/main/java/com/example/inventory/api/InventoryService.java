package com.example.inventory.api;

import akka.Done;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;

import static com.lightbend.lagom.javadsl.api.Service.*;


public interface InventoryService extends Service {

  Topic<InventoryEvent> inventoryEvents();
  ServiceCall<ApiDomain.Shipment, Done> provisionItems();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("inventory-service").withCalls(
        pathCall("/api/warehouse/:id/items",  this::provisionItems)
      ).publishing(
        topic("inventory-events", this::inventoryEvents)
          .withProperty(KafkaProperties.partitionKeyStrategy(), InventoryEvent::getName)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
