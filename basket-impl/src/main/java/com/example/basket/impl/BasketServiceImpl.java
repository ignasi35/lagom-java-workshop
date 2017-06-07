package com.example.basket.impl;

import akka.Done;
import akka.NotUsed;
import com.example.basket.api.ApiDomain.Basket;
import com.example.basket.api.ApiDomain.BasketItem;
import com.example.basket.api.BasketEvent;
import com.example.basket.api.BasketService;
import com.example.basket.impl.entity.BasketCommand;
import com.example.basket.impl.entity.BasketCommand.*;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the HelloService.
 */
public class BasketServiceImpl implements BasketService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  @Inject
  public BasketServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(com.example.basket.impl.BasketEntity.class);
  }

  @Override
  public ServiceCall<NotUsed, Basket> getBasket(UUID id) {
    return request -> refFor(id).ask(GetBasket.INSTANCE).thenApply(
      state -> {
        List<BasketItem> items = state.getItems().stream().map(dbi -> new BasketItem(dbi.getId(), dbi.getNumberOfItems())).collect(Collectors.toList());
        return new Basket(id, TreePVector.from(items));
      }
    ) ;
  }

  @Override
  public ServiceCall<BasketItem, Done> addItem(UUID id) {
    return item -> refFor(id).ask(new AddItem(item.getId(), item.getCount()));
  }

  @Override
  public ServiceCall<BasketItem, Done> deleteItem(UUID id) {
    return item -> refFor(id).ask(new AddItem(item.getId(), item.getCount()));
  }

//  @Override
  public Topic<BasketEvent> basketEvents() {
    return null;
  }

  // --------------------
  private PersistentEntityRef<BasketCommand> refFor(UUID id){
    return persistentEntityRegistry.refFor(BasketEntity.class, id.toString());
  }
}
