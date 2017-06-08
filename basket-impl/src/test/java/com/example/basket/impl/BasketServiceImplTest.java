package com.example.basket.impl;

import akka.Done;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.example.basket.api.BasketService;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;

public class BasketServiceImplTest {

    UUID basketId = UUID.randomUUID();

    @Test
    @SuppressWarnings("unchecked")
    public void integrationTest() {
        withServer(defaultSetup().withCassandra(true), server ->
                {
                    BasketService service = server.client(BasketService.class);

                    UUID oid = UUID.randomUUID();
                    UUID aid = UUID.randomUUID();
                    UUID bid = UUID.randomUUID();
                    BasketItem oranges = new BasketItem(oid, 2);
                    BasketItem apples = new BasketItem(aid, 2);
                    BasketItem bananas = new BasketItem(bid, 2);

                    CompletionStage<Done> basketSetup =
                            service.addItem(basketId).invoke(oranges).thenCompose(x ->
                                    service.addItem(basketId).invoke(apples).thenCompose(y ->
                                            service.addItem(basketId).invoke(bananas)
                                    )
                            );
                    CompletionStage<Basket> basketCompletionStage =
                            basketSetup.thenCompose(x -> service.getBasket(basketId).invoke());
                    Basket basket =
                            basketCompletionStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

                    Basket expected = Basket.with(basketId, oranges, apples, bananas);

                    Assert.assertEquals(expected, basket);
                }
        );
    }

}
