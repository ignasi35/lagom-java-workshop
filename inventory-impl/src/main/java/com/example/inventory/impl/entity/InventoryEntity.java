package com.example.inventory.impl.entity;

import com.example.inventory.impl.entity.InventoryCommand.DecreaseInventory;
import com.example.inventory.impl.entity.InventoryCommand.IncreaseInventory;
import com.example.inventory.impl.entity.PEInventoryEvent.PEInventoryDecreased;
import com.example.inventory.impl.entity.PEInventoryEvent.PEInventoryIncreased;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

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
        b.setEventHandler(PEInventoryIncreased.class, (evt) -> state().increase(evt.getByHowMuch()));
        b.setEventHandler(PEInventoryDecreased.class, (evt) -> state().increase(-evt.getByHowMuch()));
        return b.build();
    }


    private Persist increaseInventory(IncreaseInventory cmd, CommandContext ctx) {
        int countAfterIncreasing = cmd.getCount() + state().getCurrentCount();
        PEInventoryIncreased evt = new PEInventoryIncreased(
                cmd.getCount(),
                countAfterIncreasing,
                cmd.getItemId());
        return ctx.thenPersist(evt, e -> ctx.reply(evt.getCountAfterIncreasing()));
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
