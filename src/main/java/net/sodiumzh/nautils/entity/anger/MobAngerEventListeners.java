package net.sodiumzh.nautils.entity.anger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NaUtils.MOD_ID)
public class MobAngerEventListeners {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTick(LivingEvent.LivingTickEvent event)
    {
        if (!event.isCanceled() && event.getEntity() instanceof Mob mob && mob.getTarget() != null) {
            for (var cap : CMobAngerHandler.ALL_HANDLERS) {
                event.getEntity().getCapability(cap).ifPresent(c -> c.setAngryAt(mob.getTarget(), MobAngerReason.TARGETING.get()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttack(LivingHurtEvent event)
    {
        if (!event.isCanceled()
                && event.getSource().getEntity() != null
                && event.getSource().getEntity() instanceof LivingEntity src) {
            for (var cap : CMobAngerHandler.ALL_HANDLERS) {
                event.getEntity().getCapability(cap).ifPresent(c -> c.setAngryAt(src, MobAngerReason.ATTACKED.get()));
                src.getCapability(cap).ifPresent(c -> c.setAngryAt(event.getEntity(), MobAngerReason.ATTACKING.get()));
            }
        }
    }


}
