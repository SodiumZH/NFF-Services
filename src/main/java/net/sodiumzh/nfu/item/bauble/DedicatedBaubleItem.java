package net.sodiumzh.nfu.item.bauble;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.item.NFUItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiPredicate;

/**
 * Base class for dedicated items as baubles. It's behaviors can be defined in subclasses and can be directly registered to NFUBaubleAPI.
 */
public abstract class DedicatedBaubleItem extends NFUItem implements IBaubleRegistryEntry
{

	public DedicatedBaubleItem(Properties pProperties)
	{
		super(pProperties);
	}

	@Override
	public Item getItem()
	{
		return this;
	}
	
	@Override
	public BiPredicate<Item, ItemStack> getMultiItemCondition() {
		return null;
	}
	
	@Override
	@Nonnull
	public BaubleEquippingCondition getEquippingCondition()
	{
		return BaubleEquippingConditions.CONDITION_ALWAYS.get();
	}

	@Override
	public void onEquipped(BaubleProcessingArgs args) {}

	@Override
	public void preSlotTick(BaubleProcessingArgs args) {}

	@Override
	public void postSlotTick(BaubleProcessingArgs args) {}

	@Override
	@Nullable
	public BaubleAttributeModifier[] getUnrepeatableModifiers(Mob mob) {return null;}
	
}
