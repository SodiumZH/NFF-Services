package net.sodiumzh.nff.services.temp;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.mixin.event.entity.EntityFinalizeLoadingEvent;
import net.sodiumzh.nautils.mixin.event.entity.EntityLoadEvent;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

/**
 * Dedicated event handlers only for temporary solutions when API changes. Should be removed in final releases.
 */
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TempPortingEvents
{
	@SubscribeEvent
	public static void beforeLoad(EntityLoadEvent event)
	{
		if (!event.getNBT().getCompound("ForgeCaps").getCompound(NFFServices.MOD_ID + ":cap_befriended_mob_temp_data").isEmpty())
		{
			event.getNBT().getCompound("ForgeCaps").put(NFFServices.MOD_ID + ":" + "cap_befriended_mob_data", 
					event.getNBT().getCompound("ForgeCaps").getCompound(NFFServices.MOD_ID + ":cap_befriended_mob_temp_data").copy());
			event.getNBT().getCompound("ForgeCaps").remove(NFFServices.MOD_ID + ":" + "cap_befriended_mob_temp_data");
		}
	}
	
	// TODO Move it to CNFFTamedCommonData#deserializeNBT
	@SubscribeEvent
	public static void afterLoad(EntityFinalizeLoadingEvent event)
	{
		INFFTamed.ifBM(event.getEntity(), bm -> {
			bm.updateFromInventory();
			bm.init(bm.getOwnerUUID(), null);
			bm.setInit();
		});
	}
}
