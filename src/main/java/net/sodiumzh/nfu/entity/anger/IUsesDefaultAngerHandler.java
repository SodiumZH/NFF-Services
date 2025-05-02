package net.sodiumzh.nfu.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nfu.entity.IMobSpecific;
import net.sodiumzh.nfu.registry.NFUCaps;

import javax.annotation.Nonnull;

public interface IUsesDefaultAngerHandler extends IMobSpecific<Mob> {

    /**
     * Get the anger rules. Override in the mob class to configure rules.
     */
    @Nonnull
    public MobAngerRules getAngerRules();

    /**
     * Get the capability instance of the default handler.
     */
    @Nonnull
    @DontOverride
    public default CMobAngerHandler getDefaultAngerHandler() {
        return this.asMob().getCapability(NFUCaps.CAP_MOB_DEFAULT_ANGER_HANDLER)
                .orElseGet(() -> new MobAngerHandler(this.asMob(), this.getAngerRules()));
    }

    /**
     * Check if the mob is angry with a given target.
     */
    @DontOverride
    public default boolean getAngryAt(LivingEntity target) {
        return this.getDefaultAngerHandler().isAngryAt(target);
    }

    /**
     * Set the mob to be angry with a target for a given reason. The behaviors are defined in the rules ({@code getRules}),
     * and if it's not a valid reason, it will not do anything.
     */
    @DontOverride
    public default void setAngryAt(LivingEntity target, MobAngerReason reason) {
        this.getDefaultAngerHandler().setAngryAt(target, reason);
    }

    /**
     * Set the mob to be angry with a target with given ticks before forgiving. -1 = never forgive (unless {@code forgive()} is called).
     * <p>Note: this will bypass rules. If not necessary, use {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)} instead.
     */
    @DontOverride
    public default void setAngryAt(LivingEntity target, int forgivingTicks) {
        this.getDefaultAngerHandler().setAngryAt(target, forgivingTicks);
    }

    /**
     * Set the mob not to be angry with a target, no matter how long remained for forgiving and whether it's permanent.
     */
    @DontOverride
    public default void forgive(LivingEntity target) {
        this.getDefaultAngerHandler().forgive(target);
    }

    /**
     * Get the damage threshold above (excluding) which will be regarded as "attack", otherwise "hit".
     */
    public default float getDamageThreshold() {
        return this.getDefaultAngerHandler().getDamageThreshold();
    }

    /**
     * Set the damage threshold above (excluding) which will be regarded as "attack", otherwise "hit".
     */
    public default void setDamageThreshold(float value) {
        this.getDefaultAngerHandler().setDamageThreshold(value);
    }

}
