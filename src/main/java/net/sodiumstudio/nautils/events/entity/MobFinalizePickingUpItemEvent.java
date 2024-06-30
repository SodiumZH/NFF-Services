package net.sodiumstudio.nautils.events.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumstudio.nautils.events.NaUtilsEntityEvent;

/**
 * Posted after mob picking up item. Not cancellable.
 */
public class MobFinalizePickingUpItemEvent extends NaUtilsEntityEvent<Mob>
{
	public final ItemStack itemCopy;
	public MobFinalizePickingUpItemEvent(Mob entity, ItemStack item)
	{
		super(entity);
		this.itemCopy = item;
	}

}
