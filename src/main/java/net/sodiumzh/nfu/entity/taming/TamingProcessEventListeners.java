package net.sodiumzh.nfu.entity.taming;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.capability.NFUEntitySerializableCapProvider;
import net.sodiumzh.nfu.entity.anger.CMobAngerHandlerProvider;
import net.sodiumzh.nfu.registry.NFUCaps;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NFULibrary.MOD_ID)
public class TamingProcessEventListeners {

    public static final String ANGER_HANDLER_KEY = "vanillaTamableAnimalAngerHandler";
    public static final String TAMING_PROCESS_HANDLER_KEY = "vanillaAnimalTamingProcessHandler";

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof TamableAnimal tamable && event.getObject() instanceof IUsesTamingProcess utp)
        {
            event.addCapability(new ResourceLocation(NFULibrary.MOD_ID_LEGACY, TAMING_PROCESS_HANDLER_KEY),
                    new NFUEntitySerializableCapProvider<>(tamable, NFUCaps.CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY,
                            () -> new CVanillaAnimalTamingProcessHandler.Impl(utp)));
            event.addCapability(new ResourceLocation(NFULibrary.MOD_ID_LEGACY, ANGER_HANDLER_KEY),
                    new CMobAngerHandlerProvider(utp.asMob(), NFUCaps.CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER, utp.getTamingAngerRules()));
        }
    }


}
