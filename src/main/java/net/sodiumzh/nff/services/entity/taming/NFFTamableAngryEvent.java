package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.registry.NFFEntityComponents;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.component.EntityComponentEvent;
import net.sodiumzh.nfu.event.NFULivingEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NFFTamableAngryEvent extends EntityComponentEvent<Mob, NFFTamableAngerHandlerComponent> {

    @Nullable
    private final MobAngerReason reason;
    @Nonnull
    private final LivingEntity target;

    public NFFTamableAngryEvent(NFFTamableAngerHandlerComponent c, @Nonnull LivingEntity target, @Nullable MobAngerReason reason) {
        super(c);
        this.reason = reason;
        this.target = target;
    }

    @Nullable
    public MobAngerReason getReason() {
        return reason;
    }

    public NFFTamableComponent getTamable() {
        return NFFTamableComponent.getOptional(this.getEntity())
            .orElseGet(() -> NFFEntityComponents.TAMABLE.get().create(this.getEntity()));   // Nullity generally shouldn't happen
    }

    @Nonnull
    public LivingEntity getTarget() {
        return target;
    }
}
