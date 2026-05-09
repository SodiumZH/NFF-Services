package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.entity.anger.MobAngerHandlerComponent;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;

public class NFFTamableAngerHandlerComponent extends MobAngerHandlerComponent {
    public NFFTamableAngerHandlerComponent(Mob mob, MobAngerRules rules) {
        super(mob, rules);
    }

    public NFFTamableAngerHandlerComponent(Mob mob) {
        super(mob);
    }
}
