package net.sodiumzh.nff.services.subsystems.baublesystem;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nautils.annotation.DontOverride;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;

/**
 * Capability for all mobs that can equip baubles. Mobs registered in {@link RegisterBaubleEvent} will be automatically added this capability.
 */
interface CBaubleEquippableMob
{
	/**
	 * Get mob using this capability.
	 */
	public Mob getMob();

	/**
	 * Get the slot accessor.
	 */
	public BaubleSlotAccessor getBaubleSlotAccessor();
	
	
	// Modifier management
	
	/**
	 * Set of active attribute modifiers.
	 */
	public Set<BaubleAttributeModifier> getModifiers();

	/**
	 * Tick the modifiers.
	 */
	public void modifierTick();
	
	// Processing
	
	/**
	 * Invoked when the bauble item changes. Only once per tick.
	 */
	public void onSlotChange();
	
	/**
	 * Invoked once before slots tick.
	 */
	public void preTick();
	
	/**
	 * Invoked on each slot tick.
	 * @param args Slot info.
	 */
	public void slotTick(BaubleProcessingArgs args);
	
	/**
	 * Invoked once after slots tick.
	 */
	public void postTick();
	
	/**
	 * Directly invoked on mob tick.
	 */
	public void tick();

	/**
	 * Check if this capability instance is a valid capability (i.e. {@link CBaubleEquippableMobImpl})
	 * rather than an empty dummy (i.e. {@link CBaubleEquippableMobEmptyImpl}).
	 */
	@DontOverride
	public default boolean isValid()
	{
		return this instanceof CBaubleEquippableMobImpl;
	}
	
	// Utilities
	
	/**
	 * Get the capability. If it isn't present, return an empty instance.
	 */
	@Nonnull
	static CBaubleEquippableMob getCapability(Mob mob)
	{
		return NaUtilsMiscStatics.getValueOrDefault(mob.getCapability(BaubleSystemCapabilities.CAP_BAUBLE_EQUIPPABLE_MOB), 
				() -> new CBaubleEquippableMobEmptyImpl(mob));
	}
}
