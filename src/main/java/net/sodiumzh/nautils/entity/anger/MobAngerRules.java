package net.sodiumzh.nautils.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.registries.RegistryEntryCollection;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code MobAngerRules} controls mobs' behaviors about anger in different reasons.
 */
public class MobAngerRules {

    public static final int DEFAULT_FORGIVING_TICKS = 5 * 60 * 20; // 5 min
    public static final RegistryEntryCollection<MobAngerRules> RULES = RegistryEntryCollection.create(NaUtilsRegistries.MOB_ANGER_RULES, NaUtils.MOD_ID);

    public static final NaUtilsRegistry.Accessor<MobAngerRules> NO_ANGER = RULES.register("no_anger", MobAngerRules::new);

    /**
     * This mob will be angry with whom attacked it, with forgiving time 5 min.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerRules> ATTACKER = RULES.register("attacker",
            () -> new MobAngerRules().forReason(MobAngerReason.ATTACKED.get()).end());

    /**
     * This mob will be angry with whom attacked it and whom it attacked, with forgiving time 5 min.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerRules> ATTACKER_AND_ATTACKING = RULES.register("attacker_and_attacking",
            () -> new MobAngerRules().forReason(MobAngerReason.ATTACKED.get()).forReason(MobAngerReason.ATTACKING.get()).end());

    /**
     * This mob will be angry with whatever it is attacking, with forgiving time 5 min.
     */
    public static final NaUtilsRegistry.Accessor<MobAngerRules> HOSTILE = RULES.register("hostile",
            () -> new MobAngerRules().forReason(MobAngerReason.ATTACKED.get()).forReason(MobAngerReason.ATTACKING.get())
                    .forReason(MobAngerReason.TARGETING.get()).end());

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
