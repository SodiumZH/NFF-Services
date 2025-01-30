package net.sodiumzh.nautils.entity.anger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nautils.annotation.CapabilityImplementation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@CapabilityImplementation(caps = CConditionalNeutralMob.class)
public class ConditionalNeutralMob extends MobAngerHandler implements CConditionalNeutralMob {

    public ConditionalNeutralMob(Mob mob, MobAngerRules rules) {
        super(mob, rules);
    }

    @Override
    public boolean isNeutralTo(LivingEntity entity) {
        return false;
    }




}
