package net.sodiumzh.nff.services.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumzh.nautils.capability.CEntityTickingCapability;
import net.sodiumzh.nff.services.entity.capability.CAttributeMonitor;
import net.sodiumzh.nff.services.entity.capability.CHealingHandler;
import net.sodiumzh.nff.services.entity.capability.CLivingEntityDelayedActionHandler;
import net.sodiumzh.nff.services.entity.capability.CNFFPlayerModule;
import net.sodiumzh.nff.services.entity.taming.CNFFTamable;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.item.capability.CItemStackMonitor;
import net.sodiumzh.nff.services.level.CNFFLevelModule;

public class NFFCapRegistry {

	// Functional caps
	public static final Capability<CNFFTamable> CAP_BEFRIENDABLE_MOB = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<CHealingHandler> CAP_HEALING_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<CAttributeMonitor> CAP_ATTRIBUTE_MONITOR = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<CItemStackMonitor> CAP_ITEM_STACK_MONITOR = CapabilityManager.get(new CapabilityToken<>(){});
	
	// Caps for data storage only
	public static final Capability<CNFFTamedCommonData> CAP_BEFRIENDED_MOB_DATA = CapabilityManager.get(new CapabilityToken<>(){});
	
	// General functional capabilities
	public static final Capability<CNFFPlayerModule> CAP_BM_PLAYER = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<CNFFLevelModule> CAP_BM_LEVEL = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<CLivingEntityDelayedActionHandler> CAP_DELAYED_ACTION_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	
	static {
		CEntityTickingCapability.registerTicking(CAP_BEFRIENDED_MOB_DATA);
	}
	
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event)
	{
		// Entities
		event.register(CNFFTamable.class);
		event.register(CHealingHandler.class);
		event.register(CAttributeMonitor.class);
		//event.register(CBaubleDataCache.class);
		event.register(CNFFTamedCommonData.class);
		event.register(CItemStackMonitor.class);
		event.register(CNFFPlayerModule.class);
		event.register(CNFFLevelModule.class);
		event.register(CLivingEntityDelayedActionHandler.class);
	}
}
