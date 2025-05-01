package net.sodiumzh.nfu.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUInfoStatics;
import net.sodiumzh.nfu.util.NFUNBTStatics;

/**
 * A {@code MobRespawnInfo} is a serializable data piece for respawning a mob.
 * Basically it's merged from a mob's nbt, but also modified for respawning (e.g. location and velocity reset)
 */
public class MobRespawnInfo implements INBTSerializable<CompoundTag>
{
	protected static final String ENTITY_CUSTOM_NAME_KEY = "CustomName";
	protected static final String MOB_TYPE_KEY = "mob_type";
	protected static final String MOB_NBT_KEY = "mob_nbt";
	protected EntityType<? extends Mob> type;
	protected CompoundTag info = new CompoundTag();
	
	protected MobRespawnInfo()
	{
	}

	public MobRespawnInfo create()
	{
		return new MobRespawnInfo();
	}

	@Nullable
	public Component getCustomName()
	{
		return this.info.contains(ENTITY_CUSTOM_NAME_KEY, Tag.TAG_STRING) ?
				NFUInfoStatics.createText(this.info.getString(ENTITY_CUSTOM_NAME_KEY)) : null;
	}

	/**
	 * Save a mob's data to this instance.
	 */
	@SuppressWarnings("unchecked")
	public void saveFromMob(Mob mob) {
		this.beforeSave(mob);
		this.type = (EntityType<? extends Mob>) mob.getType();
		this.info = new CompoundTag();
		mob.save(info);
		//this.tag.put("mob_nbt", nbt);
		this.afterSave(mob, info);
	}

	/**
	 * Create a new instance containing a mob's data.
	 */
	public static MobRespawnInfo createFromMob(Mob mob) {
		MobRespawnInfo info = new MobRespawnInfo();
		info.saveFromMob(mob);
		return info;
	}

	/**
	 * Respawn mob from this info. 
	 * @param player Player as respawning action source.
	 * @param pos Position in level to respawn.
	 * @param direction Respawning direction.
	 * @return The mob if respawned, or null if on client, respawning cancelled or the info isn't valid.
	 */
	@Nullable
	public Mob respawn(Level level, @Nullable Player player, BlockPos pos, Direction direction) {
		if (level.isClientSide)
			return null;
		if (this.type == null)
			return null;
		if (this.beforeRespawn(level, player, pos, direction))
			return null;
		BlockState blockstate = level.getBlockState(pos);
		BlockPos pos1;
		if (blockstate.getCollisionShape(level, pos).isEmpty())
		{
			pos1 = pos;
		} else
		{
			pos1 = pos.relative(direction);
		}
		Mob mob = NFUEntityStatics.spawnDefaultMob(this.type, (ServerLevel) level, null,
				player, pos1, true, !pos.equals(pos1) && direction == Direction.UP);
		if (mob != null)
		{
			CompoundTag nbt = this.info.copy();
			if (!nbt.isEmpty()) {
				Vec3 posV = new Vec3((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
				NFUNBTStatics.putVec3(nbt, "Pos", posV);
				mob.setYRot(direction.toYRot());
				mob.load(nbt);
			}
			mob.setHealth(mob.getMaxHealth());
			this.afterRespawn(mob, level, player);
		}
		return mob;
	}

	/**
	 * Actions before merging data from mob into this respawn info.
	 */
	protected void beforeSave(Mob fromMob) {}
	
	/**
	 * Actions after merging data from mob into this respawn info.
	 * @param fromMob Mob it makes from.
	 * @param original Original NBT. Directly operate on it to modify.
	 */
	protected void afterSave(Mob fromMob, CompoundTag original) {}
	
	/**
	 * Actions before respawn. Return true to cancel respawning.
	 * @param player Player as respawning action source.
	 * @param pos Position in level to respawn.
	 * @param direction Respawning direction.
	 * @return Whether this respawning should be cancelled. If true, respawning will be cancelled and return null.
	 */
	protected boolean beforeRespawn(Level level, @Nullable Player player, BlockPos pos, Direction direction) {
		return false;
	}
	
	/**
	 * Actions after respawn.
	 * @param mob Mob just spawned.
	 * @param player Player as respawning action source.
	 */
	protected void afterRespawn(Mob mob, Level level, @Nullable Player player) {}

	/**
	 * Write the info into a {@link CompoundTag}. After writing, the {@link CompoundTag} will
	 * get two new sub-tags: "mob_type" (String) and "mob_nbt" (Compound).
	 */
	public void writeNBT(CompoundTag writeInto)
	{
		writeInto.putString(MOB_TYPE_KEY, ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
		writeInto.put(MOB_NBT_KEY, this.info.copy());
	}

	@Override
	public final CompoundTag serializeNBT() {
		CompoundTag res = new CompoundTag();
		this.writeNBT(res);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.type = nbt.contains(MOB_TYPE_KEY, Tag.TAG_STRING) ? (EntityType<? extends Mob>) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(nbt.getString("mob_type"))) : null;
		this.info = nbt.contains(MOB_NBT_KEY, Tag.TAG_COMPOUND) ? nbt.getCompound(MOB_NBT_KEY) : new CompoundTag();
	}
}
