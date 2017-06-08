package com.example.basket.api;

import akka.Done;
import akka.NotUsed;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.UUID;

import static com.lightbend.lagom.javadsl.api.Service.*;


public interface BasketService extends Service {

    ServiceCall<NotUsed, Basket> getBasket(UUID id);

    ServiceCall<BasketItem, Done> addItem(UUID id);

    ServiceCall<BasketItem, Done> deleteItem(UUID id);

    Topic<BasketEvent> basketEvents();

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("basket-service").withCalls(
                restCall(Method.POST, "/api/basket/:id/items", this::addItem),
                restCall(Method.DELETE, "/api/basket/:id/items", this::deleteItem),
                pathCall("/api/basket/:id", this::getBasket)
        ).publishing(
                topic("hello-events", this::basketEvents)
                        .withProperty(KafkaProperties.partitionKeyStrategy(), BasketEvent::getId)
        ).withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
