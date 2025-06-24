package net.sodiumzh.nfu.mixin.event.entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Posted at the start of {@link EnderMan#setTarget}. Its usage is similar to
 * {@link LivingChangeTargetEvent}
 * <p>{@link EnderMan#setTarget} is overridden, and there are some operations
 * setting synched data controlling the pose before {@link LivingChangeTargetEvent}
 * is posted. This event is posted before these operations, allowing to cancel them.
 * <p>Cancellable. If cancelled, the whole target setting operation will be cancelled,
 * and {@link LivingChangeTargetEvent} will not be posted.
 * <p>Note: {@code NFFTamedEnderManPreset} will not post this, its {@code super.setTarget()}
 * is invoked first.
 * @see  EnderMan#setTarget
 */
@Cancelable
public class EnderManStartSettingTargetEvent extends NFULivingEvent<EnderMan> {

    private final LivingEntity originalTarget;
    private LivingEntity newTarget;

    public EnderManStartSettingTargetEvent(EnderMan entity, LivingEntity originalTarget)
    {
        super(entity);
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
    }

    /**
     * {@return the new target of this entity.}
     */
    public LivingEntity getNewTarget()
    {
        return newTarget;
    }

    /**
     * Sets the new target this entity shall have.
     * @param newTarget The new target of this entity.
     */
    public void setNewTarget(LivingEntity newTarget)
    {
        this.newTarget = newTarget;
    }

    /**
     * {@return the original entity MC intended to use as a target before firing this event.}
     */
    public LivingEntity getOriginalTarget()
    {
        return originalTarget;
    }
}
