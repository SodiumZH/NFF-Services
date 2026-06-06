package net.sodiumzh.nff.services.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumzh.nff.services.entity.capability.CAttributeMonitor;
import net.sodiumzh.nff.services.entity.capability.CHealingHandler;
import net.sodiumzh.nff.services.entity.capability.CLivingEntityDelayedActionHandler;
import net.sodiumzh.nff.services.entity.capability.CNFFPlayerModule;
import net.sodiumzh.nff.services.entity.taming.CNFFTamable;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.item.capability.CItemStackMonitor;
import net.sodiumzh.nff.services.level.CNFFLevelModule;
import net.sodiumzh.nfu.capability.CEntityTickingCapability;

public class NFFCapRegistry {

	public static final Capability<CItemStackMonitor> CAP_ITEM_STACK_MONITOR = CapabilityManager.get(new CapabilityToken<>(){});
	
	// Caps for data storage only
	@Deprecated
	public static final Capability<CNFFTamedCommonData> CAP_BEFRIENDED_MOB_DATA = CapabilityManager.get(new CapabilityToken<>(){});
	
	static {
		CEntityTickingCapability.registerTicking(CAP_BEFRIENDED_MOB_DATA);
		CEntityTickingCapability.registerTicking(CAP_ITEM_STACK_MONITOR);
		//CMobAngerHandler.register(CAP_BEFRIENDABLE_MOB);
	}
	
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event)
	{
		// Entities
		event.register(CNFFTamedCommonData.class);
		event.register(CNFFLevelModule.class);
	}
}
