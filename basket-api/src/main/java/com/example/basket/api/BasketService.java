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

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("basket-service").withCalls(

                // EXERCISE 1: Create endpoints for the methods above
                //     - POST and DELETE items from a Basket
                //     - GET a complete Basket

                // TIPS:
                //      Use `uuidgen` in bash to generate UUIDs
                //      Use this to add items to a basket
                /**
                 curl -X POST \
                      -d '{"count":3, "id":"3666666F-3333-5555-2222-F31111EE0000"}' \
                      http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C/items
                 */
                //      Use this to GET the basket
                /**
                        curl http://localhost:9000/api/basket/3C90204F-2692-4934-B63B-F3E223EEB41C
                 */



                // Ignore things below this points
        ).withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString)
        ).withAutoAcl(true);


        // @formatter:on
    }
}
