package com.example.basket.impl;

import akka.Done;
import com.example.basket.impl.entity.BasketCommand;
import com.example.basket.impl.entity.BasketEvent;
import com.example.basket.impl.entity.BasketState;
import com.example.basket.impl.entity.NotEnoughItemsException;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;


import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public class BasketEntity extends PersistentEntity<BasketCommand, BasketEvent, BasketState> {
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
        PersistentEntity<BasketCommand, BasketEvent, BasketState>.BehaviorBuilder b =
                this.newBehaviorBuilder(inititalState);

        // ADD ITEM to an open basket
        b.setCommandHandler(
                BasketCommand.AddItem.class,
                (cmd, ctx) ->
                        ctx.thenPersist(
                                new BasketEvent.ItemAdded(entityuuid(), cmd.getId(), cmd.getCount()),
                                (e) -> ctx.reply(Done.getInstance())
                        )
        );
        b.setEventHandler(BasketEvent.ItemAdded.class,
                evt -> state().addItem(evt.getItemId(), evt.getCount())
        );

        // REMOVE ITEM from an open basket
        b.setCommandHandler(
                BasketCommand.DeleteItem.class,
                (cmd, ctx) -> {
                    if (state().withEnough(cmd.getId(), cmd.getCount())) {
                        return ctx.thenPersist(
                                new BasketEvent.ItemDeleted(entityuuid(), cmd.getId(), cmd.getCount()),
                                (e) -> ctx.reply(Done.getInstance()));
                    } else {
                        ctx.commandFailed(new NotEnoughItemsException("Can't remove " + cmd.getCount() + " " + cmd.getId() + " from basket."));
                        return ctx.done();
                    }
                }
        );
        b.setEventHandler(BasketEvent.ItemDeleted.class,
                evt -> state().removeItem(evt.getItemId(), evt.getCount())
        );


        // Get the basket
        b.setReadOnlyCommandHandler(
                BasketCommand.GetBasket.class,
                (cmd, ctx) -> ctx.reply(state())
        );

        return b.build();
    }

    private Behavior closedBasket(BasketState state) {
        PersistentEntity<BasketCommand, BasketEvent, BasketState>.BehaviorBuilder b =
                this.newBehaviorBuilder(state);
        return b.build();
    }


    private UUID entityuuid() {
        return UUID.fromString(entityId());
    }
}
