package net.sodiumzh.nfu.eventlistener;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.entity.DeferredEntityAttributeRegisterEvent;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryGenerateValuesEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFUSetupEventHandlers {

    /**
     * Generate registry values if needed.
     * @see NFURegistry
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void generateRegistries(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            NFURegistry.COMMON_SETUP_DONE.trySet(true);
            List<NFURegistry<?>> shouldGenerate = NFURegistry.allRegistries().values().stream()
                    .filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 0)
                    .toList();
            shouldGenerate = NFURegistry.sortByLoadingOrder(shouldGenerate);
            shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NFURegistryGenerateValuesEvent.CommonBefore(reg)));
            shouldGenerate.forEach(NFURegistry::generateAllValues);
            shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NFURegistryGenerateValuesEvent.CommonAfter(reg)));
        });
    }

    @SubscribeEvent
    public static void registerDeferredAttributeSuppliers(EntityAttributeCreationEvent event)
    {
        ModLoader.get().postEvent(new DeferredEntityAttributeRegisterEvent());
    }

}
