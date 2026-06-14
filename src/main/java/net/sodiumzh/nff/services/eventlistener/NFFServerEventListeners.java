package net.sodiumzh.nff.services.eventlistener;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFServerEventListeners 
{

	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.level instanceof ServerLevel serverlevel && event.phase.equals(TickEvent.Phase.END))
		{
            serverlevel.getCapability(NFFCapRegistry.CAP_LEVEL).ifPresent(cap -> {cap.tick();});
		}
	}
	
}