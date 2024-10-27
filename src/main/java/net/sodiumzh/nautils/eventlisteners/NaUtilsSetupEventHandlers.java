package net.sodiumzh.nautils.eventhandler;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NaUtilsSetupEventHandlers {

    /**
     * Generate registry values if needed.
     * @see NaUtilsRegistry
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void generateRegistries(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            for (var registry: NaUtilsRegistry.allRegistries().values())
            {
                if (registry.shouldGenerateOnSetup()
                        && registry.getGenerateOnSetupPhase() == 0)
                    registry.regenerateAllValues();
            }
        });
    }
}
