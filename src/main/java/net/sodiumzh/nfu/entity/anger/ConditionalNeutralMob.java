package net.sodiumzh.nfu.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.annotation.CapabilityImplementation;

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
