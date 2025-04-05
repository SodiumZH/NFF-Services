package net.sodiumzh.nautils.eventhandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.entity.DeferredEntityAttributeRegisterEvent;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.registries.NaUtilsRegistryGenerateValuesEvent;

import java.util.List;

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
            NaUtilsRegistry.COMMON_SETUP_DONE.trySet(true);
            List<NaUtilsRegistry<?>> shouldGenerate = NaUtilsRegistry.allRegistries().values().stream()
                    .filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 0)
                    .toList();
            shouldGenerate = NaUtilsRegistry.sortByLoadingOrder(shouldGenerate);
            shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NaUtilsRegistryGenerateValuesEvent.Common(reg)));
            shouldGenerate.forEach(NaUtilsRegistry::generateAllValues);
        });
    }

    @SubscribeEvent
    public static void registerDeferredAttributeSuppliers(EntityAttributeCreationEvent event)
    {
        ModLoader.get().postEvent(new DeferredEntityAttributeRegisterEvent());
    }

}
