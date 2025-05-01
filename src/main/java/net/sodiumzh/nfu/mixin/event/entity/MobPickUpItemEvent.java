package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Posted when a {@code Mob} is picking up an {@code ItemEntity}.
 * <p>Cancellable. If cancelled, the picking action will be omitted.
 */
@Cancelable
public class MobPickUpItemEvent extends NFULivingEvent<Mob>
{
	public final ItemEntity item;
	public MobPickUpItemEvent(Mob entity, ItemEntity item)
	{
		super(entity);
		this.item = item;
	}

}
