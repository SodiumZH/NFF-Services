package net.sodiumzh.nfu.entity.anger;

import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

/**
 * Reasons that a mob set anger to an entity. This controls whether the mob will get angry with the target, and
 * how long should it take before the mob forgives the target. It has a registry {@link NFURegistries#MOB_ANGER_REASONS},
 * and new custom reasons should be registered on declaration.
 */
public class MobAngerReason {

    public static final NFURegistryEntryCollection<MobAngerReason> REASONS = NFURegistryEntryCollection.create(
            NFURegistries.MOB_ANGER_REASONS, NFULibrary.MOD_ID);

    /**
     * The anger target attacked the mob and dealt damage.
     */
    public static final NFURegistry.Accessor<MobAngerReason> ATTACKED = REASONS.register("attacked", MobAngerReason::new);

    /**
     * Mob attacked the anger target.
     */
    public static final NFURegistry.Accessor<MobAngerReason> ATTACKING = REASONS.register("attacking", MobAngerReason::new);

    /**
     * Happens EVERY TICK if the mob's attack target is the anger target.
     */
    public static final NFURegistry.Accessor<MobAngerReason> TARGETING = REASONS.register("targeting", MobAngerReason::new);

    /**
     * The anger target attacked the mob but didn't deal damage.
     */
    public static final NFURegistry.Accessor<MobAngerReason> HIT = REASONS.register("hit", MobAngerReason::new);

    /**
     * Mob attacked the anger target but didn't deal damage.
     */
    public static final NFURegistry.Accessor<MobAngerReason> HITTING = REASONS.register("hitting", MobAngerReason::new);

    /**
     * Mob is damaged by the thorns enchantment of the target. Thorns damage below the threshold is ignored.
     */
    public static final NFURegistry.Accessor<MobAngerReason> THORNS = REASONS.register("thorns", MobAngerReason::new);
}
