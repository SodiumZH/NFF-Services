package net.sodiumzh.nff.services.registry;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;
import net.sodiumzh.nfu.network.AvailableSide;
import net.sodiumzh.nfu.registry.NFURegistry;

/**
 * {@link NFURegistry}s for NFF-Services.
 */
public class NFFRegistries {

    public static void init(){}

    public static NFURegistry<NFFTamingProcess> TAMING_PROCESSES = new NFURegistry<NFFTamingProcess>(new ResourceLocation(NFFServices.MOD_ID, "taming_processes"))
        .setSide(AvailableSide.SERVER).setLoadTiming(NFURegistry.LoadTiming.SIDE_SETUP);

}
