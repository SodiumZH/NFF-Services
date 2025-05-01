package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumzh.nfu.eventhandler.NFUSetupEventHandlers;

import java.util.function.Supplier;

/**
 *  Register entity attributes that will be merged into the Forge attribute registry on server start instead of mod
 *  loading. As this registering uses suppliers, you can use some values that are not available on mod setup phase (e.g. config).
 *  <p>This feature is enabled by {@link DeferredEntityAttributes} which holds and registers the table, and
 *  {@link NFUSetupEventHandlers#registerDeferredAttributeSuppliers} which posts the registration event.
 *  <p>Note that this event is posted during the mod setup phase, but applied on server start.
 */
public class DeferredEntityAttributeRegisterEvent extends Event implements IModBusEvent {

    /**
     * Register a deferred entity attribute supplier which will be added on server start instead of mod setup. This
     * allows to use values that are inaccessible on setup (e.g. config values) for entity attributes.
     * <p>Note that the attributes registered here will NOT be accessible before server start.
     */
    public void put(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> supplier)
    {
        DeferredEntityAttributes.put(type, supplier);
    }

}
