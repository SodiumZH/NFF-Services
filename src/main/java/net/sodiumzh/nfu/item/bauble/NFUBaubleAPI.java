package net.sodiumzh.nfu.item.bauble;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.handler.ipfilter.IpSubnetFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.registry.NFURegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * NFFServices - BaubleSystem is a system for equipping "bauble" items on <b>mobs</b> just like for players in Curios API.
 * <p>This class involves some common static methods.
 */
public class NFUBaubleAPI
{
	public static void init() {}

	public static final NFURegistry<BaubleEquippingCondition> EQUIPPING_CONDITIONS
		= new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "bauble_equipping_conditions"));

	/**
	 * Get the Bauble-handling capability of a mob.
	 * @return Bauble-handling capability ({@link CBaubleEquippableMob}).
	 * If the mob isn't bauble-equippable (not registered or is pending removal, etc.), return an empty instance that won't do anything. 
	 */
	@Nonnull
	static CBaubleEquippableMob getCapability(Mob mob)
	{
		return CBaubleEquippableMob.getCapability(mob);
	}

	/**
	 * Get the Bauble-handling capability of a mob.
	 * @return Bauble-handling capability ({@link CBaubleEquippableMob}).
	 * If the mob isn't bauble-equippable (not registered or is pending removal, etc.), return an empty instance that won't do anything.
	 */
	@Nonnull
	static Optional<CBaubleEquippableMob> getOptionalCapability(Mob mob)
	{
		return CBaubleEquippableMob.getOptionalCapability(mob);
	}

	/**
	 * Check if a mob has the Bauble-handling capability ({@link CBaubleEquippableMob}).
	 */
	public static boolean isCapabilityPresent(Mob mob)
	{
		return getOptionalCapability(mob).isPresent();
	}
	
	/**
	 * Do something if a mob has the Bauble-handling capability ({@link CBaubleEquippableMob}).
	 */
	public static void ifCapabilityPresent(Mob mob, Consumer<Mob> action)
	{
		getOptionalCapability(mob).ifPresent(m -> action.accept(m.getMob()));
	}
	
	/**
	 * Get the current ItemStack equipped on the given slot key <b>as a copy</b>. Empty if the slot isn't present.
	 */
	@Nonnull
	public static ItemStack getSlotItem(Mob mob, String slotKey)
	{
		CBaubleEquippableMob cap = getCapability(mob);
		return cap.getBaubleSlotAccessor().hasSlot(slotKey) ? cap.getBaubleSlotAccessor().getItemStack(slotKey) : ItemStack.EMPTY;
	}
	
	/**
	 * Get all slot keys and equipping items.
	 */
	@Nonnull
	public static Map<String, ItemStack> getAllSlotItems(Mob mob)
	{
		//Map<String, ItemStack> map = new HashMap<>();
		return getOptionalCapability(mob).map(c -> c.getBaubleSlotAccessor().getItemStacks()).orElseGet(HashMap::new);
	}
	
	/**
	 * Do an operation to all bauble registry entries that should take effect to a given slot of a given mob.
	 */
	public static void forEachMatchedEntry(Mob mob, String slot, Consumer<IBaubleRegistryEntry> operationForEach)
	{
		getOptionalCapability(mob).ifPresent(m -> BaubleRegistries.forEachMatchedEntry(m, slot, operationForEach));
	}
	
	/**
	 * Get all entries which the input ItemStack is in the definitions, despite the mob and the slot.
	 */
	public static Set<IBaubleRegistryEntry> getAllRelatedEntries(ItemStack itemstack)
	{
		return BaubleRegistries.getAllRelatedEntries(itemstack);
	}
	
	/**
	 * Check if an ItemStack can be equipped on a given mob into a given slot.
	 */
	public static boolean canEquipOn(ItemStack itemstack, Mob mob, String slot)
	{
		return BaubleRegistries.canEquipOn(itemstack, mob, slot);
	}
}
