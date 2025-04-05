package net.sodiumzh.nautils.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.registries.NaUtilsRegistryGenerateValuesEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NaUtilsClientSetupEventListeners {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        NaUtilsRegistry.CLIENT_SETUP_DONE.trySet(true);
        List<NaUtilsRegistry<?>> shouldGenerate = NaUtilsRegistry.allRegistries().values().stream()
                .filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 2)
                .toList();
        shouldGenerate = NaUtilsRegistry.sortByLoadingOrder(shouldGenerate);
        shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NaUtilsRegistryGenerateValuesEvent.Client(reg)));
        shouldGenerate.forEach(NaUtilsRegistry::generateAllValues);
    }
}
