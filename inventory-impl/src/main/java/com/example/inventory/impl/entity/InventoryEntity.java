package com.example.inventory.impl.entity;

import akka.Done;
import akka.japi.Effect;
import com.example.inventory.impl.entity.InventoryCommand.*;
import com.example.inventory.impl.entity.PEInventoryEvent.*;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * tracks the quantity of instances of an item in a given warehouse. The location is relevant because some items may
 * be available but on locations that make it unfeasible to complete the delivery given the customer location.
 * <p>
 * Also, a shipment only arrives at a single location on a given time.
 */
@SuppressWarnings("unchecked")
public class InventoryEntity extends PersistentEntity<InventoryCommand, PEInventoryEvent, InventoryState> {
    @Override
    public Behavior initialBehavior(Optional<InventoryState> snapshotState) {

        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(new InventoryState()));
        b.setCommandHandler(IncreaseInventory.class, this::increaseInventory);
        b.setCommandHandler(DecreaseInventory.class, this::decreaseInventory);
        b.setEventHandler(PEInventoryItemLabelled.class, (evt) -> state().rename(evt.getItemName()));
        b.setEventHandler(PEInventoryIncreased.class, (evt) -> state().increase(evt.getByHowMuch()));
        b.setEventHandler(PEInventoryDecreased.class, (evt) -> state().increase(-evt.getByHowMuch()));
        return b.build();
    }


    private Persist increaseInventory(IncreaseInventory cmd, CommandContext ctx) {
        PEInventoryIncreased evt =
                new PEInventoryIncreased(cmd.getCount(), cmd.getCount() + state().getCurrentCount(), cmd.getItemId());
        if (!state().getName().isPresent()) {
            List<PEInventoryEvent> list = Arrays.asList(
                    new PEInventoryItemLabelled(cmd.getName(), cmd.getItemId()),
                    evt);
            Effect effect = () -> ctx.reply(evt.getCountAfterIncreasing());
            return ctx.thenPersistAll(list, effect);
        } else {
            return ctx.thenPersist(evt, e -> ctx.reply(evt.getCountAfterIncreasing()));

        }
    }

    private Persist decreaseInventory(DecreaseInventory cmd, CommandContext ctx) {

        if (state().getCurrentCount() < cmd.getCount()) {
            System.out.println(" INSUFFICIENT STOCK!!!");
            ctx.commandFailed(new InsuffucientStock(cmd.getItemId()));
            return ctx.done();
        } else {
            int countAfterDecreasing = state().getCurrentCount() - cmd.getCount();
            PEInventoryDecreased evt =
                    new PEInventoryDecreased(
                            cmd.getCount(),
                            countAfterDecreasing,
                            cmd.getItemId());
            return ctx.thenPersist(evt, e -> ctx.reply(evt.getCountAfterDecreasing()));
        }

    }

}
