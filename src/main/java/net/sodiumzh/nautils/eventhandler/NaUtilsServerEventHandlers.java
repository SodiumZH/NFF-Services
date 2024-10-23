package net.sodiumzh.nautils.eventhandler;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.data.ServerSideRegistry;
import net.sodiumzh.nautils.entity.ConditionalAttributeModifier;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaUtilsServerEventHandlers {

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.START)
			ConditionalAttributeModifier.update();
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event)
	{
		for (var registry: NaUtilsRegistry.allRegistries().values())
		{
			if (registry.shouldGenerateOnSetup()
					&& registry.getValueClass().isAnnotationPresent(ServerSideRegistry.class))
				registry.regenerateAllValues();
		}
	}
}
