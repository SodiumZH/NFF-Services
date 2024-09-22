package net.sodiumzh.nff.services.level;

import java.util.HashSet;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import org.apache.commons.lang3.mutable.MutableObject;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.capability.CNFFPlayerModule;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.event.BMHooks;
import net.sodiumzh.nff.services.event.level.NFFLevelModuleTickEndEvent;
import net.sodiumzh.nff.services.event.level.NFFLevelModuleTickStartEvent;
import net.sodiumzh.nff.services.eventlisteners.NFFEntityEventListeners;
import net.sodiumzh.nff.services.eventlisteners.NFFServerEventListeners;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

/**
 * Comprehensive serializable module for levels in BM.
 * <p> Note: it's only for server. To sync the data you must send packets.
 */
public interface CNFFLevelModule extends INBTSerializable<CompoundTag>
{

	/** Get the attached level. */
	public ServerLevel getLevel();

	/** Get the whole NBT tag. */
	public CompoundTag getNbt();
	
	/** Get the overall tick count from this level created. */
	public long getTickCount();
	
	/** Add a suspended respawner.
	 * <p> A suspended respawner is a respawner item stack generated on befriended mob dying that cannot be instantly given back to the owner.
	 * So it will be temporarily saved in the level, and once the owner enters the level, the respawner will be given and removed from the suspended list.
	 * <p> Suspended respawners are added only when {@link INFFTamed#getDeathRespawnerGenerationType} returns {@code DeathRespawnerGenerationType.GIVE}, but can be given back any time.
	 * <p> The adding action will be handled in {@link NFFEntityEventListeners#onLivingDeath}.
	 * @param owner UUID of the owner.
	 */
	public void addSuspendedRespawner(NFFMobRespawnerInstance respawner);
	
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

	/** Executed every tick.
	 * Called in {@link NFFServerEventListeners#onLevelTick}.
	 * <p> It's ticked after the vanilla level tick.
	 */
	public void tick();

	
	// ============================ Implementation ============================ //
	
	public static class Impl implements CNFFLevelModule
	{

		protected ServerLevel level;
		protected CompoundTag nbt;
		protected long tickCount = 0;
		
		
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
		public long getTickCount()
		{
			return tickCount;
		}
		
		@Override
		public void addSuspendedRespawner(NFFMobRespawnerInstance respawner) {
			CompoundTag tag = new CompoundTag();
			respawner.get().save(tag);
			this.getNbt().getCompound("suspended_respawners").put(respawner.getUUID().toString(), tag);
		}
		
		@Override
		public boolean tryReturnSuspendedRespawner(String key) {
			CompoundTag container = this.getNbt().getCompound("suspended_respawners");
			if (!container.contains(key, NaUtilsNBTStatics.TAG_COMPOUND_ID))
				return false;	// No such entry
			ItemStack stack = ItemStack.of(container.getCompound(key));
			if (stack == null)
				return false;	// Not a valid item stack
			NFFMobRespawnerInstance resp = NFFMobRespawnerInstance.create(stack);
			if (resp == null)
				return false;	// Not a valid respawner
			if (this.getLevel().getPlayerByUUID(resp.getOwnerUUID()) == null)
				return false;	// Player isn't present
			if (this.getLevel().getPlayerByUUID(resp.getOwnerUUID()).getInventory().getFreeSlot() == -1)
				return false;	// Player's inventory is full
			if (!this.getLevel().getPlayerByUUID(resp.getOwnerUUID()).addItem(resp.get()))
				return false;	// Adding failed for possible other reason
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
		
		@Override
		public void tick()
		{			
			tickCount += 1;
			MinecraftForge.EVENT_BUS.post(new NFFLevelModuleTickStartEvent(level));;
			
			if (getTickCount() % 20 == 0)
				updateSuspendedRespawners();
			
			// Debug output time
			if (NFFServices.IS_DEBUG_MODE && getTickCount() % 200 == 0 && getLevel().players().size() > 0)
				NaUtilsDebugStatics.debugPrintToScreen("Time (s) : " + Long.toString(getTickCount()), getLevel().players().get(0));
			
			MinecraftForge.EVENT_BUS.post(new NFFLevelModuleTickEndEvent(level));
		}
	}
	
	
	// ============================ Provider ============================ //	
	
	public static class Prvd implements ICapabilitySerializable<CompoundTag>
	{

		protected final Impl impl;
		
		public Prvd(ServerLevel level)
		{
			impl = new Impl(level);
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == NFFCapRegistry.CAP_BM_LEVEL)
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
		MutableObject<CNFFPlayerModule> wrp = new MutableObject<>(null);
		player.getCapability(NFFCapRegistry.CAP_BM_PLAYER).ifPresent(c -> 
		{
			wrp.setValue(c);
		});
		return wrp.getValue();
	}
	
}
