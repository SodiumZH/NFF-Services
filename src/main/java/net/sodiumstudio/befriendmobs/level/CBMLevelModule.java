package net.sodiumstudio.befriendmobs.level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumstudio.befriendmobs.item.MobRespawnerInstance;
import net.sodiumstudio.nautils.NbtHelper;

/**
 * Comprehensive serializable module for levels in BM.
 * <p> Note: it's only for server. To sync the data you must send packets.
 */
public interface CBMLevelModule extends INBTSerializable<CompoundTag>
{

	/** Get the attached level. */
	public ServerLevel getLevel();

	/** Get the whole NBT tag. */
	public CompoundTag getNbt();
	
	/** Add a suspended respawner.
	 * <p> A suspended respawner is a respawner item stack generated on befriended mob dying that cannot be instantly given back to the owner.
	 * So it will be temporarily saved in the level, and once the owner enters the level, the respawner will be given and removed from the suspended list.
	 * <p> Suspended respawners are added only when {@link IBefriendedMob#getDeathRespawnerGenerationType} returns {@code DeathRespawnerGenerationType.GIVE}, but can be given back any time.
	 * <p> The adding action will be handled in {@link EntityEvents#onLivingDeath}.
	 * @param owner UUID of the owner.
	 */
	public void addSuspendedRespawner(MobRespawnerInstance respawner);
	
	/** Try returning a suspended respawner to the owner, and remove it from suspended list if succeeded.
	 * @param key UUID of the mob as the key.
	 * @return Whether succeeded. If succeeded, the UUID entry will NOT be removed, since in updating it's an iteration and the invalidated entries will be removed together after that.
	 */
	public boolean tryReturnSuspendedRespawner(String key);
	
	
	/**
	 * Remove a suspended respawner.
	 */
	public void removeSuspendedRespawner(String key);
	
	
	/** Scan the saved respawner list, check if some of them can be given to the owner, and give them */
	public void updateSuspendedRespawners();
	
	public static class Impl implements CBMLevelModule
	{

		protected ServerLevel level;
		
		protected CompoundTag nbt;
		
		protected Impl(ServerLevel level)
		{
			this.level = level;
			this.nbt = new CompoundTag();
			nbt.put("suspended_respawners", new CompoundTag());
		}
		
		
		@Override
		public CompoundTag serializeNBT() {
			return nbt.copy();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.nbt = nbt.copy();
		}

		@Override
		public ServerLevel getLevel() {
			return level;
		}

		@Override
		public CompoundTag getNbt() {
			return nbt;
		}


		@Override
		public void addSuspendedRespawner(MobRespawnerInstance respawner) {
			CompoundTag tag = new CompoundTag();
			respawner.get().save(tag);
			this.getNbt().getCompound("suspended_respawners").put(respawner.getUUID().toString(), tag);
		}
		
		@Override
		public boolean tryReturnSuspendedRespawner(String key) {
			CompoundTag container = this.getNbt().getCompound("suspended_respawners");
			if (!container.contains(key, NbtHelper.TAG_COMPOUND_ID))
				return false;	// No such entry
			ItemStack stack = ItemStack.of(container.getCompound(key));
			if (stack == null)
				return false;	// Not a valid item stack
			MobRespawnerInstance resp = MobRespawnerInstance.create(stack);
			if (resp == null)
				return false;	// Not a valid respawner
			if (this.getLevel().getPlayerByUUID(resp.getOwnerUUID()) == null)
				return false;	// Player isn't present
			if (!this.getLevel().getPlayerByUUID(resp.getOwnerUUID()).addItem(resp.get()))
				return false;	// Player's inventory is full
			else return true;	// Successfully added
		}


		@Override
		public void removeSuspendedRespawner(String key) 
		{
			this.getNbt().getCompound("suspended_respawners").remove(key);
		}
		
		@Override
		public void updateSuspendedRespawners() {
			CompoundTag container = this.getNbt().getCompound("suspended_respawners");
			HashSet<String> toRemove = new HashSet<>();
			for (String key: container.getAllKeys())
			{
				if (this.tryReturnSuspendedRespawner(key))
					toRemove.add(key);
			}
			for (String key: toRemove)
			{
				this.removeSuspendedRespawner(key);
			}
		}

		
	}
	
}
