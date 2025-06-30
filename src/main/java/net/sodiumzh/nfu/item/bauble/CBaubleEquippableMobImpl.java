package net.sodiumzh.nfu.item.bauble;

import java.util.*;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

class CBaubleEquippableMobImpl implements CBaubleEquippableMob
{

	private final Mob mob;
	
	public CBaubleEquippableMobImpl(Mob mob)
	{
		this.mob = mob;
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}

	private BaubleSlotAccessor accessors = null;
	
	@Override
	public BaubleSlotAccessor getBaubleSlotAccessor()
	{
		if (accessors == null)
			accessors = new BaubleSlotAccessor(this);
		return accessors;
	}

	// Modifiers
	
	/** Key: modifier; Value: source slot key. */
	private HashSet<BaubleAttributeModifier> modifiers = new HashSet<>();
	
	@Override
	public Set<BaubleAttributeModifier> getModifiers()
	{
		return modifiers;
	}
	
	@Override
	public void modifierTick()
	{
		getModifiers().forEach(BaubleAttributeModifier::tick);
	}
	
	private void refreshSlotModifiers()
	{
		modifiers.forEach(BaubleAttributeModifier::stopApplying);
		modifiers.clear();
		// Collect all entries to be applied with no duplication, for adding unrepeatable modifiers
		HashSet<IBaubleRegistryEntry> allMatchedEntries = new HashSet<>();
		for (String key: getBaubleSlotAccessor().getAccessors().keySet())
		{
			BaubleRegistries.forEachMatchedEntry(this, key, allMatchedEntries::add);
		}
		Map<ResourceLocation, BaubleAttributeModifier> modifiersWithID = new HashMap<>();
		// Iterate all unrepeatable modifiers
		allMatchedEntries.forEach(entry ->
		{
			BaubleAttributeModifier[] mods = entry.getUnrepeatableModifiers(this.getMob());
			if (mods != null)
			{
				// Non-IDed modifiers are directly applied, IDed modifiers are collected
				for (int i = 0; i < mods.length; ++i)
				{
					ResourceLocation id = mods[i].getAdditionalID().orElse(null);
					if (id != null) {
						if (!modifiersWithID.containsKey(id))
							modifiersWithID.put(id, mods[i]);
						// Check if the modifiers are compatible. Crash if not.
						else if (mods[i].getAttribute() != modifiersWithID.get(id).getAttribute()
							|| mods[i].getOperation() != modifiersWithID.get(id).getOperation()
							// As predicates are hard to check equality, just check if they're present here
							|| (mods[i].getAdditionalCondition() == null) == (modifiersWithID.get(id).getAdditionalCondition() != null)) {
							throw new UnsupportedOperationException("CBaubleEquippableMob: Incompatible modifiers with the same ID.");
						}
						// Overwrite if larger
						else if (mods[i].getAmount() > modifiersWithID.get(id).getAmount()) {
							modifiersWithID.put(id, mods[i]);
						}
					}
					// Modifiers without ID are directly applied.
					else mods[i].startApplying(new BaubleProcessingArgs(null, this, null));
				}
			}
		});
		// Finally apply IDed modifiers
		modifiersWithID.values().forEach(m -> m.startApplying(new BaubleProcessingArgs(null, this, null)));
		// Then add repeatable modifiers by slots
		for (String key: getBaubleSlotAccessor().getAccessors().keySet())
		{
			BaubleRegistries.forEachMatchedEntry(this, key, entry -> 
			{
				BaubleAttributeModifier[] mods = entry.getRepeatableModifiers(new BaubleProcessingArgs(getBaubleSlotAccessor().getItemStack(key),
						this, key));
				if (mods != null)
				{
					for (int i = 0; i < mods.length; ++i)
					{
						mods[i].startApplying(new BaubleProcessingArgs(this.getBaubleSlotAccessor().getItemStack(key), this, key));
					}
				}
			});
		}
	}
	
	// Processing
	
	private HashMap<String, ItemStack> previousStacks = new HashMap<>();
	private HashMap<String, ItemStack> currentStacks = new HashMap<>();
	
	// Get and update current ItemStacks. Invoked in the VERY BEGINNING of tick.
	private void updateStacks()
	{
		previousStacks.clear();
		for (var entry: currentStacks.entrySet())
		{
			previousStacks.put(entry.getKey(), entry.getValue().copy());
		}
		currentStacks.clear();
		for (var entry: this.getBaubleSlotAccessor().getAccessors().entrySet())
		{
			currentStacks.put(entry.getKey(), entry.getValue().apply(this.getMob()).copy());
		}
	}
	
