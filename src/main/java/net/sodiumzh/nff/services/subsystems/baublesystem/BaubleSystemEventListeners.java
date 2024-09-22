package net.sodiumzh.nff.services.subsystems.baublesystem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.annotation.DontCallManually;
import net.sodiumzh.nff.services.NFFServices;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class BaubleSystemEventListeners
{

	@SuppressWarnings("unchecked")
	@DontCallManually
	@SubscribeEvent
	public static void attachLivingEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Mob mob)
		{
			if (BaubleEquippableMobRegistries.containsMobType(mob.getClass()))
			{
				CBaubleEquippableMobPrvd prvd = new CBaubleEquippableMobPrvd(mob);
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, "cap_bauble_equippable_mob")
						, prvd);
			}
		}
	}
	
	@DontCallManually
	@SubscribeEvent
	public static void onLivingTick(LivingTickEvent event)
	{
		event.getEntity().getCapability(BaubleSystemCapabilities.CAP_BAUBLE_EQUIPPABLE_MOB).ifPresent(cap -> cap.tick());
	}
}
