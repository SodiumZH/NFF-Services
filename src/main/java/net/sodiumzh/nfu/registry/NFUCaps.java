package net.sodiumzh.nfu.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.capability.CEntityDataCapability;
import net.sodiumzh.nfu.capability.CEntityTickingCapability;
import net.sodiumzh.nfu.capability.CEntityTimerCapability;
import net.sodiumzh.nfu.entity.anger.CMobAngerHandler;
import net.sodiumzh.nfu.entity.taming.CVanillaAnimalTamingProcessHandler;
import net.sodiumzh.nfu.entity.taming.VanillaAnimalTamingProcess;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = NFULibrary.MOD_ID)
public class NFUCaps {

    /**
     * A default anger handler.
     */
    public static final Capability<CMobAngerHandler> CAP_MOB_DEFAULT_ANGER_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    /**
     * Anger handler only for tamable animals using {@link VanillaAnimalTamingProcess}.
     */
    public static final Capability<CMobAngerHandler> CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    /**
     * For tamable animals using taming process.
     */
    public static final Capability<CVanillaAnimalTamingProcessHandler> CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY
            = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<CEntityDataCapability> CAP_ENTITY_DATA
            = CapabilityManager.get(new CapabilityToken<>() {});

    static {
        CMobAngerHandler.register(CAP_MOB_DEFAULT_ANGER_HANDLER);
        CMobAngerHandler.register(CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER);
        CEntityTickingCapability.registerTicking(CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY);
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(CMobAngerHandler.class);
        event.register(CEntityDataCapability.class);
        event.register(CEntityTickingCapability.class);
        event.register(CEntityTimerCapability.class);
    }

}
