package net.sodiumzh.nautils.entity.anger;

import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.registries.RegistryEntryCollection;

/**
 * Reasons that a mob set anger to an entity. This controls whether the mob will get angry with the target, and
 * how long should it take before the mob forgives the target. It has a registry {@link NaUtilsRegistries#MOB_ANGER_REASONS},
 * and new custom reasons should be registered on declaration.
 */
public class MobAngerReason {

    public static final RegistryEntryCollection<MobAngerReason> REASONS = RegistryEntryCollection.create(
            NaUtilsRegistries.MOB_ANGER_REASONS, NaUtils.MOD_ID);

    /**
     * The anger target attacked the mob and dealt damage.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerReason> ATTACKED = REASONS.register("attacked", MobAngerReason::new);

    /**
     * Mob attacked the anger target.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerReason> ATTACKING = REASONS.register("attacking", MobAngerReason::new);

    /**
     * Happens EVERY TICK if the mob's attack target is the anger target.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerReason> TARGETING = REASONS.register("targeting", MobAngerReason::new);

    /**
     * The anger target attacked the mob but didn't deal damage.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerReason> HIT = REASONS.register("hit", MobAngerReason::new);

    /**
     * Mob attacked the anger target but didn't deal damage.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerReason> HITTING = REASONS.register("hitting", MobAngerReason::new);

}
