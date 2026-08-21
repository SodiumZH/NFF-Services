package net.sodiumzh.nff.services.event.entity;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.entity.taming.NFFTamableDataComponent;
import net.sodiumzh.nff.services.entity.taming.NFFTamedDataComponent;
import net.sodiumzh.nfu.entity.component.EntityComponentEvent;

/**
 * Post on {@link NFFTamedDataComponent} initialization.
 * <p>Note: this event is for non-synched data. For synched data, use {@link NFFTamedSyncherConstructEvent}.
 */
public class NFFTamedDataConstructEvent extends EntityComponentEvent<Mob, NFFTamableDataComponent> {
    public NFFTamedDataConstructEvent(Mob entity, NFFTamableDataComponent component) {
        super(entity, component);
    }
}
