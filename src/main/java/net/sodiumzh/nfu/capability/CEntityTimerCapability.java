package net.sodiumzh.nfu.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nfu.annotation.DontCallManually;
import net.sodiumzh.nfu.annotation.DontOverride;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@code CEntityTimerCapability} is a capability template that has a timer which updates every tick.
 * <p>It a ticking capability and needs to register using {@link CEntityTickingCapability#registerTicking}, but
 * the {@code tickTimer} method will be auto called on tick together with {@code tick} and don't need to be manually called.
 * <p>Capabilities using this interface need a field of timer map (i.e. a {@code Map<String, Integer>}) and make {@code getTimerMap}
 * return it.
 */
@AutoRegisterCapability
public interface CEntityTimerCapability<T extends Entity> extends CEntityTickingCapability<T> {

    @DontCallManually
    public Map<String, Integer> getTimerMap();

    @DontOverride
    public default int getTimerRemainingTime(String key) {
        return getTimerMap().getOrDefault(key, 0);
    }

    /**
     * Check if the timer exists with a given key.
     */
    @DontOverride
    public default boolean hasTimer(String key) {
        return getTimerRemainingTime(key) != 0;
    }

    /**
     * Set the given timer. If the timer exists, the remaining ticks will be overwritten. Negative = add a permanent entry
     * that will never expire until manually removed.
     * <p>Note: if 0 is input, it will end the timer WITHOUT posting event. This operation is not recommended, and
     * use {@code removeTimer} instead.
     */
    @DontOverride
    public default void setTimer(String key, int ticks) {
        if (ticks != 0)
            getTimerMap().put(key, ticks);
        else removeTimer(key, false);
    }

    /**
     * Put an entry to timer, but will not overwrite if the key exists and the remaining time is longer than input.
     * Negative = add a permanent entry that will never expire until manually removed.
     */
    @DontOverride
    public default void safeSetTimer(String key, int ticks) {
        if (!(getTimerRemainingTime(key) > 0 && ticks > 0 && ticks < getTimerRemainingTime(key)))
            setTimer(key, ticks);
    }

    /**
     * Remove a timer of given key.
     */
    @DontOverride
    public default void removeTimer(String key, boolean postEvent) {
        this.getTimerMap().remove(key);
        if (postEvent) MinecraftForge.EVENT_BUS.post(new ExpireEvent(this, key));
    }

    @DontOverride
    public default void tickTimer()
    {
        var map = getTimerMap();
        Set<String> removal = new HashSet<>();
        for (String key: map.keySet()) {
            int oldVal = map.get(key);
            if (oldVal > 0)
                map.put(key, oldVal - 1);
            else if (oldVal == 0) {
                removal.add(key);
            }
        }
        for (String key: removal) {
            map.remove(key);
        }
        for (String key: removal) {
            MinecraftForge.EVENT_BUS.post(new ExpireEvent(this, key));
        }
    }

    /**
     * Save the current timer data to NBT.
     * @return a {@link CompoundTag} containing the timer data.
     */
    @DontOverride
    public default CompoundTag saveTimer() {
        CompoundTag nbt = new CompoundTag();
        var map = getTimerMap();
        for (String key: map.keySet()) {
            nbt.put(key, IntTag.valueOf(map.get(key)));
        }
        return nbt;
    }

    /**
     * Load timer data from NBT. Note that this action will clear current timer before loading!
     * @param nbt NBT containing the timer data. It must be generated from {@code saveTimer}, otherwise
     *            it will cause error!
     */
    @DontOverride
    public default void loadTimerFromNBT(CompoundTag nbt) {
        var map = getTimerMap();
        map.clear();
        for (String key: nbt.getAllKeys()) {
            map.put(key, nbt.getInt(key));
        }
    }

    public static class ExpireEvent extends Event {
        private final CEntityTimerCapability<?> cap;
        private final String key;

        public ExpireEvent(CEntityTimerCapability<?> cap, String key) {
            this.cap = cap;
            this.key = key;
        }

        public CEntityTimerCapability<?> getCapability() {
            return cap;
        }

        public String getKey() {
            return key;
        }
    }
}
