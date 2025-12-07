package net.sodiumzh.nff.services.entity.capability;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;
import net.sodiumzh.nff.services.eventlistener.NFFEntityEventListeners;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;
import net.sodiumzh.nfu.capability.CEntityTimerCapability;
import net.sodiumzh.nfu.capability.EntityTimerAccessor;
import net.sodiumzh.nfu.entity.anger.CMobAngerHandler;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.util.NFUMiscStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CNFFTamable extends CEntityTimerCapability<Mob>, CMobAngerHandler {

	/**
	 * Get the general nbt data, i.e. non-player-specific data. One mob has only one general nbt.
	 */
	@Nonnull
	public CompoundTag getGeneralNBT();

	/**
	 * Get the nbt for a specific player. This nbt is separated from general nbt, and has one nbt for each player.
	 */
	@Nonnull
	public Optional<CompoundTag> getPlayerSpecificNBT(Player player);

	@Nonnull
	public Optional<CompoundTag> getPlayerSpecificNBT(UUID uuid);

	/**
	 * Get the nbt for a specific player if it's present, and create if absent.
	 * This nbt is separated from general nbt, and has one nbt for each player.
	 */
	public CompoundTag getOrCreatePlayerSpecificNBT(Player player);

	public CompoundTag getOrCreatePlayerSpecificNBT(UUID uuid);

	public boolean hasPlayerSpecificNBT(Player player);

	public boolean hasPlayerSpecificNBT(UUID uuid);

	/**
	 * Get all players which have a <i>non-empty</i> nbt in this mob.
	 * <p>Note: It will return all UUIDs of which the nbt is present. There's no guarantee that the player is present
	 * in the level.
	 * <p>Note: This is not the timer.
	 */
	public ImmutableSet<UUID> getAllPlayersWithNBT();

	public int getPlayerTimerRemainingTime(@Nonnull Player player, String key);

	public int getPlayerTimerRemainingTime(@Nonnull UUID uuid, String key);

	public boolean hasPlayerTimer(Player player, String key);

	public boolean hasPlayerTimer(UUID uuid, String key);

	public void putPlayerTimer(Player player, String key, int ticks);

	public void putPlayerTimer(UUID uuid, String key, int ticks);

	public void removePlayerTimer(Player player, String key, boolean postEvent);

	public void removePlayerTimer(UUID uuid, String key, boolean postEvent);

	public List<String> getAllTimerKeys(Player player);

	/**
	 * Get all players which have a <i>non-empty</i> timer in this mob. Note that this is not the nbt.
	 */
	public List<UUID> getAllTimingPlayers();

	/**
	 * Get all player UUIDs and remaining timers of the same key. (The players are not necessarily present in the level)
	 */
	public List<Tuple<UUID, Integer>> getAllPlayerTimersOfKey(String key);

	/**
	 * Set the mob is always hostile to a specified target once it's in the follow range, ignoring target goals.
	 * If input is null, the previous always-hostile-to target will be removed and the mob will perform normally.
	 * <p>Always Hostile feature is handled in {@link NFFEntityEventListeners#onLivingChangeTarget_Low}
	 * and {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest}.
	 */
	public void setAlwaysHostileTo(@Nullable LivingEntity target);
	
	/**
	 * get the target the mob is always hostile to.
	 * If the mob isn't set always hostile to anything, it will return null, no matter if the mob has a target.
	 * <p>Always Hostile feature is handled in {@link NFFEntityEventListeners#onLivingChangeTarget_Low}
	 * and {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest}.
	 */
	@Nullable
	public UUID getAlwaysHostileTo();
	
	/**
	 * Set if the mob is forced no despawn despite the return of {@link Mob#isPersistenceRequired}.
	 * <p>Force Persistent feature is handled in {@link NFFEntityEventListeners#onDespawn}.
	 * <p>Note: it doesn't prevent despawn in the peace mode.
	 * @param value True to keep it persistent. False to perform {@link Mob#isPersistenceRequired} check.
	 */
	public void setForcePersistent(boolean value);
	
	/**
	 * Get if the mob is forced no despawn despite the return of {@link Mob#isPersistenceRequired}
	 * <p>Force Persistent feature is handled in {@link NFFEntityEventListeners#onDespawn}.
	 * <p>Note: it doesn't prevent despawn in the peace mode.
	 * @return True if the mob is forced no despawn in {@link CNFFTamable}. False to perform {@link Mob#isPersistenceRequired} check.
	 */
	public boolean isForcePersistent();

	@Nonnull
	public NFFTamingProcess getTamingProcess();

	/**
	 * Get the tamable capability of a mob. If it's not present, return a new (invalid) instance to prevent errors.
	 */
	@Nonnull
	public static CNFFTamable get(Mob mob) {
		Optional<CNFFTamable> res = getOptional(mob).resolve();
		return res.orElseGet(() -> new CNFFTamableImpl(mob, MobAngerRules.NO_ANGER.get()));
	}

	/**
	 * Get the tamable capability of a mob if present, or empty if not.
	 */
	@Nonnull
	public static LazyOptional<CNFFTamable> getOptional(Mob mob) {
		return mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB);
	}


	public static String getPlayerSpecificTimerKey(UUID playerUUID, String key) {
		return playerUUID + "|" + key;
	}

	public static String getPlayerSpecificTimerKey(Player player, String key) {
		return player.getStringUUID() + "|" + key;
	}

	public static Optional<Tuple<UUID, String>> parsePlayerSpecificTimerKey(String rawKey) {
		if (!rawKey.contains("|")) return Optional.empty();
		String[] split = rawKey.split("\\|");
		if (split.length != 2) return Optional.empty();
		return NFUMiscStatics.toOptionalUUID(split[0]).map(uuid -> new Tuple<>(uuid, split[1]));
	}

	public static EntityTimerAccessor getTimerAccessor(String key) {
		return EntityTimerAccessor.get(key, NFFCapRegistry.CAP_BEFRIENDABLE_MOB);
	}

}
