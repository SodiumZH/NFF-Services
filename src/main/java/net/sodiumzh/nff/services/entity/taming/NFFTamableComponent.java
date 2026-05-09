package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.entity.anger.MobAngerHandlerComponent;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.entity.component.IEntityComponent;

public class NFFTamableComponent extends MobAngerHandlerComponent {

    public NFFTamableComponent(Mob mob, MobAngerRules rules) {
        super(mob, rules);
    }

    public NFFTamableComponent(Mob mob) {
        super(mob);
    }

}
