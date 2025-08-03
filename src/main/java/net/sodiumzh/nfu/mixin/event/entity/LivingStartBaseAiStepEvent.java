package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Invoked at the head of {@link LivingEntity#aiStep()}. This event allows to insert code
 * right before base {@code LivingEntity.aiStep()} call and after any override actions before {@code super.aiStep()}.
 */
public class LivingStartBaseAiStepEvent extends NFULivingEvent<LivingEntity> {

    public LivingStartBaseAiStepEvent(LivingEntity entity) {
        super(entity);
    }

}
