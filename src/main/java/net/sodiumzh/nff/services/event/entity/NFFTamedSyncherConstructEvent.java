package net.sodiumzh.nff.services.event.entity;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.entity.taming.NFFTamedDataComponent;
import net.sodiumzh.nff.services.entity.taming.NFFTamedSyncherComponent;
import net.sodiumzh.nfu.entity.component.EntityComponentAPI;
import net.sodiumzh.nfu.entity.component.EntityComponentEvent;

/**
 * Post on {@link NFFTamedSyncherComponent} initialization.
 * <p>Note: this event is for synched data. For non-synched data, use {@link NFFTamedDataConstructEvent}.
 */
public class NFFTamedSyncherConstructEvent extends EntityComponentEvent<Mob, NFFTamedSyncherComponent> {
    public NFFTamedSyncherConstructEvent(Mob entity, NFFTamedSyncherComponent component) {
        super(entity, component);
    }
}
