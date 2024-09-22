package net.sodiumzh.nautils.entity;

import java.util.function.Consumer;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;

/**
 * A {@code MobRespawnInfo} is a serializable data piece for respawning a mob.
 * Basically it's merged from a mob's nbt, but also modified for respawning (e.g. location and velocity reset)
 */
public class MobRespawnInfo implements INBTSerializable<CompoundTag>
{
	protected static final String ENTITY_CUSTOM_NAME_KEY = "CustomName";
	protected EntityType<? extends Mob> type;
	protected CompoundTag info = new CompoundTag();
	
	private MobRespawnInfo()
	{
	}

	@Nullable
	public Component getCustomName()
	{
		return this.info.contains(ENTITY_CUSTOM_NAME_KEY, Tag.TAG_STRING) ?
				NaUtilsInfoStatics.createText(this.info.getString(ENTITY_CUSTOM_NAME_KEY)) : null;
	}
	
	/**
	 * Actions to modify the NBT before respawning mob from it, e.g. position.
	 * This action doesn't impact the NBT itself, but creates and outputs a copy.
	 */
	protected CompoundTag preRespawnModify(Player player, BlockPos pos, Direction direction) {
		CompoundTag nbt = this.info.copy();
		Vec3 posV = new Vec3((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
		NaUtilsNBTStatics.putVec3(nbt, "Pos", posV);
		return nbt;
	}

	/**
	 * Make the info from a mob.
	 */
	@SuppressWarnings("unchecked")
	protected void makeFromMob(Mob mob) {
		this.beforeMake(mob);
		//this.tag.putString("mob_type", ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()).toString());
		this.type = (EntityType<? extends Mob>) mob.getType();
		this.info = new CompoundTag();
		mob.save(info);
		//this.tag.put("mob_nbt", nbt);
		this.afterMake(mob).accept(info);
	}

	/**
	 * Respawn mob from this info. 
	 * @param player Player as respawning action source.
	 * @param pos Position in level to respawn.
	 * @param direction Respawning direction.
	 * @return The mob if respawned, or null if on client, respawning cancelled or the info isn't valid.
	 */
	@Nullable
	public Mob respawn(Player player, BlockPos pos, Direction direction) {
		if (this.info.isEmpty()) 
			return null;
		if (this.type == null)
			return null;
		if (player.level.isClientSide)
			return null;
		if (this.beforeRespawn(player, pos, direction))
			return null;
		BlockState blockstate = player.level.getBlockState(pos);
		BlockPos pos1;
		if (blockstate.getCollisionShape(player.level, pos).isEmpty())
		{
			pos1 = pos;
		} else
		{
			pos1 = pos.relative(direction);
		}
		Mob mob = NaUtilsEntityStatics.spawnDefaultMob(this.type, (ServerLevel) (player.level), null,
				this.getCustomName() != null ? this.getCustomName() : null,
				player, pos1, true, !pos.equals(pos1) && direction == Direction.UP);
		if (mob != null)
		{
			CompoundTag nbt = preRespawnModify(player, pos1, direction);
			mob.setYRot(direction.toYRot());
			mob.load(nbt);
			mob.setHealth(mob.getMaxHealth());
			/*if (mob instanceof IBefriendedMob b)
			{
				//b.setInventoryFromMob();
				b.updateAnchor();
				b.setInit();
			}*/
			// stack.shrink(1);
			this.afterRespawn(mob, player);
		}
		return mob;
	}

	/**
	 * Actions before merging data from mob into this respawn info.
	 */
	protected void beforeMake(Mob fromMob) {}
	
	/**
	 * Actions after merging data from mob into this respawn info.
	 * @param fromMob Mob it makes from.
	 * @return Actions to modify the info nbt.
	 */
	protected Consumer<CompoundTag> afterMake(Mob fromMob) {return c -> {};}
	
	/**
	 * Actions before respawn. Return true to cancel respawning.
	 * @param player Player as respawning action source.
	 * @param pos Position in level to respawn.
	 * @param direction Respawning direction.
	 * @return Whether this respawning should be cancelled. If true, respawning will be cancelled and return null.
	 */
	protected boolean beforeRespawn(Player player, BlockPos pos, Direction direction) {return false;}
	
	/**
	 * Actions after respawn.
	 * @param mob Mob just spawned.
	 * @param player Player as respawning action source.
	 */
	protected void afterRespawn(Mob mob, Player player) {}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag res = new CompoundTag();
		res.putString("mob_type", ForgeRegistries.ENTITIES.getKey(type).toString());
		res.put("mob_nbt", this.info);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.type = nbt.contains("mob_type", Tag.TAG_STRING) ? (EntityType<? extends Mob>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("mob_type"))) : null;
		this.info = nbt.contains("mob_nbt", Tag.TAG_COMPOUND) ? nbt.getCompound("mob_nbt") : new CompoundTag();
	}
}
