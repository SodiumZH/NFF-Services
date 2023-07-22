package net.sodiumstudio.befriendmobs.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.nautils.Wrapped;

public interface CBMPlayer extends INBTSerializable<CompoundTag>
{

	public Player getPlayer();
	public CompoundTag getNbt();
	
	public static class Impl implements CBMPlayer
	{

		protected Player player;
		protected CompoundTag tag;
		
		public Impl(Player player)
		{
			this.player = player;
			this.tag = new CompoundTag();
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
			if (cap == BMCaps.CAP_BM_PLAYER)
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
	
	public static CBMPlayer get(Player player)
	{
		Wrapped<CBMPlayer> wrp = new Wrapped<>(null);
		player.getCapability(BMCaps.CAP_BM_PLAYER).ifPresent(c -> 
		{
			wrp.set(c);
		});
		return wrp.get();
	}
	
}
