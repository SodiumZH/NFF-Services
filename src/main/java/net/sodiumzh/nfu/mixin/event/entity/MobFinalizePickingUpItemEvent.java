package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.event.NFUEntityEvent;

/**
 * Posted after mob picking up item. Not cancellable.
 */
public class MobFinalizePickingUpItemEvent extends NFUEntityEvent<Mob>
{
	public final ItemStack itemCopy;
	public MobFinalizePickingUpItemEvent(Mob entity, ItemStack item)
	{
		super(entity);
		this.itemCopy = item;
	}

}
