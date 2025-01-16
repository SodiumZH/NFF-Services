package net.sodiumzh.nautils.entity.taming;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandlerProvider;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nautils.registries.NaUtilsCaps;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NaUtils.MOD_ID)
public class TamingProcessEventListeners {

    public static final String ANGER_HANDLER_KEY = "vanillaTamableAnimalAngerHandler";
    public static final String TAMING_PROCESS_HANDLER_KEY = "vanillaAnimalTamingProcessHandler";

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof TamableAnimal tamable && event.getObject() instanceof IUsesTamingProcess utp)
        {
            event.addCapability(new ResourceLocation(NaUtils.MOD_ID, TAMING_PROCESS_HANDLER_KEY),
                    new CVanillaAnimalTamingProcessHandler.Prvd(utp, NaUtilsCaps.CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY));
            event.addCapability(new ResourceLocation(NaUtils.MOD_ID, ANGER_HANDLER_KEY),
                    new CMobAngerHandlerProvider(utp.asMob(), NaUtilsCaps.CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER, utp.getTamingAngerRules()));
        }
    }


}
