package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Posted <b>after</b> a {@link LivingEntity} takes damage. NOT cancellable.
 * <p>This event ensure that the damage has been actually taken. Note that anything
 * handled on this event will NOT affect the damage amount.
 */
public class LivingEntityDamageTakenEvent extends NFULivingEvent<LivingEntity> {

    private final float amount;
    private final DamageSource damageSource;

    public LivingEntityDamageTakenEvent(LivingEntity entity, float amount, DamageSource damageSource) {
        super(entity);
        this.amount = amount;
        this.damageSource = damageSource;
    }

    public float getAmount() {
        return amount;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

}
