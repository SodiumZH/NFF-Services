package net.sodiumzh.nautils.eventlisteners;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.entity.ConditionalAttributeModifier;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.registries.NaUtilsRegistryGenerateValuesEvent;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaUtilsServerEventListeners {

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.START)
			ConditionalAttributeModifier.update();
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event)
	{
		NaUtilsRegistry.SERVER_SETUP_DONE.trySet(true);
		List<NaUtilsRegistry<?>> shouldGenerate = NaUtilsRegistry.allRegistries().values().stream()
				.filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 1)
				.toList();
		shouldGenerate = NaUtilsRegistry.sortByLoadingOrder(shouldGenerate);
		shouldGenerate.forEach(reg -> MinecraftForge.EVENT_BUS.post(new NaUtilsRegistryGenerateValuesEvent.Server(reg)));
		shouldGenerate.forEach(NaUtilsRegistry::generateAllValues);
	}
}
