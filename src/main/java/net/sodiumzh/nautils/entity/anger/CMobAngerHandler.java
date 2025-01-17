package net.sodiumzh.nautils.entity.anger;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nautils.capability.CEntityTickingCapability;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

/**
 * {@code CMobAngerHandler} is a capability handling mechanics that mob can be angry with other living entities when some
 * event happens (e.g. attack). This capability doesn't to anything other than keeping an anger list.
 */
public interface CMobAngerHandler extends CEntityTickingCapability<Mob>, INBTSerializable<CompoundTag> {

    /**
     * Get the anger rules (which impacts the behavior of {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)}).
     */
    public MobAngerRules getRules();

    /**
     * Check if the mob is angry with a given target.
     */
    public boolean isAngryAt(LivingEntity target);

    /**
     * Set the mob to be angry with a target for a given reason. The behaviors are defined in the rules ({@code getRules}),
     * and if it's not a valid reason, it will not do anything.
     */
    public void setAngryAt(LivingEntity target, MobAngerReason reason);

    /**
     * Set the mob to be angry with a target with given ticks before forgiving. -1 = never forgive (unless {@code forgive()} is called).
     * <p>Note: this will bypass rules. If not necessary, use {@link CMobAngerHandler#setAngryAt(LivingEntity, MobAngerReason)} instead.
     */
    public void setAngryAt(LivingEntity target, int forgivingTicks);

    /**
     * Set the mob not to be angry with a target, no matter how long remained for forgiving and whether it's permanent.
     */
    public void forgive(LivingEntity target);

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
    public CMobAngerHandler setDamageThreshold(float value);

    /**
     * Save the anger list to an nbt.
     * @return a new nbt containing the anger list.
     */
    public CompoundTag saveAngerList();

    /**
     * Load the anger list from an nbt.
     */
    public void loadAngerList(CompoundTag nbt);

    public class Impl implements CMobAngerHandler {

        private final Mob mob;
        private final MobAngerRules rules;
        private final Map<UUID, MutableObject<Integer>> angerList = new HashMap<>();
        // Just for preventing tick() from repeatedly creating sets
        private final Set<UUID> tempRemoval = new HashSet<>();
        private float damageThreshold = 1e-3f;


        public Impl(Mob mob, MobAngerRules rules) {
            this.mob = mob;
            this.rules = rules;
        }

        @Override
        public void tick() {
            for (UUID key: angerList.keySet()) {
                int current = angerList.get(key).getValue();
                if (current == 0) {
                    this.tempRemoval.add(key);
                }
                else if (current > 0) {
                    angerList.get(key).setValue(current - 1);
                }
            }
            for (UUID removal: tempRemoval) angerList.remove(removal);
        }

        @Override
        public Mob getEntity() {
            return mob;
        }

        @Override
        public MobAngerRules getRules() {
            return rules;
        }

        @Override
        public boolean isAngryAt(LivingEntity target) {
            return angerList.containsKey(target.getUUID());
        }

        @Override
        public void setAngryAt(LivingEntity target, MobAngerReason reason) {
            int forgiving = this.rules.getForgivingTicks(reason, this.mob, target);
            setAngryAt(target, forgiving);
        }

        @Override
        public void setAngryAt(LivingEntity target, int forgivingTicks) {
            if (forgivingTicks != 0) {
                if (!angerList.containsKey(target.getUUID()))
                {
                    angerList.put(target.getUUID(), new MutableObject<>(forgivingTicks));
                }
                else if (forgivingTicks < 0 || forgivingTicks > angerList.get(target.getUUID()).getValue())
                {
                    angerList.get(target.getUUID()).setValue(forgivingTicks);
                }
            }
        }

        @Override
        public int getRemainingForgivingTicks(LivingEntity target) {
            return isAngryAt(target) ? angerList.get(target.getUUID()).getValue() : 0;
        }

        @Override
        public void forgive(LivingEntity target) {
            this.angerList.remove(target.getUUID());
        }

        @Override
        public float getDamageThreshold() {
            return this.damageThreshold;
        }

        @Override
        public CMobAngerHandler setDamageThreshold(float value) {
            this.damageThreshold = value;
            return this;
        }

        @Override
        public CompoundTag saveAngerList() {
            CompoundTag nbt = new CompoundTag();
            for (var e: angerList.entrySet())
            {
                nbt.put(e.getKey().toString(), IntTag.valueOf(e.getValue().getValue()));
            }
            return nbt;
        }

        @Override
        public void loadAngerList(CompoundTag nbt) {
            angerList.clear();
            for (var key: nbt.getAllKeys())
            {
                angerList.put(UUID.fromString(key), new MutableObject<>(nbt.getInt(key)));
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.saveAngerList();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.loadAngerList(nbt);
        }
    }

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
