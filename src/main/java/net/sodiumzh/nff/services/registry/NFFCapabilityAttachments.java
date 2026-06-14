package net.sodiumzh.nff.services.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.level.CNFFLevelModule;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import java.util.Map;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFCapabilityAttachments {

	public static final String KEY_ATTRIBUTE_MONOTOR = "attribute_monitor";
	public static final String KEY_ITEM_STACK_MONITOR = "item_stack_monitor";
	public static final String KEY_DELAYED_ACTION_HANDLER = "delayed_action_handler";
	public static final String KEY_NFF_TAMABLE = "nff_tamable";
	public static final String KEY_NFF_MOB_COMMON_DATA = "nff_mob_common_data";
	public static final String KEY_HEALING_HANDLER = "healing_handler";
	public static final String KEY_NFF_PLAYER = "nff_player";
	public static final String KEY_NFF_LEVEL = "nff_level";
	
	public static final String KEY_ATTRIBUTE_MONOTOR_LEGACY = "cap_attribute_monitor";
	public static final String KEY_ITEM_STACK_MONITOR_LEGACY = "cap_item_stack_monitor";
	public static final String KEY_DELAYED_ACTION_HANDLER_LEGACY = "cap_delay_action_handler";
	public static final String KEY_NFF_TAMABLE_LEGACY = "cap_befriendable";
	public static final String KEY_NFF_MOB_COMMON_DATA_LEGACY = "cap_befriended_mob_data";
	public static final String KEY_HEALING_HANDLER_LEGACY = "cap_healing_handler";
	public static final String KEY_NFF_PLAYER_LEGACY = "cap_bm_player";
	public static final String KEY_NFF_LEVEL_LEGACY = "cap_bm_level";
	
	// Attach capabilities
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachLivingEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {

		// CNFFTamable
		/*if (event.getObject() instanceof Mob mob) {
			if (NFFTamingMapping.containsAfter((EntityType<? extends Mob>) mob.getType())
					&& mob instanceof INFFTamed bm)
			{
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_MOB_COMMON_DATA),
					new CNFFTamedCommonData.Prvd(bm));
			}
		}*/
	}

	@SubscribeEvent
	public static void attachLevelCapabilities(AttachCapabilitiesEvent<Level> event)
	{
		if (event.getObject() instanceof ServerLevel sl
				&& !getExistingCaps(event).containsKey(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_LEVEL)))
			// A duplicate key error is often reported here, so make a check
			// TODO: Will it have side effects?
		{
			event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_LEVEL), 
					new CNFFLevelModule.Prvd(sl));
		}
	}

	public static Map<ResourceLocation, ICapabilityProvider> getExistingCaps(AttachCapabilitiesEvent<?> event) {
		return NFUReflectionStatics.forceGet(event, AttachCapabilitiesEvent.class, "caps")
				.cast();
	}
}
