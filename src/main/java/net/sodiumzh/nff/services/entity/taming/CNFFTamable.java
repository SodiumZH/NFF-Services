package net.sodiumzh.nff.services.entity.taming;

import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nautils.Wrapped;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.eventlisteners.NFFEntityEventListeners;
import net.sodiumzh.nff.services.eventlisteners.TamableAddHatredEvent;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public interface CNFFTamable extends INBTSerializable<CompoundTag> {

	public Mob getOwner();
	
	// ======== Hatred List related
	
	// Get this mob's hatred list
	// Sometimes hatred list prevents player to befriend the mob
	public HashSet<UUID> getHatred();
	
	// Add a player to the hatred list
	// If a player is in hatred list, it will take some time to remove from it. 15min by default.
	// Or input -1 to add permanent hatred.
	// This function doesn't post events.
	public void addHatred(Player player, int ticks);
	
	public default void addHatred(Player player)
	{
		addHatred(player, 18000);
	}

	public default void addHatredPermanent(Player player)
	{
		addHatred(player, -1);
	}
	
	// Check if a player is in the hatred list
	public boolean isInHatred(Player player);
	
	public int getHatredDuration(Player player);
	
	// ======== Serialization
	
	// Get NBT tag
	public CompoundTag getNbt();
	
	// Get the whole player data subtag under NBT
	public CompoundTag getPlayerDataNbt();

	public default Tag getPlayerData(Player player, String key)
	{
		return NaUtilsNBTStatics.getPlayerData(getPlayerDataNbt(), player, key);
	}

	public default int getPlayerDataInt(Player player, String key)
	{
		return ((IntTag)getPlayerData(player, key)).getAsInt();
	}
	
	public default float getPlayerDataFloat(Player player, String key)
	{
		return ((FloatTag)getPlayerData(player, key)).getAsFloat();
	}
	
	public default double getPlayerDataDouble(Player player, String key)
	{
		Tag tag = getPlayerData(player, key);
		if (tag instanceof FloatTag f)
			return f.getAsDouble();
		else if (tag instanceof DoubleTag d)
			return d.getAsDouble();
		else throw new ClassCastException();
	}
		
	public default void putPlayerData(Tag data, Player player, String key)
	{
		NaUtilsNBTStatics.putPlayerData(data, getPlayerDataNbt(), player, key);
	}
	
	public default boolean hasPlayerData(Player player, String key)
	{
		return NaUtilsNBTStatics.containsPlayerData(getPlayerDataNbt(), player, key);
	}
	
	public default void removePlayerData(Player player, String key)
	{
		NaUtilsNBTStatics.removePlayerData(getPlayerDataNbt(), player, key);
	}
	
	// =========== Timer
	
	// Get the value of non-player-specified timer (tick)
	public int getTimer(String key);
	
	// Get the value of player-specified timer (tick). (PS = player specified)
	public int getPlayerTimer(Player player, String key);
	
	public boolean hasTimer(String key);
	
	public boolean hasPlayerTimer(Player player, String key);
	
	// Setup a non-player-specified timer
	public IntTag setTimer(String key, int ticks);
	
	// Setup a player-specified timer. (PS = player specified)
	public IntTag setPlayerTimer(Player player, String key, int ticks);
	
	/**
	 *  Force stop a timer.
	 * @param key Timer key.
	 * @param postEvent If true, it will post the timer-up event.
	 */
	public void stopTimer(String key, boolean postEvent);
	
	/**
	 *  Force stop a player-specified timer. 
	 * @param postEvent If true, it will post the timer-up event.
	 */
	public void stopPlayerTimer(Player player, String key, boolean postEvent);
	
	
	// Update all timers that should be executed every tick
	public void updateTimers();
	
	
	// ============ Related utils
	
	public static CNFFTamable getCap(Mob mob)
	{
		Wrapped<CNFFTamable> cap = new Wrapped<CNFFTamable>(null);
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> {
			cap.set(l);
		});
		return cap.get();
	}
	
	public static CompoundTag getCapNbt(Mob mob)
	{
		Wrapped<CompoundTag> tag = new Wrapped<CompoundTag>(null);
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> {
			tag.set(l.getNbt());
		});
		return tag.get();
	}
	
	/** Try adding hatred with given reason. This function will check in befriending handler whether this reason is accepted,
	* post event and check whether canceled.
	* <p> Return if added hatred.
	* <p> Note: If a zero-duration hatred is attempted to add, it will normally post events but not do anything on hatred list.
	*/
	public default boolean addHatredWithReason(Player player, TamableHatredReason reason)
	{
		NFFTamingProcess handler = NFFTamingMapping.getHandler(getOwner());
		int ticks = handler.getHatredDurationTicks(reason);
		if (handler.getAddHatredReasonSet() != null
			&& handler.getAddHatredReasonSet().contains(reason) 
			&& !(this.isInHatred(player) && this.getHatredDuration(player) > ticks))
		{
			TamableAddHatredEvent e = 
					new TamableAddHatredEvent(getOwner(), player, ticks, reason);
			boolean canceled = MinecraftForge.EVENT_BUS.post(e);
			if (!canceled)
			{
				if (ticks != 0)
					addHatred(player, ticks);
				handler.onAddingHatred(getOwner(), player, reason);
				return ticks != 0;
			}
		}
		return false;
	}

	// Presets
	
	/**
	 * Set the mob is always hostile to a specified target once it's in the follow range, ignoring target goals.
	 * If input is null, the previous always-hostile-to target will be removed and the mob will perform normally.
	 * Its implementation is in {@link NFFEntityEventListeners#onLivingChangeTarget_Lowest},
	 * {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest} and 
	 */
	public default void setAlwaysHostileTo(@Nullable LivingEntity target)
	{
		if (target == null || !target.isAlive())
		{
			this.getNbt().remove("always_hostile_to");
		}
		else
		{
			this.getNbt().putUUID("always_hostile_to", target.getUUID());
		}
	}
	
	/**
	 * get the target the mob is always hostile to.
	 * If the mob isn't set always hostile to anything, it will return null, no matter if the mob has a target.
	 * Its implementation is in {@link NFFEntityEventListeners#onLivingChangeTarget_Lowest} and {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest}
	 */
	@Nullable
	public default UUID getAlwaysHostileTo()
	{
		if (this.getNbt().contains("always_hostile_to", NaUtilsNBTStatics.TAG_INT_ARRAY_ID))
		{
			return this.getNbt().getUUID("always_hostile_to");		
		}
		else return null;
	}
	
	/**
	 * Set if the mob is forced not despawning despite the return of {@link Mob#isPersistenceRequired}
	 * Its implementation is in {@link NFFEntityEventListeners#onCheckDespawn}
	 * @param value True to keep it persistance. False to apply {@link Mob#isPersistenceRequired} on check.
	 */
	public default void setForcePersistent(boolean value)
	{
		this.getNbt().putBoolean("force_persistent", value);
	}
	
	/**
	 * Get if the mob forced not despawning despite the return of {@link Mob#isPersistenceRequired}
	 * @return True if the mob is forced not despawning in {@link CNFFTamable}. False to apply {@link Mob#isPersistenceRequired} on check.
	 */
	public default boolean isForcePersistent()
	{
		if (this.getNbt().contains("force_persistent", NaUtilsNBTStatics.TAG_BYTE_ID))
			return this.getNbt().getBoolean("force_persistent");
		else return false;
	}
	
}
