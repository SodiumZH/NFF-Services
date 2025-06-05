package net.sodiumzh.nfu.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.capability.CEntityDataCapability;
import net.sodiumzh.nfu.capability.NFUEntitySerializableCapProvider;
import net.sodiumzh.nfu.entity.anger.CMobAngerHandlerProvider;
import net.sodiumzh.nfu.entity.anger.IUsesDefaultAngerHandler;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFUCapabilityAttachment {

    public static final ResourceLocation KEY_DATA_CAPABILITY
            = new ResourceLocation(NFULibrary.MOD_ID, "data_capability");
    public static final ResourceLocation KEY_DEFAULT_ANGER_HANDLER
            = new ResourceLocation(NFULibrary.MOD_ID, "default_anger_handler");

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(KEY_DATA_CAPABILITY, new NFUEntitySerializableCapProvider<>(
                event.getObject(), NFUCaps.CAP_ENTITY_DATA, CEntityDataCapability.Impl::new));

        if (event.getObject() instanceof Mob mob && event.getObject() instanceof IUsesDefaultAngerHandler uses) {
            event.addCapability(KEY_DEFAULT_ANGER_HANDLER,
                    new CMobAngerHandlerProvider(mob, NFUCaps.CAP_MOB_DEFAULT_ANGER_HANDLER, uses.getAngerRules()));
        }


    }
}