	// Invoked on tick to check if modifiers should be refreshed on this tick.
	@Deprecated
	private boolean shouldRefreshModifiers()
	{
		for (String key: previousStacks.keySet())
		{
			if (!currentStacks.containsKey(key) || currentStacks.get(key) == null)
				return true;
		}
		for (String key: currentStacks.keySet())
		{
			if (!previousStacks.containsKey(key) || previousStacks.get(key) == null)
				return true;
			else if (!currentStacks.get(key).equals(previousStacks.get(key), false))
				return true;
		}
		return false;
	}
	
	/**
	 * Invoked on tick, get slots of which the content changed in this tick.
	 */
	private Set<String> getChangedSlots()
	{
		Set<String> set = new HashSet<>();
		for (String key: previousStacks.keySet())
		{
			if (!currentStacks.containsKey(key) || currentStacks.get(key) == null)
				set.add(key);
		}
		for (String key: currentStacks.keySet())
		{
			if (!previousStacks.containsKey(key) || previousStacks.get(key) == null)
				set.add(key);
			else if (!currentStacks.get(key).equals(previousStacks.get(key), false))
				set.add(key);
		}
		return set;
	}
	
	
	@Override
	public void onSlotChange() {
		refreshSlotModifiers();
	}
	
	@Override
	public void preTick()
	{
		HashSet<IBaubleRegistryEntry> tickedEntries = new HashSet<>();
		for (String slotKey: getBaubleSlotAccessor().getAccessors().keySet())
		{
			BaubleRegistries.forEachMatchedEntry(this, slotKey, entry -> 
			{
				if (!tickedEntries.contains(entry))
				{
					try {
						entry.preSlotTick(new BaubleProcessingArgs(null, this, slotKey));
						tickedEntries.add(entry);
					}
					catch (NullPointerException e)
					{
						LogUtils.getLogger().error("Null Pointer in IBaubleRegistryEntry#preSlotTick. If it's from "
								+ "the input BaubleProcessingArgs#baubleItemStack, note that in preSlotTick it's invalid.");
						throw e;
					}
				}
			});
		}
	}
	
	@Override
	public void slotTick(BaubleProcessingArgs args)
	{
		BaubleRegistries.forEachMatchedEntry(args.getCapability(), args.slotKey(), entry -> entry.slotTick(args));
	}
	
	@Override
	public void postTick()
	{
		HashSet<IBaubleRegistryEntry> tickedEntries = new HashSet<>();
		for (String slotKey: getBaubleSlotAccessor().getAccessors().keySet())
		{
			BaubleRegistries.forEachMatchedEntry(this, slotKey, entry -> 
			{
				if (!tickedEntries.contains(entry))
				{
					try {
						entry.preSlotTick(new BaubleProcessingArgs(null, this, slotKey));
						tickedEntries.add(entry);
					}
					catch (NullPointerException e)
					{
						LogUtils.getLogger().error("Null Pointer in IBaubleRegistryEntry#postSlotTick. If it's from "
								+ "the input BaubleProcessingArgs#baubleItemStack, note that in postSlotTick it's invalid.");
						throw e;
					}
				}
			});
		}
	}
	
	@SuppressWarnings("resource")
	@Override
	public void tick()
	{
		if (this.getMob().getLevel().isClientSide)
			return;
		updateStacks();
		Set<String> changedSlots = this.getChangedSlots();
		if (changedSlots.size() > 0)
		{
			refreshSlotModifiers();
			for (String key: changedSlots)
			{
				BaubleRegistries.forEachMatchedEntry(this, key, entry -> entry.onEquipped(new BaubleProcessingArgs(currentStacks.get(key), this, key)));
			}
		}
		this.modifierTick();
		MinecraftForge.EVENT_BUS.post(new BaubleEquippableMobTickEvent.PreTick(mob, this));
		this.preTick();
		MinecraftForge.EVENT_BUS.post(new BaubleEquippableMobTickEvent.PreSlotTick(mob, this));
		for (String key: accessors.getAccessors().keySet())
		{
			this.slotTick(new BaubleProcessingArgs(accessors.getItemStack(key), this, key));
		}
		MinecraftForge.EVENT_BUS.post(new BaubleEquippableMobTickEvent.PostSlotTick(mob, this));
		this.postTick();
		MinecraftForge.EVENT_BUS.post(new BaubleEquippableMobTickEvent.PostTick(mob, this));
	}


}
