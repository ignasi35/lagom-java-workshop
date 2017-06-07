package com.example.basket.impl;

import static org.junit.Assert.assertEquals;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.example.basket.impl.entity.BasketCommand;
import com.example.basket.impl.entity.BasketEvent;
import com.example.basket.impl.entity.BasketState;
import com.example.basket.impl.entity.DomainBasketItem;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Collections;
import java.util.UUID;

public class BasketEntityTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("BasketEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }


    @Test
    public void addItems() {
        UUID bid = UUID.randomUUID();
        UUID orangeId = UUID.randomUUID();
        UUID bananaId = UUID.randomUUID();


        BasketCommand.AddItem cmd1 = new BasketCommand.AddItem(orangeId, 3);
        BasketCommand.AddItem cmd2 = new BasketCommand.AddItem(bananaId, 1);
        BasketCommand.GetBasket cmd3 = BasketCommand.GetBasket.INSTANCE;

        PersistentEntityTestDriver<BasketCommand, BasketEvent, BasketState> driver =
                new PersistentEntityTestDriver<>(system, new BasketEntity(), bid.toString());


        // add some oranges
        Outcome<BasketEvent, BasketState> outcome1 = driver.run(cmd1);
        assertEquals(Done.getInstance(), outcome1.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome1.issues());

        // and then add some bananas. Finally get the basket.
        Outcome<BasketEvent, BasketState> outcome3 = driver.run(cmd2, cmd3);

        PSequence<DomainBasketItem> items = TreePVector.<DomainBasketItem>empty()
                .plus(new DomainBasketItem(orangeId, 3))
                .plus(new DomainBasketItem(bananaId, 1));
        BasketState expected = new BasketState(items, false);
        assertEquals(expected, outcome3.getReplies().get(1));
        assertEquals(Collections.emptyList(), outcome1.issues());

    }
}
