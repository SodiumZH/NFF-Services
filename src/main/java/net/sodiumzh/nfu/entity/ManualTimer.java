package net.sodiumzh.nfu.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A simple manually-updated timer attached to an entity.
 */
public class ManualTimer<T> {

    private Map<T, Integer> timer = new HashMap<>();
    private List<T> timedUpObjects = new ArrayList<>();
    private boolean serializable = false;
    private Function<T, String> keySerializer = null;
    private Function<String, T> keyDeserializer = null;

    public void addTimer(T key, int durationTicks) {
        timer.put(key, durationTicks);
    }

    public boolean hasTimer(T key) {
        return getRemainingTime(key) > 0;
    }

    public int getRemainingTime(T key) {
        return timer.getOrDefault(key, 0);
    }

    /**
     * Update the timer once. Should be invoked once in a tick operation.
     */
    public void update() {
        timedUpObjects.clear();
        Stream<Map.Entry<T, Integer>> entryStream = timer.entrySet().stream();
        entryStream.forEach(e -> {
            if (e.getValue() <= 1) {
                timer.remove(e.getKey());
                timedUpObjects.add(e.getKey());
            }
            else timer.put(e.getKey(), e.getValue() - 1);
        });
    }

    /**
     * Get keys that timed up on the last update. Recommended to call only right after update.
     */
    public List<T> getTimedUpObjects() {
        return List.copyOf(timedUpObjects);
    }

    public ManualTimer<T> setSerializable(@Nonnull Function<T, String> keySerializer,
                                          @Nonnull Function<String, T> keyDeserializer) {
        this.serializable = true;
        this.keySerializer = keySerializer;
        this.keyDeserializer = keyDeserializer;
        return this;
    }

    public CompoundTag serialize() {
        if (!this.serializable || this.keySerializer == null || this.keyDeserializer == null)
            throw new UnsupportedOperationException("NFU#ManualTimer: this timer doesn't support serialization.");
        CompoundTag nbt = new CompoundTag();
        this.timer.forEach((k, v) -> nbt.put(this.keySerializer.apply(k), IntTag.valueOf(v)));
        return nbt;
    }

    public void deserialize(CompoundTag nbt) {
        if (!this.serializable || this.keySerializer == null || this.keyDeserializer == null)
            throw new UnsupportedOperationException("NFU#ManualTimer: this timer doesn't support serialization.");
        this.timer.clear();
        nbt.getAllKeys().forEach(k -> timer.put(keyDeserializer.apply(k), ((IntTag)(nbt.get(k))).getAsInt()));
    }

}
