package net.sodiumzh.nff.services.entity.taming;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nautils.entity.anger.MobAngerHandler;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nautils.entity.anger.MobForgiveResult;
import net.sodiumzh.nautils.entity.anger.MobSetAngerResult;
import net.sodiumzh.nautils.exceptions.MissingRegistryException;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public class CNFFTamableImpl extends MobAngerHandler implements CNFFTamable
{
	private static final UUID EMPTY_UUID = new UUID(0, 0);

	private CompoundTag generalNBT = new CompoundTag();
	private final Map<UUID, CompoundTag> playerSpecificNBT = new HashMap<>();
	private final Map<String, Integer> timer = new HashMap<>();
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
		if (!nbt.getBoolean("0.x.28+")) return;	// Don't load data when porting old data to prevent errors
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
			if (!entry.getValue().isEmpty()) nbt.put(entry.getKey().toString(), entry.getValue().copy());
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

	private String playerSpecificTimerKey(@Nonnull UUID uuid, String key)
	{
		return uuid.toString() + "|" + key;
	}

	@Override
	public @Nonnull CompoundTag getGeneralNBT() {
		return generalNBT;
	}

	@Nonnull
	@Override
	public CompoundTag getOrCreatePlayerSpecificNBT(@Nonnull Player player) {
		return getOrCreatePlayerSpecificNBT(player.getUUID());
	}

	@Override
	public CompoundTag getOrCreatePlayerSpecificNBT(@Nonnull UUID uuid) {
		if (uuid.equals(EMPTY_UUID)) throw new IllegalArgumentException("Empty UUID.");
		if (!this.playerSpecificNBT.containsKey(uuid))
			this.playerSpecificNBT.put(uuid, new CompoundTag());
		return this.playerSpecificNBT.get(uuid);
	}

	// Player timer's key is "{player_uuid}|{key}"
	@Override
	public int getPlayerTimerRemainingTime(@Nonnull Player player, String key) {
		return getTimerRemainingTime(playerSpecificTimerKey(player, key));
	}

	@Override
	public int getPlayerTimerRemainingTime(@Nonnull UUID uuid, String key) {
		return getTimerRemainingTime(playerSpecificTimerKey(uuid, key));
	}

	@Override
	public @NotNull Optional<CompoundTag> getPlayerSpecificNBT(Player player) {
		return this.getPlayerSpecificNBT(player.getUUID());
	}

	@Nonnull
	@Override
	public Optional<CompoundTag> getPlayerSpecificNBT(UUID uuid) {
		if (uuid == null || uuid.equals(EMPTY_UUID)) return Optional.empty();
		if (this.playerSpecificNBT.containsKey(uuid)
				&& !this.playerSpecificNBT.get(uuid).isEmpty())
			return Optional.of(this.playerSpecificNBT.get(uuid));
		else return Optional.empty();
	}

	@Override
	public boolean hasPlayerSpecificNBT(Player player) {
		return hasPlayerSpecificNBT(player.getUUID());
	}

	@Override
	public boolean hasPlayerSpecificNBT(UUID uuid) {
		return uuid != null
				&& !uuid.equals(EMPTY_UUID)
				&& this.playerSpecificNBT.containsKey(uuid)
				&& !this.playerSpecificNBT.get(uuid).isEmpty();
	}

	@Override
	public ImmutableSet<UUID> getAllPlayersWithNBT() {
		return ImmutableSet.copyOf(this.playerSpecificNBT.keySet());
	}

	@Override
	public void putTimer(String key, int ticks) {
		String actualKey = key;
		if (key.contains("|")) {
			NaUtilsDebugStatics.errorOnce(LogUtils.getLogger(), String.format("CNFFTamableImpl: illegal general timer " +
					"\"%s\". \"|\" is reserved for player-specific timers. Removed \"|\".", key));
			actualKey = String.copyValueOf(key.toCharArray()).replaceAll("\\|", "");
		}
		CNFFTamable.super.putTimer(actualKey, ticks);
	}

	@Override
	public boolean hasPlayerTimer(Player player, String key)
	{
		return this.hasTimer(playerSpecificTimerKey(player, key));
	}

	@Override
	public boolean hasPlayerTimer(UUID uuid, String key) {
		return this.hasTimer(playerSpecificTimerKey(uuid, key));
	}

	@Override
	public void putPlayerTimer(Player player, String key, int ticks)
	{
		CNFFTamable.super.putTimer(playerSpecificTimerKey(player, key), ticks);
	}

	@Override
	public void putPlayerTimer(UUID uuid, String key, int ticks) {
		CNFFTamable.super.putTimer(playerSpecificTimerKey(uuid, key), ticks);
	}

	@Override
	public void removePlayerTimer(Player player, String key, boolean postEvent)
	{
		this.removeTimer(playerSpecificTimerKey(player, key), postEvent);
	}

	@Override
	public void removePlayerTimer(UUID uuid, String key, boolean postEvent) {
		this.removeTimer(playerSpecificTimerKey(uuid, key), postEvent);
	}

	@Override
	public List<String> getAllTimerKeys(Player player) {
		return this.timer.keySet().stream()
				.filter(key -> this.timer.get(key) != 0 && key.contains("|"))
				.map(str -> str.split("\\|"))
				.filter(s -> s.length > 1 && s[0].equals(player.getStringUUID()))
				.map(s -> s[1]).toList();
	}

	/**
	 * Get all players which have a <i>non-empty</i> timer in this mob. Note that this is not the nbt.
	 */
	@Override
	public List<UUID> getAllTimingPlayers() {
		return this.timer.keySet().stream()
				.filter(key -> this.timer.get(key) != 0 && key.contains("|"))
				.map(key -> key.split("\\|"))
				.map(split -> split.length > 0 ? NaUtilsMiscStatics.toOptionalUUID(split[0]).orElse(EMPTY_UUID) : EMPTY_UUID)
				.filter(uuid -> uuid != EMPTY_UUID).distinct().toList();
	}


	@Override
	public void setAlwaysHostileTo(@Nullable LivingEntity target) {
		this.alwaysHostileTo = Optional.ofNullable(target).map(LivingEntity::getUUID).orElse(EMPTY_UUID);
	}

	@Nullable
	@Override
	public final UUID getAlwaysHostileTo() {
		return this.alwaysHostileTo;
	}

	@Override
	public final void setForcePersistent(boolean value) {
		this.forcePersistent = value;
	}

	@Override
	public boolean isForcePersistent() {
		return this.forcePersistent;
	}

	@Nonnull
	@Override
	public final NFFTamingProcess getTamingProcess() {
		NFFTamingProcess res = NFFTamingMapping.getProcess(this.getEntity());
		if (res == null) throw new MissingRegistryException(String.format("CNFFTamable missing process: %s. Register the " +
				"entity type to NFFTamingMapping", this.getEntity().getType().getDescription().getString()));
		return res;
	}

	@Override
	public void tick() {
		super.tick();
		this.getTamingProcess().doServerTick(this.getEntity());
	}

	@Override
	public void onAngryAt(LivingEntity target, int forgivingTicks, MobSetAngerResult setResult) {
		super.onAngryAt(target, forgivingTicks, setResult);
		if (target instanceof Player player) {
			if (setResult.isHandled())
				this.getTamingProcess().onAngryAt(this.getEntity(), player, setResult.reason().orElse(null));
		}
	}

	@Override
	public void onForgive(UUID target, MobForgiveResult setResult) {
		super.onForgive(target, setResult);
	}

	@Override
	public final Map<String, Integer> getTimerMap() {
		return timer;
	}

}
