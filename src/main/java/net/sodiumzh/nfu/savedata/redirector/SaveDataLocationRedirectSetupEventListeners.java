package net.sodiumzh.nfu.savedata.redirector;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nfu.NFULibrary;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SaveDataLocationRedirectSetupEventListeners {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void markLoadingCompleted(FMLCommonSetupEvent event) {
        event.enqueueWork(SaveDataLocationRedirector::setLoadingCompleted);
    }

}
