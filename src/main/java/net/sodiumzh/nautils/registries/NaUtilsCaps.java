package net.sodiumzh.nautils.registries;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.sodiumzh.nautils.capability.CEntityTickingCapability;
import net.sodiumzh.nautils.capability.CEntityDataCapability;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandler;
import net.sodiumzh.nautils.entity.taming.CVanillaAnimalTamingProcessHandler;
import net.sodiumzh.nautils.entity.taming.VanillaAnimalTamingProcess;

public class NaUtilsCaps {

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

}
