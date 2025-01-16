package net.sodiumzh.nautils.entity.anger;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nautils.capability.NaUtilsEntityCapProvider;

import javax.annotation.Nonnull;

public class CMobAngerHandlerProvider extends NaUtilsEntityCapProvider<Mob, CMobAngerHandler> {

    public CMobAngerHandlerProvider(Mob entity, Capability<CMobAngerHandler> holder, @Nonnull MobAngerRules angerRules, float damageThreshold) {
        super(entity, holder, () -> new CMobAngerHandler.Impl(entity, angerRules));
        this.getCapInstance().setDamageThreshold(damageThreshold);
    }

    public CMobAngerHandlerProvider(Mob entity, Capability<CMobAngerHandler> holder, @Nonnull MobAngerRules angerRules) {
        super(entity, holder, () -> new CMobAngerHandler.Impl(entity, angerRules));
    }

}
