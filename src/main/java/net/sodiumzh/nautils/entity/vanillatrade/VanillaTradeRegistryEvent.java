package net.sodiumzh.nautils.entity.vanillatrade;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class VanillaTradeRegistryEvent extends Event {

    private final VanillaTradeRegistry registry;

    public VanillaTradeRegistryEvent(VanillaTradeRegistry registry) { this.registry = registry; }
}
