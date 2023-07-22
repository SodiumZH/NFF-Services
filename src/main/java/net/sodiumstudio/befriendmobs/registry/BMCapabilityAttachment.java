package net.sodiumstudio.befriendmobs.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.befriending.registry.BefriendableMobRegistry;
import net.sodiumstudio.befriendmobs.entity.befriending.registry.BefriendingTypeRegistry;
import net.sodiumstudio.befriendmobs.entity.capability.CAttributeMonitorProvider;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendableMobProvider;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendedMobTempData;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerProvider;
import net.sodiumstudio.befriendmobs.item.baublesystem.CBaubleDataCache;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleHolder;
import net.sodiumstudio.befriendmobs.item.capability.CItemStackMonitor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BMCapabilityAttachment {

	// Attach capabilities
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachLivingEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
	
		if (event.getObject() instanceof LivingEntity living)
		{
			// Attribute change monitor
			CAttributeMonitorProvider prvd = new CAttributeMonitorProvider(living);
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_attribute_monitor")
					, prvd);		
			// Item Stack monitor
			CItemStackMonitor.Prvd prvd1 = new CItemStackMonitor.Prvd(living);
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_item_stack_monitor"), prvd1);
		}			
		
		
		// CBefriendableMob
		if (event.getObject() instanceof Mob mob) {
			if (BefriendingTypeRegistry.contains((EntityType<? extends Mob>) mob.getType())
					&& !(mob instanceof IBefriendedMob)) 
			{
				event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_befriendable"),
						new CBefriendableMobProvider(mob));
				if (BefriendingTypeRegistry.contains((EntityType<? extends Mob>) mob.getType()) 
						&& BefriendingTypeRegistry.getHandler((EntityType<? extends Mob>) mob.getType()) != null)
				{
					// Initialize capability (defined in handlers)
					mob.getCapability(BMCaps.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> 
					{
						BefriendableMobRegistry.put(mob);
					});
				}
			}
		}

		if (event.getObject() instanceof IBefriendedMob bef)
		{
			// Temp data (CBefriendedMobTempData)
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_befriended_mob_temp_data"),
					new CBefriendedMobTempData.Prvd(bef));
			// CHealingHandler
			if (bef.healingHandlerClass() != null)
			{
				try
				{
					event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_healing_handler"), 
						new CHealingHandlerProvider(
							// Implementation class defined in IBefriendedMob implementation
							bef.healingHandlerClass().getDeclaredConstructor(LivingEntity.class).newInstance(bef.asMob()), 
							bef.asMob()));
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		// CBaubleDataCache
		if (event.getObject() instanceof IBaubleHolder b)
		{
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_bauble_data_cache"), 
					new CBaubleDataCache.Prvd(b));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
	{
	}
}