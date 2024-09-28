package net.sodiumzh.nautils.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @deprecated Not implemented
 */
@Deprecated
public interface CConditionalNeutralMob extends INBTSerializable<ListTag>
{
	/**
	 * Get mob possessing this capability.
	 */
	public Mob getMob();
	
	/**
	 * Get the condition 
	 */
	public BiPredicate<Mob, LivingEntity> getCondition();
	/**
	 * Get a set of UUIDs of entities that have provoked the mob by attacking or calling {@code setAngryAt} somehow.
	 * <p>Note: If this mob is angry at a target in this capability, it only means the conditional neutralization doesn't
	 * apply on the target. If there're other mechanisms preventing the mob from attacking, they'll still take effect.
	 */
	public Set<UUID> getAngryUUIDSet();
	
	/**
	 * Check if this mob is angry at a {@code LivingEntity}.
	 * <p>Note: If this mob is angry at a target in this capability, it only means the conditional neutralization doesn't
	 * apply on the target. If there're other mechanisms preventing the mob from attacking, they'll still take effect.
	 */
	public boolean isAngryAt(LivingEntity target);
	
	/**
	 * Get the default forgiving time (in ticks). This will applied in {@code setAngryAt(LivingEntity)}.
	 */
	public int getDefaultForgivingTime();
	
	/**
	 * Set this mob angry at a target with given forgiving time (in ticks). -1 means permanent.
	 * <p>Note: If this mob is angry at a target in this capability, it only means the conditional neutralization doesn't
	 * apply on the target. If there're other mechanisms preventing the mob from attacking, they'll still take effect.
	 */
	public void setAngryAt(LivingEntity target, int forgivingTime);
	
	/**
	 * Set this mob angry at a target with default forgiving time.
	 * <p>Note: If this mob is angry at a target in this capability, it only means the conditional neutralization doesn't
	 * apply on the target. If there're other mechanisms preventing the mob from attacking, they'll still take effect.
	 */
	public default void setAngryAt(LivingEntity target) {setAngryAt(target, getDefaultForgivingTime());}
	
	/**
	 * Set this mob always (permanently) angry at a mob
	 */
	public default void setAlwaysAngryAt(LivingEntity target) {setAngryAt(target, -1);}
	
	public int getRemainingForgivingTime(LivingEntity target);
	
	public void stopAngryAt(LivingEntity target);
	
	public void tick();
	
	public static class Impl implements CConditionalNeutralMob
	{
		private Mob mob;
		private Map<UUID, Integer> angryForgivingTimes = new HashMap<>();
		private BiPredicate<Mob, LivingEntity> condition;
		private int defaultForgivingTime;
		
		public Impl(Mob mob, BiPredicate<Mob, LivingEntity> cond, int defaultForgivingTime)
		{
			this.mob = mob;
			this.condition = cond;
			this.defaultForgivingTime = defaultForgivingTime;
		}
		
		public Impl(Mob mob, BiPredicate<Mob, LivingEntity> cond)
		{
			this(mob, cond, 180 * 20);	// 3 min by default
		}
		
		@Override
		public ListTag serializeNBT() {
			ListTag res = new ListTag();
			for (Entry<UUID, Integer> entry: this.angryForgivingTimes.entrySet())
			{
				if (entry.getValue() == 0) continue;
				CompoundTag elem = new CompoundTag();
				elem.putUUID("uuid", entry.getKey());
				elem.putInt("forgiving_time", entry.getValue());
				res.add(elem);
			}
			return res;
		}

		@Override
		public void deserializeNBT(ListTag nbt) {
			this.angryForgivingTimes.clear();
			for (int i = 0; i < nbt.size(); ++i)
			{
				CompoundTag elem = nbt.getCompound(i);
				this.angryForgivingTimes.put(elem.getUUID("uuid"), elem.getInt("forgiving_time"));
			}
		}

		@Override
		public Mob getMob() {
			return mob;
		}

		@Override
		public BiPredicate<Mob, LivingEntity> getCondition() {
			return condition;
		}

		@Override
		public Set<UUID> getAngryUUIDSet()
		{
			Set<UUID> res = this.angryForgivingTimes.keySet();
			res.removeIf(u -> this.angryForgivingTimes.get(u) == 0);
			return res;
		}

		@Override
		public int getDefaultForgivingTime() {
			return this.defaultForgivingTime;
		}

		@Override
		public int getRemainingForgivingTime(LivingEntity target)
		{
			if (!this.angryForgivingTimes.containsKey(target.getUUID()))
				return 0;
			else return this.angryForgivingTimes.get(target.getUUID());
		}
		
		@Override
		public boolean isAngryAt(LivingEntity target) {
			return this.getRemainingForgivingTime(target) != 0;
		}

		@Override
		public void setAngryAt(LivingEntity target, int forgivingTime) {
			boolean shouldAdd;
			if (forgivingTime == 0) throw new IllegalArgumentException();
			if (!this.isAngryAt(target))
				shouldAdd = true;
			else if (this.getRemainingForgivingTime(target) < 0)
				shouldAdd = false;
			else if (forgivingTime < 0)
				shouldAdd = true;
			else shouldAdd = this.getRemainingForgivingTime(target) < forgivingTime;
			if (shouldAdd)
				this.angryForgivingTimes.put(target.getUUID(), forgivingTime);
		}

		@Override
		public void stopAngryAt(LivingEntity target) {
			this.angryForgivingTimes.remove(target.getUUID());
		}
		
		@Override
		public void tick() 
		{
			for (UUID uuid: this.angryForgivingTimes.keySet())
			{
				if (this.angryForgivingTimes.get(uuid) == 0)
					this.angryForgivingTimes.remove(uuid);
				else if (this.angryForgivingTimes.get(uuid) > 0)
					this.angryForgivingTimes.put(uuid, this.angryForgivingTimes.get(uuid) - 1);
			}
		}
		
	}
}
