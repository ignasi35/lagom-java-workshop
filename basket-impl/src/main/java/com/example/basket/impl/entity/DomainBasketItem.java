package com.example.basket.impl.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

@Value
public class DomainBasketItem implements Jsonable {
  private UUID id;
  private int numberOfItems;

  @JsonCreator
  public DomainBasketItem(UUID id, int numberOfItems) {
    this.id = id;
    this.numberOfItems = numberOfItems;
  }
}
