package net.sodiumzh.nfu.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code MobAngerRules} controls the mob's behaviors about anger for different reasons.
 */
public class MobAngerRules {

    public static final int DEFAULT_FORGIVING_TICKS = 5 * 60 * 20; // 5 min
    public static final NFURegistryEntryCollection<MobAngerRules> RULES = NFURegistryEntryCollection.create(NFURegistries.MOB_ANGER_RULES, NFULibrary.MOD_ID);

    public static final NFURegistry.Accessor<MobAngerRules> NO_ANGER = RULES.register("no_anger", MobAngerRules::new);

    /**
     * This mob will be angry with whom attacked it even without actual damage (defined in {@link CMobAngerHandler#getDamageThreshold()}),
     * with forgiving time 5 min.
     */
    public static final NFURegistry.Accessor<MobAngerRules> ATTACKER = RULES.register("attacker",
            () -> new MobAngerRules()
                    .forReason(MobAngerReason.ATTACKED.get())
                    .forReason(MobAngerReason.HIT.get())
                    .end());

    /**
     * This mob will be angry with whom attacked it only with actual damage (defined in {@link CMobAngerHandler#getDamageThreshold()}),
     * with forgiving time 5 min.
     */
    public static final NFURegistry.Accessor<MobAngerRules> ATTACKER_DAMAGED = RULES.register("attacked_damaged",
            () -> new MobAngerRules()
                    .forReason(MobAngerReason.ATTACKED.get())
                    .end());

    /**
     * This mob will be angry with whom attacked it even without damage (defined in {@link CMobAngerHandler#getDamageThreshold()}),
     * and whom it attacked (even without damage), with forgiving time 5 min.
     */
    public static final NFURegistry.Accessor<MobAngerRules> ATTACKER_AND_ATTACKING = RULES.register("attacker_and_attacking",
            () -> new MobAngerRules()
                    .forReason(MobAngerReason.ATTACKED.get())
                    .forReason(MobAngerReason.HIT.get())
                    .forReason(MobAngerReason.ATTACKING.get())
                    .forReason(MobAngerReason.HITTING.get())
                    .end());

    /**
     * This mob will be angry with whom attacked it with damage (defined in {@link CMobAngerHandler#getDamageThreshold()}),
     * and whom it attacked (even without damage), with forgiving time 5 min.
     */
    public static final NFURegistry.Accessor<MobAngerRules> ATTACKER_DAMAGED_AND_ATTACKING = RULES.register("attacker_and_attacking",
            () -> new MobAngerRules()
                    .forReason(MobAngerReason.ATTACKED.get())
                    .forReason(MobAngerReason.ATTACKING.get())
                    .forReason(MobAngerReason.HITTING.get())
                    .end());

    /**
     * This mob will be angry with whatever it is attacking, with forgiving time 5 min.
     */
    public static final NFURegistry.Accessor<MobAngerRules> HOSTILE = RULES.register("hostile",
            () -> new MobAngerRules()
                    .forReason(MobAngerReason.ATTACKED.get())
                    .forReason(MobAngerReason.HIT.get())
                    .forReason(MobAngerReason.ATTACKING.get())
                    .forReason(MobAngerReason.HITTING.get())
                    .forReason(MobAngerReason.TARGETING.get())
                    .end());

    private final Map<MobAngerReason, TriFunction<MobAngerReason, Mob, LivingEntity, Integer>> table = new HashMap<>();
    private boolean ended = false;

    /**
     * Declare that the mob will get angry for the given reason.
     * @param reason Reason to get angry.
     * @param forgivingTicksGetter Function to get how long it should take to forgive the target.
     * @return {@code this}.
     */
    public MobAngerRules forReason(MobAngerReason reason, TriFunction<MobAngerReason, Mob, LivingEntity, Integer> forgivingTicksGetter)
    {
        if (ended) throw illegalModification();
        table.put(reason, forgivingTicksGetter);
        return this;
    }

    /**
     * Declare that the mob will get angry for the given reason.
     * @param reason Reason to get angry.
     * @param forgivingTicks How long it should take to forgive the target.
     * @return {@code this}.
     */
    public MobAngerRules forReason(MobAngerReason reason, int forgivingTicks)
    {
        if (ended) throw illegalModification();
        table.put(reason, (r, m, l) -> forgivingTicks);
        return this;
    }

    /**
     * Declare that the mob will get angry for the given reason, with 5 minutes before forgiving the target.
     * @param reason Reason to get angry.
     * @return {@code this}.
     */
    public MobAngerRules forReason(MobAngerReason reason)
    {
        return forReason(reason, DEFAULT_FORGIVING_TICKS);
    }

    private IllegalStateException illegalModification()
    {
        return new IllegalStateException("MobAngerRules#forReason: Illegal operation as construction has ended. Consider listening to MobAngerRulesEvent for external modification.");
    }

    /**
     * Label this rules as construction ended. This operation posts {@link MobAngerRulesEvent} for external modification,
     * and blocks in-place {@code forReason} calls after this, in order to prevent accident modification of the registry entries.
     */
    public MobAngerRules end() {
        MinecraftForge.EVENT_BUS.post(new MobAngerRulesEvent(this));
        this.ended = true;
        return this;
    }

    /**
     * Get how long in ticks the mob should forgive the player. -1 means never forgive (until {@link CMobAngerHandler#}). 0 means the mob should not
     * be angry with the target for this reason.
     */
    public int getForgivingTicks(MobAngerReason reason, Mob mob, LivingEntity target)
    {
        if (!table.containsKey(reason))
            return 0;
        else return table.get(reason).apply(reason, mob, target);
    }




}
