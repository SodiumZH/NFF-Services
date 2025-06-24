package net.sodiumzh.nfu.eventhandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.entity.ConditionalAttributeModifier;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryGenerateValuesEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFUServerEventHandlers {

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.START)
			ConditionalAttributeModifier.update();
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event)
	{
		NFURegistry.SERVER_SETUP_DONE.trySet(true);
		List<NFURegistry<?>> shouldGenerate = NFURegistry.allRegistries().values().stream()
				.filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 1)
				.toList();
		shouldGenerate = NFURegistry.sortByLoadingOrder(shouldGenerate);
		shouldGenerate.forEach(reg -> MinecraftForge.EVENT_BUS.post(new NFURegistryGenerateValuesEvent.ServerBefore(reg)));
		shouldGenerate.forEach(NFURegistry::generateAllValues);
		shouldGenerate.forEach(reg -> MinecraftForge.EVENT_BUS.post(new NFURegistryGenerateValuesEvent.ServerAfter(reg)));
	}
}
