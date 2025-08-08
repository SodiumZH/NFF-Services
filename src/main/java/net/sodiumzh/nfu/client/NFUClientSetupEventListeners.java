package net.sodiumzh.nfu.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.client.renderer.EmptyEntityRenderer;
import net.sodiumzh.nfu.registry.NFUEntityTypes;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryGenerateValuesEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFUClientSetupEventListeners {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        NFURegistry.CLIENT_SETUP_DONE.trySet(true);
        List<NFURegistry<?>> shouldGenerate = NFURegistry.allRegistries().values().stream()
                .filter(reg -> reg.shouldGenerateOnSetup() && reg.getGenerateOnSetupPhase() == 2)
                .toList();
        shouldGenerate = NFURegistry.sortByLoadingOrder(shouldGenerate);
        shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NFURegistryGenerateValuesEvent.ClientBefore(reg)));
        shouldGenerate.forEach(NFURegistry::generateAllValues);
        shouldGenerate.forEach(reg -> ModLoader.get().postEvent(new NFURegistryGenerateValuesEvent.ClientAfter(reg)));
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NFUEntityTypes.ATTACHED_ITEM_DISPLAYER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NFUEntityTypes.DEFAULT_ITEM_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NFUEntityTypes.DEFAULT_EFFECT_ZONE.get(), EmptyEntityRenderer::new);

    }
}
