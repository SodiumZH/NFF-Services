package net.sodiumzh.nautils.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.data.ClientSideRegistry;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NaUtilsClientSetupEventListeners {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        for (var registry: NaUtilsRegistry.allRegistries().values())
        {
            if (registry.shouldGenerateOnSetup()
                    && registry.getValueClass().isAnnotationPresent(ClientSideRegistry.class))
                registry.regenerateAllValues();
        }
    }

}
