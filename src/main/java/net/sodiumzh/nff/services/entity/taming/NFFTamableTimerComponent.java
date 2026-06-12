package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.entity.component.preset.EntityTimerComponent;

public class NFFTamableTimerComponent extends EntityTimerComponent<Mob> {
    public NFFTamableTimerComponent(Mob entity) {
        super(entity);
    }
}
