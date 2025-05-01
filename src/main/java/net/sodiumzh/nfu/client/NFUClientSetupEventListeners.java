package net.sodiumzh.nfu.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryGenerateValuesEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID_LEGACY, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFUClientSetupEventListeners {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        NFURegistry.CLIENT_SETUP_DONE.trySet(true);
        List<NFURegistry<?>> shouldGenerate = NFURegistry.allRegistries().values().stream()
                .filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 2)
                .toList();
        shouldGenerate = NFURegistry.sortByLoadingOrder(shouldGenerate);
        shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NFURegistryGenerateValuesEvent.Client(reg)));
        shouldGenerate.forEach(NFURegistry::generateAllValues);
    }
}
