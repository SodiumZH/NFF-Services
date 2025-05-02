package net.sodiumzh.nfu.entity.anger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nfu.annotation.CapabilityInterface;
import net.sodiumzh.nfu.capability.CEntityTickingCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * {@code CMobAngerHandler} is a capability handling mechanics that mob can be angry with other living entities when some
 * event happens (e.g. attack). This capability doesn't to anything other than keeping an anger list.
 */
@CapabilityInterface
@AutoRegisterCapability
public interface CMobAngerHandler extends CEntityTickingCapability<Mob>, INBTSerializable<CompoundTag> {

    /**
     * Get the anger rules (which impacts the behavior of {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)}).
     */
    @Nonnull
    public MobAngerRules getAngerRules();

    /**
     * Set the anger rules (which impacts the behavior of {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)}).
     */
    public void setAngerRules(@Nonnull MobAngerRules rules);

    /**
     * Check if the mob is angry with a given target.
     */
    public boolean isAngryAt(LivingEntity target);

    /**
     * Set the mob to be angry with a target for a given reason and forgiving time.
     * <p>Note: it will <i>NOT</i> consider the anger rules.
     */
    public MobSetAngerResult setAngryAt(LivingEntity target, @Nullable MobAngerReason reason, int forgivingTicks);

    /**
     * Set the mob to be angry with a target for a given reason. The behaviors are defined in the rules ({@code getRules}),
     * and if it's not a valid reason, it will not do anything.
     */
    public MobSetAngerResult setAngryAt(LivingEntity target, MobAngerReason reason);

    /**
     * Set the mob to be angry with a target with given ticks before forgiving. -1 = never forgive (unless {@code forgive()} is called).
     * <p>Note: this will bypass rules. If not necessary, use {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)} instead.
     */
    public MobSetAngerResult setAngryAt(LivingEntity target, int forgivingTicks);

    /**
     * Set the mob not to be angry with a target, no matter how long remained for forgiving and whether it's permanent.
     */
    public MobForgiveResult forgive(LivingEntity target);

    /**
     * Get how long in ticks before the mob forgives the target. Returns 0 if not angry with the target.
     */
    public int getRemainingForgivingTicks(LivingEntity target);

    /**
     * Get the damage threshold above which (excluding) will be regarded as "attack", otherwise "hit".
     */
    public float getDamageThreshold();

    /**
     * Set the damage threshold above which (excluding) will be regarded as "attack", otherwise "hit".
     */
    public void setDamageThreshold(float value);

    /**
     * Save the anger list to an nbt.
     * @return a new nbt containing the anger list.
     */
    public CompoundTag saveAngerList();

    /**
     * Load the anger list from an nbt.
     */
    public void loadAngerList(CompoundTag nbt);

    /**
     * Custom actions on mob getting angry at the target.
     * @param target Target.
     * @param forgivingTicks How many ticks it should take to forgive the target. Negative - permanent anger.
     * @param reason Anger reason. Empty if it's calling without specifying a reason.
     */
    public void onAngryAt(LivingEntity target, int forgivingTicks, MobSetAngerResult reason);

    /**
     * Custom actions on mob forgiving the target.
     * @param target Target UUID.
     * @param isManual True if the forgiving is due of manually calling forgive() or setAngryAt(target, 0). False if
     *                 due to timer up.
     */
    public void onForgive(UUID target, MobForgiveResult isManual);

    static Set<Capability<? extends CMobAngerHandler>> ALL_HANDLERS = new HashSet<>();

    /**
     * Register a holder as anger handler, so that the default anger reasons (attacking, attacked, targeting) will be
     * auto handled. This also registers the capability as ticking, and you don't need to manually call {@link CEntityTickingCapability#registerTicking}.
     */
    public static void register(Capability<? extends CMobAngerHandler> holder)
    {
        CEntityTickingCapability.registerTicking(holder);
        ALL_HANDLERS.add(holder);
    }

}
