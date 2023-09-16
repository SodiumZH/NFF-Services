package net.sodiumstudio.befriendmobs.entity.befriended;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.registry.BMCaps;

/**
 * A temporal module for storage of data in IBefriendedMob interface.
 */
public interface CBefriendedMobData extends INBTSerializable<CompoundTag> {

	/**
	 * Transform capability to value implementations.
	 * In values transient data can be directly accessed.
	 * If permanent (serializable) data is needed, use wrapped methods in IBefriendedMob instead.
	 */
	public default Values values()
	{
		return (Values)this;
	}
	
	/** Get the whole NBT as compound tag.*/
	public CompoundTag getNbt();

	/**
	 * Values of trancient mob data, also as implementation of interface methods.
	 */
	public class Values implements CBefriendedMobData
	{
		public IBefriendedMob mob;
		public CompoundTag tag = new CompoundTag();
		public Values(IBefriendedMob mob)
		{
			this.mob = mob;
			this.anchor = mob.asMob().position();
			this.tag = new CompoundTag();
		}
		
		public boolean hasInit = false;
		public LivingEntity previousTarget = null;
		public Vec3 anchor;
		
		// BefriendedUndeadMob data
		public HashMap<String, Supplier<Boolean>> sunImmuneConditions = new HashMap<String, Supplier<Boolean>>();
		public HashMap<String, Supplier<Boolean>> sunImmuneNecessaryConditions = new HashMap<String, Supplier<Boolean>>();
		
		public Map<String, Object> tempObjects = new HashMap<>();
		
		@Override
		public CompoundTag serializeNBT() {
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			tag = nbt;
		}

		@Override
		public CompoundTag getNbt() {
			return tag;
		}		
	}
	
	public class Prvd implements ICapabilitySerializable<CompoundTag>
	{
		
		public CBefriendedMobData values;
		
		public Prvd(IBefriendedMob mob)
		{
			values = new Values(mob);
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == BMCaps.CAP_BEFRIENDED_MOB_TEMP_DATA)
				return LazyOptional.of(() -> {return this.values;}).cast();
			else return LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			return values.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			values.deserializeNBT(nbt);
		}
	}
}
