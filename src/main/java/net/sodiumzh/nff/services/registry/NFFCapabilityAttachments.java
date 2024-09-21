package net.sodiumzh.nff.services.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.capability.CAttributeMonitorProvider;
import net.sodiumzh.nff.services.entity.capability.CNFFTamableProvider;
import net.sodiumzh.nff.services.entity.capability.CHealingHandlerProvider;
import net.sodiumzh.nff.services.entity.capability.CLivingEntityDelayedActionHandler;
import net.sodiumzh.nff.services.entity.capability.CNFFPlayerModule;
import net.sodiumzh.nff.services.entity.capability.wrapper.IAttributeMonitor;
import net.sodiumzh.nff.services.entity.capability.wrapper.ILivingDelayedActions;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.item.capability.CItemStackMonitor;
import net.sodiumzh.nff.services.item.capability.wrapper.IItemStackMonitor;
import net.sodiumzh.nff.services.level.CNFFLevelModule;

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
	
		if (event.getObject() instanceof LivingEntity living)
		{
			// Attribute change monitor
			if (living instanceof IAttributeMonitor)
			{
				CAttributeMonitorProvider prvd = new CAttributeMonitorProvider(living);
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_ATTRIBUTE_MONOTOR)
						, prvd);
			}
			// Item Stack monitor
			if (living instanceof IItemStackMonitor)
			{
				CItemStackMonitor.Prvd prvd1 = new CItemStackMonitor.Prvd(living);
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_ITEM_STACK_MONITOR), prvd1);
			}
			// Delay action handler
			if (living instanceof ILivingDelayedActions)
			{
				CLivingEntityDelayedActionHandler.Prvd prvd = new CLivingEntityDelayedActionHandler.Prvd(living);
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_DELAYED_ACTION_HANDLER), prvd);
			}
		}			
		
		
		// CNFFTamable
		if (event.getObject() instanceof Mob mob) {
			if (NFFTamingMapping.contains((EntityType<? extends Mob>) mob.getType())
					&& !(mob instanceof INFFTamed)) 
			{
				event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_TAMABLE),
						new CNFFTamableProvider(mob));
				if (NFFTamingMapping.contains((EntityType<? extends Mob>) mob.getType()) 
						&& NFFTamingMapping.getHandler((EntityType<? extends Mob>) mob.getType()) != null)
				{
					// Initialize capability (defined in handlers)
					/*mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> 
					{
						BefriendableMobRegistry.put(mob);
					});*/
				}
			}
		}

		if (event.getObject() instanceof INFFTamed bm)
		{
			// Temp data (CNFFTamedCommonData)
			// Renamed key in 0.x.25 from "cap_befriended_mob_temp_data" to "cap_befriended_mob_data"
			/*event.addCapability(new ResourceLocation(NFFServices.MOD_ID, "cap_befriended_mob_temp_data"),
					new CNFFTamedCommonData.Prvd(bef));*/
			event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_MOB_COMMON_DATA),
					new CNFFTamedCommonData.Prvd(bm));
			
			
			// CHealingHandler
			if (bm.healingHandlerClass() != null)
			{
				try
				{
					event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_HEALING_HANDLER), 
						new CHealingHandlerProvider(
							// Implementation class defined in INFFTamed implementation
							bm.healingHandlerClass().getDeclaredConstructor(LivingEntity.class).newInstance(bm.asMob()), 
							bm.asMob()));
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		// CBaubleDataCache
		/*if (event.getObject() instanceof IBaubleEquipable b)
		{
			event.addCapability(new ResourceLocation(NFFServices.MOD_ID, "cap_bauble_data_cache"), 
					new CBaubleDataCache.Prvd(b));
		}*/
		
		// CNFFPlayerModule
		if (event.getObject() instanceof Player p)
		{
			event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_PLAYER), 
					new CNFFPlayerModule.Prvd(p));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachLevelCapabilities(AttachCapabilitiesEvent<Level> event)
	{
		if (event.getObject() instanceof ServerLevel sl)
		{
			event.addCapability(new ResourceLocation(NFFServices.MOD_ID, KEY_NFF_LEVEL), 
					new CNFFLevelModule.Prvd(sl));
		}
	}
}
