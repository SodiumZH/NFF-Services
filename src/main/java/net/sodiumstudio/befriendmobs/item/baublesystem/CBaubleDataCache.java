package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.nautils.NbtHelper;

public interface CBaubleDataCache
{

	public IBaubleEquipable getHolder();
	
	public CompoundTag getNbt();

	public HashSet<IBaubleEquipable.TransientModifierInfo> transientModifiers();
		
	public default HashMap<String, ItemStack> getCachedStacks() {
		HashMap<String, ItemStack> map = new HashMap<String, ItemStack>();
		for (String key: getNbt().getCompound("stacks").getAllKeys())
		{
			map.put(key, NbtHelper.readItemStack(getNbt().getCompound("stacks"), key));
		}
		return map;
	}
	
	public default void write()
	{
		getNbt().put("stacks", new CompoundTag());
		for (String key: getHolder().getBaubleSlots().keySet())
		{
			NbtHelper.saveItemStack(getHolder().getBaubleSlots().get(key), getNbt().getCompound("stacks"), key);
		}
	}	
	
	public default boolean hasSlotChanged(String key)
	{
		ItemStack stack = getHolder().getBaubleSlots().get(key);
		if (stack == null)
			stack = ItemStack.EMPTY;
		ItemStack old = getCachedStacks().get(key);
		if (old == null)
			old = ItemStack.EMPTY;
		return !stack.equals(old, false);
		
	}
	
	public static class Impl implements CBaubleDataCache
	{

		protected CompoundTag nbt = new CompoundTag();
		public final IBaubleEquipable holder;
		public final HashSet<IBaubleEquipable.TransientModifierInfo> transientModifiers 
			= new HashSet<IBaubleEquipable.TransientModifierInfo>();
		
		public Impl(IBaubleEquipable holder)
		{
			this.holder = holder;
			nbt.put("stacks", new CompoundTag());
		}
		
		@Override
		public CompoundTag getNbt() {
			return nbt;
		}

		@Override
		public IBaubleEquipable getHolder() {
			return holder;
		}
		
		@Override
		public HashSet<IBaubleEquipable.TransientModifierInfo> transientModifiers()
		{
			return transientModifiers;
		}
		
	}
	
	public static class Prvd implements ICapabilityProvider
	{

		protected final IBaubleEquipable holder;
		protected CBaubleDataCache cap;
		
		public Prvd(IBaubleEquipable holder)
		{
			this.holder = holder;
			cap = new CBaubleDataCache.Impl(this.holder);
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if(cap == BMCaps.CAP_BAUBLE_DATA_CACHE)
				return LazyOptional.of(() -> {return this.cap;}).cast();
			else
				return LazyOptional.empty();
		}
		
	}
	
}
