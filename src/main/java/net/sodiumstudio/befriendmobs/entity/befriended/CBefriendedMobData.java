package net.sodiumstudio.befriendmobs.entity.befriended;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.befriendmobs.util.BMErrorHandler;
import net.sodiumstudio.nautils.MiscUtil;

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
	 * Save common data of all befriended mobs to the data capability tag.
	 */
	public void saveFromMob();
	
	/**
	 * Load common data of all befriended mobs from the data capability tag.
	 */
	public void loadToMob();
	
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
			
			tag.put("common", new CompoundTag());
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
			saveFromMob();
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			loadToMob();
			tag = nbt;
		}

		@Override
		public CompoundTag getNbt() {
			return tag;
		}

		@Override
		public void saveFromMob() {
			CompoundTag common = this.getNbt().getCompound("common");
			common.putUUID("owner", this.mob.getOwnerUUID());
			common.putString("mod_id", this.mob.getModId());
			common.putInt("ai_state", this.mob.getAIState().id);
			this.mob.getAdditionalInventory().saveToTag(common, "inventory");
		}

		@Override
		public void loadToMob() {
			CompoundTag common = this.getNbt().getCompound("common");
			if (common.isEmpty())
			{
				BMErrorHandler.exception("Missing befriended mob common data.");
				return;
			}
			this.mob.setOwnerUUID(common.getUUID("owner"));
			this.mob.setAIState(BefriendedAIState.fromID(common.getInt("ai_state")), false);
			this.mob.getAdditionalInventory().readFromTag(common.getCompound("inventory"));			
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
			if (cap == BMCaps.CAP_BEFRIENDED_MOB_DATA)
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
	
	// ========== static utilities
	public static CBefriendedMobData get(IBefriendedMob mob)
	{
		return MiscUtil.getValue(mob.asMob().getCapability(BMCaps.CAP_BEFRIENDED_MOB_DATA));
	}
	
}
