package net.sodiumzh.nautils.registries;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandler;

public class NaUtilsCaps {

    public static final Capability<CMobAngerHandler> CAP_MOB_ANGER_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    static {
        CMobAngerHandler.register(CAP_MOB_ANGER_HANDLER);
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(CMobAngerHandler.class);
    }

}
