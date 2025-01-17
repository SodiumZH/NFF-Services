package net.sodiumzh.nff.services.entity.taming;

import java.util.*;

import javax.annotation.Nonnull;

import net.minecraft.nbt.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.eventlisteners.TamableTimerUpEvent;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandler;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CNFFTamableImpl extends CMobAngerHandler.Impl implements CNFFTamable
{
	private static final UUID EMPTY_UUID = new UUID(0, 0);

	private CompoundTag generalNBT = new CompoundTag();
	private final Map<UUID, CompoundTag> playerSpecificNBT = new HashMap<>();
	private Mob mob;
	private final Map<String, Integer> timer = new HashMap<>();
	private MobAngerRules angerRules;
	private UUID alwaysHostileTo = EMPTY_UUID;
	private boolean forcePersistent = false;

	public CNFFTamableImpl(Mob mob, MobAngerRules rules)
	{
		super(mob, rules);
	}

	@Override
	public CompoundTag serializeNBT() 
	{
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("0.x.28+", true);
		nbt.put("generalNBT", generalNBT.copy());
		nbt.put("playerSpecificNBT", this.savePlayerSpecificNBT());
		nbt.put("timer", this.saveTimer());
		nbt.put("anger", this.saveAngerList());
		nbt.put("alwaysHostileTo", NbtUtils.createUUID(alwaysHostileTo));
		nbt.putBoolean("forcePersistent", forcePersistent);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) 
	{		
		if (!nbt.contains("0.x.28+")) return;	// Don't load data when porting old data to prevent errors
		this.generalNBT = nbt.getCompound("generalNBT").copy();
		this.loadPlayerSpecificNBT(nbt.getCompound("playerSpecificNBT"));
		this.loadTimerFromNBT(nbt.getCompound("timer"));
		this.loadAngerList(nbt.getCompound("anger"));
		this.alwaysHostileTo = NbtUtils.loadUUID(nbt.getCompound("alwaysHostileTo"));
		this.forcePersistent = nbt.getBoolean("forcePersistent");
	}

	private CompoundTag savePlayerSpecificNBT() {
		CompoundTag nbt = new CompoundTag();
		for (var entry: this.playerSpecificNBT.entrySet()) {
			nbt.put(entry.getKey().toString(), entry.getValue().copy());
		}
		return nbt;
	}

	private void loadPlayerSpecificNBT(CompoundTag nbt) {
		this.playerSpecificNBT.clear();
		for (String key: nbt.getAllKeys()) {
			this.playerSpecificNBT.put(UUID.fromString(key), nbt.getCompound(key).copy());
		}
	}

	private String playerSpecificTimerKey(@Nonnull Player player, String key)
	{
		return player.getStringUUID() + "|" + key;
	}

	@NotNull
	@Override
	public CompoundTag getPlayerSpecificNBT(Player player) {
		return this.playerSpecificNBT.getOrDefault(player.getUUID(), new CompoundTag());
	}

	// Player timers are in nbt/timers/player_timers
	@Override
	public int getPlayerTimerRemainingTime(@Nonnull Player player, String key) {
		return getTimerRemainingTime(playerSpecificTimerKey(player, key));
	}

	@Override
	public boolean hasPlayerTimer(Player player, String key)
	{
		return this.hasTimer(playerSpecificTimerKey(player, key));
	}

	@Override
	public void putPlayerTimer(Player player, String key, int ticks)
	{
		this.putTimer(playerSpecificTimerKey(player, key), ticks);
	}

	@Override
	public void removePlayerTimer(Player player, String key, boolean postEvent)
	{
		this.removeTimer(playerSpecificTimerKey(player, key), postEvent);
	}

	@Override
	public void setAlwaysHostileTo(@Nullable LivingEntity target) {
		this.alwaysHostileTo = Optional.ofNullable(target).map(LivingEntity::getUUID).orElse(EMPTY_UUID);
	}

	@Nullable
	@Override
	public UUID getAlwaysHostileTo() {
		return this.alwaysHostileTo;
	}

	@Override
	public void setForcePersistent(boolean value) {
		this.forcePersistent = value;
	}

	@Override
	public boolean isForcePersistent() {
		return this.forcePersistent;
	}

	@Override
	public Mob getEntity() {
		return mob;
	}

	@Override
	public MobAngerRules getRules() {
		return angerRules;
	}

	@Override
	public Map<String, Integer> getTimerMap() {
		return timer;
	}
}
