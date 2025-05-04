package net.sodiumzh.nfu.entity.anger;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nfu.capability.NFUEntityCapProvider;

import javax.annotation.Nonnull;

public class CMobAngerHandlerProvider extends NFUEntityCapProvider<Mob, CMobAngerHandler> {

    public CMobAngerHandlerProvider(Mob entity, Capability<CMobAngerHandler> holder, @Nonnull MobAngerRules angerRules, float damageThreshold) {
        super(entity, holder, () -> new MobAngerHandler(entity, angerRules));
        this.getCapInstance().setDamageThreshold(damageThreshold);
    }

    public CMobAngerHandlerProvider(Mob entity, Capability<CMobAngerHandler> holder, @Nonnull MobAngerRules angerRules) {
        super(entity, holder, () -> new MobAngerHandler(entity, angerRules));
    }

}
