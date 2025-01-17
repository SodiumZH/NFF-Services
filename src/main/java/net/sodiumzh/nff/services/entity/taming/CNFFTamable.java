package net.sodiumzh.nff.services.entity.taming;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.capability.CEntityTimerCapability;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandler;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nff.services.eventlisteners.NFFEntityEventListeners;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public interface CNFFTamable extends CEntityTimerCapability<Mob>, CMobAngerHandler {

	@Nonnull
	public CompoundTag getPlayerSpecificNBT(Player player);

	public int getPlayerTimerRemainingTime(@Nonnull Player player, String key);

	public boolean hasPlayerTimer(Player player, String key);

	public void putPlayerTimer(Player player, String key, int ticks);

	public void removePlayerTimer(Player player, String key, boolean postEvent);

	// Presets
	
	/**
	 * Set the mob is always hostile to a specified target once it's in the follow range, ignoring target goals.
	 * If input is null, the previous always-hostile-to target will be removed and the mob will perform normally.
	 * Its implementation is in {@link NFFEntityEventListeners#onLivingChangeTarget_Lowest},
	 * {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest} and 
	 */
	public void setAlwaysHostileTo(@Nullable LivingEntity target);
	
	/**
	 * get the target the mob is always hostile to.
	 * If the mob isn't set always hostile to anything, it will return null, no matter if the mob has a target.
	 * Its implementation is in {@link NFFEntityEventListeners#onLivingChangeTarget_Lowest} and {@link NFFEntityEventListeners#onLivingSetAttackTarget_Lowest}
	 */
	@Nullable
	public UUID getAlwaysHostileTo();
	
	/**
	 * Set if the mob is forced not despawning despite the return of {@link Mob#isPersistenceRequired}
	 * Its implementation is in {@link NFFEntityEventListeners#onCheckDespawn}
	 * @param value True to keep it persistance. False to apply {@link Mob#isPersistenceRequired} on check.
	 */
	public void setForcePersistent(boolean value);
	
	/**
	 * Get if the mob forced not despawning despite the return of {@link Mob#isPersistenceRequired}
	 * @return True if the mob is forced not despawning in {@link CNFFTamable}. False to apply {@link Mob#isPersistenceRequired} on check.
	 */
	public boolean isForcePersistent();

	@Nonnull
	public static CNFFTamable get(Mob mob) {
		return mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).orElse(new CNFFTamableImpl(mob, MobAngerRules.NO_ANGER.get()));
	}

}
