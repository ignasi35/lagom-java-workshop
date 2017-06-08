package com.example.basket.impl;

import akka.Done;
import com.example.basket.impl.entity.BasketCommand;
import com.example.basket.impl.entity.BasketCommand.*;
import com.example.basket.impl.entity.PEBasketEvent;
import com.example.basket.impl.entity.PEBasketEvent.*;
import com.example.basket.impl.entity.BasketState;
import com.example.basket.impl.entity.NotEnoughItemsException;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;


import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 */
public class BasketEntity extends PersistentEntity<BasketCommand, PEBasketEvent, BasketState> {

    @Override
    public Behavior initialBehavior(Optional<BasketState> snapshotState) {
        if (snapshotState.isPresent()) {
            BasketState state = snapshotState.get();
            if (snapshotState.get().isAlreadyOrdered()) {
                return closedBasket(state);
            } else {
                return openBasket(state);
            }
        } else {
            return openBasket(BasketState.EMTPY_BASKET);
        }
    }


    private Behavior openBasket(BasketState inititalState) {
        BehaviorBuilder b = newBehaviorBuilder(inititalState);

        // ADD ITEM to an open basket
        b.setCommandHandler(AddItem.class, this::addItemCommandHandler);
        b.setEventHandler(ItemAdded.class, itemAdded());

        // REMOVE ITEM from an open basket
        b.setCommandHandler(DeleteItem.class, this::deleteItemCommandHandler);
        b.setEventHandler(ItemDeleted.class, itemDeleted());

        // Get the basket
        b.setReadOnlyCommandHandler(GetBasket.class, (cmd, ctx) -> ctx.reply(state()));


        // CHECK OUT
        b.setCommandHandler(CheckOut.class, this::checkout);
        b.setEventHandlerChangingBehavior(CheckedOut.class, checkedOut());


        return b.build();
    }


    private Behavior closedBasket(BasketState state) {
        PersistentEntity<BasketCommand, PEBasketEvent, BasketState>.BehaviorBuilder b =
                this.newBehaviorBuilder(state);

        // TODO treat all commands gently instead of ignoring all which causes exceptions.
        return b.build();
    }


    private Function<ItemAdded, BasketState> itemAdded() {
        return evt -> state().addItem(evt.getItemId(), evt.getCount());
    }

    private Function<ItemDeleted, BasketState> itemDeleted() {
        return evt -> state().removeItem(evt.getItemId(), evt.getCount());
    }

    private Function<CheckedOut, Behavior> checkedOut(){
        return e -> closedBasket(state());
    }

    private Persist addItemCommandHandler(AddItem cmd, CommandContext ctx) {
        return ctx.thenPersist(
                new PEBasketEvent.ItemAdded(entityuuid(), state(), cmd.getId(), cmd.getCount()),
                (e) -> ctx.reply(Done.getInstance())
        );
    }

    private Persist deleteItemCommandHandler(DeleteItem cmd, CommandContext ctx) {
        if (state().withEnough(cmd.getId(), cmd.getCount())) {
            return ctx.thenPersist(
                    new PEBasketEvent.ItemDeleted(entityuuid(), state(), cmd.getId(), cmd.getCount()),
                    (e) -> ctx.reply(Done.getInstance()));
        } else {
            ctx.commandFailed(new NotEnoughItemsException("Can't remove " + cmd.getCount() + " " + cmd.getId() + " from basket."));
            return ctx.done();
        }
    }

    private Persist checkout(CheckOut cmd, CommandContext ctx) {
        return ctx.thenPersist(new CheckedOut(entityuuid(), state()), (e) -> ctx.reply(Done.getInstance()));
    }


    private UUID entityuuid() {
        return UUID.fromString(entityId());
    }
}
