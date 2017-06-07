package com.example.basket.impl;

import com.example.basket.api.BasketService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * The module that binds the HelloService so that it can be served.
 */
public class BasketModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(BasketService.class, BasketServiceImpl.class);
  }
}
