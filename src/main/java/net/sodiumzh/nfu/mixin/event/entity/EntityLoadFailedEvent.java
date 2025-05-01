package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.event.NFUEntityEvent;
import net.sodiumzh.nfu.util.NFUDebugStatics;

/**
 * Posted when a failure happened during entity loading, allowing to manually fix the issues.
 */
public class EntityLoadFailedEvent extends NFUEntityEvent<Entity> {

    private final Throwable cause;
    private boolean shouldIgnore = false;
    private final CompoundTag readingNBT;

    public EntityLoadFailedEvent(Entity entity, Throwable cause, CompoundTag nbt) {
        super(entity);
        this.cause = cause;
        this.readingNBT = nbt;
    }

    /**
     * Get the throwable that caused this failure.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Ignore the failure and force the entity to be added to the level.
     * <p>Be very careful using this! Use this only when you know what you are ignoring, and
     * ensure the issue has been fixed manually.
     * @param shouldPrintStackTrace If true, the throwable will print stack trace to log.
     */
    public void ignore(boolean shouldPrintStackTrace) {
        this.shouldIgnore = true;
        NFUDebugStatics.errorOnce(EntityLoadFailedEvent.class, "NaUtils: Entity %s loading failed. Ignored.");
        if (shouldPrintStackTrace){
            cause.printStackTrace();
        }
    }

    public boolean isShouldIgnore() {
        return shouldIgnore;
    }

    public CompoundTag getReadingNBT() {
        return readingNBT;
    }
}
