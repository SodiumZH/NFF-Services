package net.sodiumzh.nff.services.eventlistener;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;
import net.sodiumzh.nff.services.item.NFFMobRespawnerItem;
import net.sodiumzh.nfu.mixin.event.entity.EntityStartTickEvent;
import net.sodiumzh.nfu.util.NFUEntityStatics;

// Handle Item, Item Stack and Item Entity events
@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFItemEventListeners
{
	
	@SubscribeEvent
	public static void onItemEntityJoinWorld(EntityJoinLevelEvent event)
	{
		// Initialize mob respawner invulnerable
		if (event.getEntity() instanceof ItemEntity ie)
		{
			NFFMobRespawnerInstance ins = NFFMobRespawnerInstance.createIfValid(ie.getItem());
			if (ins != null && ins.isInvulnerable())
			{
				ie.setInvulnerable(true);
			}	
		}
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void onServerItemEntityPreWorldTick(EntityStartTickEvent event)
	{
		if (event.getEntity() instanceof ItemEntity itementity)
		{
			// Handle respawner item entity falling into void
			if (itementity.getY() < (double)(itementity.level().getMinBuildHeight() - 1))
			{
				NFFMobRespawnerInstance ins = NFFMobRespawnerInstance.createIfValid(itementity.getItem());
				if (ins != null && ins.recoverInVoid())
				{
					// Lift onto y=64
					itementity.setPos(new Vec3(itementity.getX(), 64d, itementity.getZ()));
				if (!NFUEntityStatics.tryTeleportOntoGround(itementity, new Vec3(16d, 16d, 16d), 32))
					{
						// If cannot teleport onto ground, let it float
						itementity.setNoGravity(true);
					}
					itementity.setDeltaMovement(new Vec3(0d, 0d, 0d));

				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onItemEntityPickUp(EntityItemPickupEvent event)
	{
		if (event.getItem().getItem().getItem() instanceof NFFMobRespawnerItem)
		{
			/**  There was an unknown bug that player will still pick up item even if the inventory
			 *   is full, causing item permanent loss
			 **/
			if (event.getEntity().getInventory().getFreeSlot() == -1)
			{
				event.setCanceled(true);
			}
		}
	}
}
