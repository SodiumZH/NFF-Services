package net.sodiumzh.nfu.entity.anger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A default implementation of {@code CMobAngerHandler}.
 */
public class MobAngerHandler implements CMobAngerHandler {

    private final Mob mob;
    private MobAngerRules rules;
    private final Map<UUID, MutableObject<Integer>> angerList = new HashMap<>();
    // Just for preventing tick() from repeatedly creating sets
    private final Set<UUID> tempRemoval = new HashSet<>();
    private float damageThreshold = 1e-3f;

    public MobAngerHandler(Mob mob, MobAngerRules rules) {
        this.mob = mob;
        this.rules = rules;
    }

    @Override
    public void tick() {
        for (UUID key : angerList.keySet()) {
            int current = angerList.get(key).getValue();
            if (current == 0) {
                this.tempRemoval.add(key);
            } else if (current > 0) {
                angerList.get(key).setValue(current - 1);
            }
        }
        for (UUID removal : tempRemoval) {
            angerList.remove(removal);
            this.onForgive(removal, new MobForgiveResult(removal, true, false));
        }
    }

    @Override
    public final Mob getEntity() {
        return mob;
    }

    @Nonnull
    @Override
    public final MobAngerRules getAngerRules() {
        return rules;
    }

    @Override
    public void setAngerRules(@Nonnull MobAngerRules rules) {
        this.rules = rules;
    }

    @Override
    public final boolean isAngryAt(LivingEntity target) {
        return angerList.containsKey(target.getUUID());
    }

    @Override
    public MobSetAngerResult setAngryAt(LivingEntity target, @Nullable MobAngerReason reason, int forgivingTicks) {
        if (forgivingTicks != 0) {
            if (setAngryInternal(target, forgivingTicks))
            {
                MobSetAngerResult res = new MobSetAngerResult(target.getUUID(), true, Optional.ofNullable(reason));
                this.onAngryAt(target, forgivingTicks, res);
                return res;
            }
            else return new MobSetAngerResult(target.getUUID(), false, Optional.ofNullable(reason));
        }
        return new MobSetAngerResult(target.getUUID(), false, Optional.ofNullable(reason));
    }

    @Override
    public MobSetAngerResult setAngryAt(LivingEntity target, MobAngerReason reason) {
        return this.setAngryAt(target, reason, this.rules.getForgivingTicks(reason, this.mob, target));
    }

    @Override
    public final MobSetAngerResult setAngryAt(LivingEntity target, int forgivingTicks) {
        if (setAngryInternal(target, forgivingTicks)) {
            MobSetAngerResult res = new MobSetAngerResult(target.getUUID(), true, Optional.empty());
            this.onAngryAt(target, forgivingTicks, res);
            return res;
        }
        return new MobSetAngerResult(target.getUUID(), false, Optional.empty());
    }

    private boolean setAngryInternal(LivingEntity target, int forgivingTicks) {
        if (forgivingTicks == 0) return false;
        if (!angerList.containsKey(target.getUUID())) {
            angerList.put(target.getUUID(), new MutableObject<>(forgivingTicks));
            return true;
        } else if (forgivingTicks < 0 || forgivingTicks > angerList.get(target.getUUID()).getValue()) {
            angerList.get(target.getUUID()).setValue(forgivingTicks);
            return true;
        }
        return false;
    }

    @Override
    public final int getRemainingForgivingTicks(LivingEntity target) {
        return isAngryAt(target) ? angerList.get(target.getUUID()).getValue() : 0;
    }

    @Override
    public final MobForgiveResult forgive(LivingEntity target) {
        if (this.angerList.containsKey(target.getUUID())) {
            this.angerList.remove(target.getUUID());
            MobForgiveResult res = new MobForgiveResult(target.getUUID(), true, true);
            this.onForgive(target.getUUID(), res);
        }
        return new MobForgiveResult(target.getUUID(), false, true);
    }

    @Override
    public final float getDamageThreshold() {
        return this.damageThreshold;
    }

    @Override
    public final void setDamageThreshold(float value) {
        this.damageThreshold = value;
    }

    @Override
    public CompoundTag saveAngerList() {
        CompoundTag nbt = new CompoundTag();
        for (var e : angerList.entrySet()) {
            nbt.put(e.getKey().toString(), IntTag.valueOf(e.getValue().getValue()));
        }
        return nbt;
    }

    @Override
    public void loadAngerList(CompoundTag nbt) {
        angerList.clear();
        for (var key : nbt.getAllKeys()) {
            angerList.put(UUID.fromString(key), new MutableObject<>(nbt.getInt(key)));
        }
    }

    @Override
    public void onAngryAt(LivingEntity target, int forgivingTicks, MobSetAngerResult setResult) {

    }

    @Override
    public void onForgive(UUID target, MobForgiveResult setResult) {

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
