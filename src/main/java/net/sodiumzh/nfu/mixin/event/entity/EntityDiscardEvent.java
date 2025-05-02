package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.event.NFUEntityEvent;

/**
 * Called when an entity is about to be removed by calling discard(). Not cancellable.
 * <p>Note: not all entity removals are "discard". "Discard" usually happens when am entity is
 * permanently removed but not killed, like item entity expiring, mob conversion, or manual
 * discard() call.
 */
public class EntityDiscardEvent extends NFUEntityEvent<Entity> {

    public EntityDiscardEvent(Entity entity) {
        super(entity);
    }

}
