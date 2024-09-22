package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nautils.Wrapped;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

/**
 * An additional serializable module attached on players provided by NFF.
 */
public interface CNFFPlayerModule extends INBTSerializable<CompoundTag>
{

	public Player getPlayer();
	public CompoundTag getNbt();
	
	/**
	 * Check if the player has a one-tick tag (a tag that will be auto-removed after tick end)
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public boolean hasOneTickTag(String key);
	
	/**
	 * Check if the player has a one-tick tag (a tag that will be auto-removed after tick end)
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public static boolean hasOneTickTag(Player player, String key)
	{
		Wrapped<Boolean> res = new Wrapped<>(false);
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent((c) -> 
		{
			res.set(c.hasOneTickTag(key));
		});
		return res.get();
	}
	
	/**
	 * Add a one-tick tag (a tag that will be auto-removed after tick end.
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public void addOneTickTag(String key);
	
	/**
	 * Add a one-tick tag (a tag that will be auto-removed after tick end.
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public static void addOneTickTag(Player player, String key)
	{
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent((c) -> 
		{
			c.addOneTickTag(key);
		});
	}
	
	/**
	 * Remove a one-tick tag (a tag that will be auto-removed after tick end)
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public void removeOneTickTag(String key);
	
	/**
	 * Remove a one-tick tag (a tag that will be auto-removed after tick end)
	 * One-tick tags exist only in server.
	 */
	@Deprecated
	public static void removeOneTickTag(Player player, String key)
	{
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent((c) -> 
		{
			c.removeOneTickTag(key);
		});
	}
	
	/**
	 * One-tick tags exist only in server.
	 * Remove all one-tick tags (tags that will be auto-removed after tick end)
	 */
	@Deprecated
	public void removeOneTickTags();
	
	/**
	 * One-tick tags exist only in server.
	 * Remove all one-tick tags (tags that will be auto-removed after tick end)
	 */
	@Deprecated
	public static void removeOneTickTags(Player player)
	{
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent((c) -> 
		{
			c.removeOneTickTags();
		});
	}
	
	public static class Impl implements CNFFPlayerModule
	{

		protected Player player;
		protected CompoundTag tag;
		
		public Impl(Player player)
		{
			this.player = player;
			this.tag = new CompoundTag();
			this.tag.put("one_tick_tag", new CompoundTag());
		}
		
		@Override
		public Player getPlayer()
		{
			return player;
		}
		
		@Override
		public CompoundTag getNbt()
		{
			return this.tag;
		}
		
		@Override
		public CompoundTag serializeNBT() {
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.tag = nbt;
		}
			
		@Override
		public boolean hasOneTickTag(String key)
		{
			return this.tag.getCompound("one_tick_tag").contains(key);
		}
		
		@Override
		public void addOneTickTag(String key)
		{
			this.tag.getCompound("one_tick_tag").putBoolean(key, true);
		}

		@Override
		public void removeOneTickTag(String key) {
			this.tag.getCompound("one_tick_tag").remove(key);
		}
		
		@Override
		public void removeOneTickTags()
		{
			this.tag.put("one_tick_tag", new CompoundTag());
		}

	}
	
	public static class Prvd implements ICapabilitySerializable<CompoundTag>
	{

		protected final Impl impl;
		
		public Prvd(Player player)
		{
			impl = new Impl(player);
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == NFFCapRegistry.CAP_BM_PLAYER)
				return LazyOptional.of(() -> {return this.impl;}).cast();
			else return LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			return impl.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			impl.deserializeNBT(nbt);
		}
		
	}
	
	public static CNFFPlayerModule get(Player player)
	{
		Wrapped<CNFFPlayerModule> wrp = new Wrapped<>(null);
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent(c -> 
		{
			wrp.set(c);
		});
		return wrp.get();
	}

}
