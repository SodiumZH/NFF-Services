package net.sodiumzh.nff.services.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.level.CNFFLevelModule;
import net.sodiumzh.nfu.capability.CEntityTickingCapability;

public class NFFCapRegistry {

	// Caps for data storage only
	@Deprecated
	public static final Capability<CNFFTamedCommonData> CAP_BEFRIENDED_MOB_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<CNFFLevelModule> CAP_LEVEL = CapabilityManager.get(new CapabilityToken<>(){});

	static {
		CEntityTickingCapability.registerTicking(CAP_BEFRIENDED_MOB_DATA);
	}
	
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event)
	{
		// Entities
		event.register(CNFFTamedCommonData.class);
		event.register(CNFFLevelModule.class);
	}
}
