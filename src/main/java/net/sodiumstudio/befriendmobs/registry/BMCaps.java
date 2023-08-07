package net.sodiumstudio.befriendmobs.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumstudio.befriendmobs.entity.capability.CAttributeMonitor;
import net.sodiumstudio.befriendmobs.entity.capability.CBMPlayer;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendableMob;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendedMobData;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.CBaubleDataCache;
import net.sodiumstudio.befriendmobs.item.capability.CItemStackMonitor;

public class BMCaps {

	// Functional caps
	public static Capability<CBefriendableMob> CAP_BEFRIENDABLE_MOB = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<CHealingHandler> CAP_HEALING_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<CAttributeMonitor> CAP_ATTRIBUTE_MONITOR = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<CItemStackMonitor> CAP_ITEM_STACK_MONITOR = CapabilityManager.get(new CapabilityToken<>(){});
	
	// Caps for data storage only
	public static Capability<CBaubleDataCache> CAP_BAUBLE_DATA_CACHE = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<CBefriendedMobData> CAP_BEFRIENDED_MOB_TEMP_DATA = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<CBMPlayer> CAP_BM_PLAYER = CapabilityManager.get(new CapabilityToken<>(){});
	
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event)
	{
		// Entities
		event.register(CBefriendableMob.class);
		event.register(CHealingHandler.class);
		event.register(CAttributeMonitor.class);
		event.register(CBaubleDataCache.class);
		event.register(CBefriendedMobData.class);
		event.register(CItemStackMonitor.class);
		event.register(CBMPlayer.class);
	}
}
