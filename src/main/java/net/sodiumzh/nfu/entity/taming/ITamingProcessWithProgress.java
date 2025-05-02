package net.sodiumzh.nfu.entity.taming;

import net.minecraft.world.entity.Mob;

import java.util.Optional;
import java.util.UUID;

/**
 * Indicates the taming process has a double value of 0-1 as progress for each player and each mob. Usually the progress
 * should be 0 by default, and the mob will be tamed by the player when the progress reaches 1.
 */
public interface ITamingProcessWithProgress<T extends Mob> {

    public ITamingProcess<T> asProcess();

    /**
     * Get progress (usually 0-1) if in process, or empty if not.
     */
    public Optional<Double> getProgressValue(T mob, UUID playerUUID);

    public void setProgressValue(T mob, UUID playerUUID, double value);

    public void removeProgressValue(T mob, UUID playerUUID);

    public default void setProgressIfAbsent(T mob, UUID playerUUID, double value) {
        if (getProgressValue(mob, playerUUID).isEmpty())
            this.setProgressValue(mob, playerUUID, value);
    }

    /**
     * Add a delta value to a progress value.
     * WARNING: this method will do nothing if the player is not in process.
     * WARNING: this method will not handle interruption or finalization if the progress reaches 0 or 1.
     */
    public default void addProgressValue(T mob, UUID playerUUID, double deltaValue)
    {
        this.setProgressValue(mob, playerUUID, this.getProgressValue(mob, playerUUID)
                .map(val -> val + deltaValue).orElse(deltaValue));
    }
}
