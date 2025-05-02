package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.event.NFULivingEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NFFTamableAngryEvent extends NFULivingEvent<Mob> {

    @Nullable
    private final MobAngerReason reason;
    @Nonnull
    private final LivingEntity target;

    public NFFTamableAngryEvent(Mob entity, @Nonnull LivingEntity target, @Nullable MobAngerReason reason) {
        super(entity);
        this.reason = reason;
        this.target = target;
    }

    @Nullable
    public MobAngerReason getReason() {
        return reason;
    }

    public CNFFTamable getTamable() {
        return CNFFTamable.get(this.getEntity());
    }

    @Nonnull
    public LivingEntity getTarget() {
        return target;
    }
}
