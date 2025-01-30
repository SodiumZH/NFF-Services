package net.sodiumzh.nautils.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nautils.annotation.CapabilityInterface;

@CapabilityInterface
public interface CConditionalNeutralMob extends CMobAngerHandler {

    public boolean isNeutralTo(LivingEntity entity);

}
