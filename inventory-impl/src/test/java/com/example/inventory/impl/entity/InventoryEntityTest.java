package com.example.inventory.impl.entity;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.example.inventory.api.InventoryEvent;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.*;
import com.example.inventory.impl.entity.InventoryCommand.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


import java.util.UUID;


public class InventoryEntityTest {


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
    public void increaseInventory() {

        PersistentEntityTestDriver<InventoryCommand, PEInventoryEvent, InventoryState> driver =
                new PersistentEntityTestDriver<>(system, new InventoryEntity(), "test-id");

        Outcome<PEInventoryEvent, InventoryState> outcome =
                driver.run(new IncreaseInventory("oranges", UUID.randomUUID(), 1000));

        assertEquals( new Integer(1000), outcome.getReplies().get(0));
    }

    @Test
    public void decreaseInventory() {
        PersistentEntityTestDriver<InventoryCommand, PEInventoryEvent, InventoryState> driver =
                new PersistentEntityTestDriver<>(system, new InventoryEntity(), "test-id");

        UUID orangesId = UUID.randomUUID();
        IncreaseInventory add1000 = new IncreaseInventory("oranges", orangesId, 1000);
        DecreaseInventory remove100 = new DecreaseInventory(orangesId, 100);
        Outcome<PEInventoryEvent, InventoryState> outcome =
                driver.run(add1000, remove100);

        assertEquals( new Integer(1000), outcome.getReplies().get(0));
        assertEquals( new Integer(900), outcome.getReplies().get(1));
    }

    @Test
    public void decreaseInventoryBelowStock() {
        PersistentEntityTestDriver<InventoryCommand, PEInventoryEvent, InventoryState> driver =
                new PersistentEntityTestDriver<>(system, new InventoryEntity(), "test-id");

        UUID orangesId = UUID.randomUUID();
        IncreaseInventory add10 = new IncreaseInventory("oranges", orangesId, 10);
        DecreaseInventory remove11 = new DecreaseInventory(orangesId, 100);
        Outcome<PEInventoryEvent, InventoryState> outcome =
                driver.run(add10, remove11);

        assertEquals( new Integer(10), outcome.getReplies().get(0));
        assertEquals( new InsuffucientStock(orangesId), outcome.getReplies().get(1));
    }

}
