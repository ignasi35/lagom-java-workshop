package com.example.inventory.impl;

import com.example.inventory.api.InventoryService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 *
 */
public class InventoryModule extends AbstractModule implements ServiceGuiceSupport{
    @Override
    protected void configure() {
        bindService(InventoryService.class, InventoryServiceImpl.class);
    }
}
