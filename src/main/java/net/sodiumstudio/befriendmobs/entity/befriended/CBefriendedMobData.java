package net.sodiumstudio.befriendmobs.entity.befriended;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.nautils.NbtHelper;
import net.sodiumstudio.nautils.function.MutablePredicate;

/**
 * A temporal module for storage of data in IBefriendedMob interface.
 */
public interface CBefriendedMobData extends INBTSerializable<CompoundTag> {

	/**
	 * Transform capability to value implementations.
	 * In values transient data can be directly accessed.
	 * If permanent (serializable) data is needed, use wrapped methods in IBefriendedMob instead.
	 * @deprecated Don't directly access the values (capability implementation) here. Use getters and setters instead.
	 */
	@Deprecated
	public default Values values()
	{
		return (Values)this;
	}
	
	/** Get the befriended mob owning this data. */
	public IBefriendedMob getOwner();
	
	/** Get the whole NBT as compound tag.*/
	public CompoundTag getNbt();

	/** Get sun immunity. It only works when the mob is an {@link IBefriendedSunSensitiveMob}, otherwise throws exception. */
	public MutablePredicate<IBefriendedSunSensitiveMob> getSunImmunity();
	
	/** Get temporary object from a key from table. Temporary object table is a non-serialized object table to store any objects, 
	 * not directly accessible but only with {@code getTempObject}, {@code addTempObject} and {@code removeTempObject}.
	 * @return Object if present, or null if not.
	 */
	@Nullable
	public Object getTempObject(String key);
	
	/** Add a temporary object to table. Temporary object table is a non-serialized object table to store any objects.*/
	public void addTempObject(String key, Object obj);
	
	/** Remove a temporary object from table. Temporary object table is a non-serialized object table to store any objects.
	 * If the key is absent, it will not do anything.
	 */
	public void removeTempObject(String key);
	
	/**
	 * Force access a value with casted class.
	 * If it's absent, return null. If class mismatches, throw exception.
	 * @return Value with casted class, or null if absent or class mismatching.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public default <T> T getTempObjectCasted(String key)
	{
		Object obj = getTempObject(key);
		if (obj == null) return null;
		try {
			return (T) obj;
		}
		catch (ClassCastException e) {
			LogUtils.getLogger().error("CBefriendedMobData#getTempObjectCasted: class mismatch found.");
			throw e;
		}
	}
	
	// Owner info related
	/**
	 * Get the owner's display name, or "Not Present" if not found.
	 */
	public String getOwnerName();
	
	/**
	 * Get the date it's befriended.
	 * @return An int[3] indicating year, month and day, or null if not recorded (legacy).
	 */
	@Nullable
	public int[] getBefriendedDate();
	
	/**
	 * Get the dimension where it's befriended, or "Not Recorded" if not recorded (legacy).
	 * @return Dimension name.
	 */
	public MutableComponent getBefriendedDimension();
	
	/**
	 * Get the coordination where it's befriended, or null if not recorded (legacy).
	 */
	@Nullable
	public Vec3 getBefriendedLocation();
	
	/**
	 * Record info about befriending time and location. Invoked in {@link BefriendingHandler#befriend).
	 */
	public void recordBefriendedInfo();
	
	/**
	 * Values of trancient mob data, also as implementation of interface methods.
	 */
	public class Values implements CBefriendedMobData
	{
		private IBefriendedMob mob;
		private CompoundTag tag = new CompoundTag();
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
		private MutablePredicate<IBefriendedSunSensitiveMob> sunImmunity = new MutablePredicate<>();
		
		private Map<String, Object> tempObjects = new HashMap<>();
		
		@Override
		public CompoundTag serializeNBT() {
			// TODO: remove this in release. This is only for porting old data to new one.
			mob.getOwnerInDimension().ifPresent(player -> {
				if (!tag.contains("befriended_date", NbtHelper.TAG_COMPOUND_ID))
				{
					tag.put("befriended_date", new CompoundTag());
					tag.getCompound("befriended_date")
				}
			});
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

		@Override
		public IBefriendedMob getOwner() {
			return mob;
		}

		@Override
		public MutablePredicate<IBefriendedSunSensitiveMob> getSunImmunity() {
			if (mob instanceof IBefriendedSunSensitiveMob)
				return sunImmunity;
			else throw new UnsupportedOperationException("CBefriendedMobData only supports IBefriendedSunSensitiveMob. Attempted class: " + mob.getClass().toString());
		}

		@Override
		public Object getTempObject(String key) {
			return tempObjects.get(key);
		}

		@Override
		public void addTempObject(String key, Object obj) {
			tempObjects.put(key, obj);
		}

		@Override
		public void removeTempObject(String key) {
			tempObjects.remove(key);
		}

		@Override
		public String getOwnerName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getBefriendedDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MutableComponent getBefriendedDimension() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Vec3 getBefriendedLocation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void recordBefriendedInfo() {
			tag.put("befriended_info", new CompoundTag());
			CompoundTag info = tag.getCompound("befriended_info");
			info.putString("dimension", RegistryAccess//mob.asMob().level.dimensionType());
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
