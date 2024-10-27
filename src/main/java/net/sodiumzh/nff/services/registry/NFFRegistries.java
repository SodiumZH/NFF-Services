package net.sodiumzh.nff.services.registry;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;

/**
 * {@link NaUtilsRegistry}s for NFF-Services.
 */
public class NFFRegistries {

    public static void init(){}

    public static NaUtilsRegistry<NFFTamingProcess> TAMING_PROCESSES = new NaUtilsRegistry<NFFTamingProcess>(new ResourceLocation(NFFServices.MOD_ID, "taming_processes"))
            .setShouldGenerateOnServerSetup();

}
