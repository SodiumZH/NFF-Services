package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.entity.anger.MobAngerHandlerComponent;
import net.sodiumzh.nfu.entity.anger.MobForgiveResult;
import net.sodiumzh.nfu.entity.anger.MobSetAngerResult;

import java.util.UUID;

public class NFFTamableAngerHandlerComponent extends MobAngerHandlerComponent {

    public NFFTamableAngerHandlerComponent(Mob mob) {
        super(mob);
        this.setAngerRules(NFFTamingMapping.getProcess(this.getEntity()).getAngerRules());
    }

    @Override
    public void onAngryAt(LivingEntity target, int forgivingTicks, MobSetAngerResult setResult) {
        super.onAngryAt(target, forgivingTicks, setResult);
        if (target instanceof Player player) {
            if (setResult.isHandled()) {
                MinecraftForge.EVENT_BUS.post(
                    new NFFTamableAngryEvent(this.getEntity(), target, setResult.reason().orElse(null)));
                if (this.getParent().orElseThrow(() -> new IllegalStateException("missing parent")) instanceof NFFTamableComponent c) {
                    c.getTamingProcess().onAngryAt(this.getEntity(), player, setResult.reason().orElse(null));
                }
                else throw new IllegalStateException("NFFTamableAngerHandlerComponent must be attached to a NFFTamableComponent.");
            }
        }
    }

    @Override
    public void onForgive(UUID target, MobForgiveResult setResult) {
        super.onForgive(target, setResult);
    }

}
