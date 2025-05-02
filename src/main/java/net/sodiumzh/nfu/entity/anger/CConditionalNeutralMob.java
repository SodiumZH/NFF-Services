package net.sodiumzh.nfu.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nfu.annotation.CapabilityInterface;

@CapabilityInterface
public interface CConditionalNeutralMob extends CMobAngerHandler {

    public boolean isNeutralTo(LivingEntity entity);

}
