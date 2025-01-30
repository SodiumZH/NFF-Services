package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.capability.NaUtilsEntitySerializableCapProvider;
import net.sodiumzh.nautils.capability.CEntityDataCapability;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandlerProvider;
import net.sodiumzh.nautils.entity.anger.IUsesDefaultAngerHandler;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaUtilsCapAttachment {

    public static final ResourceLocation KEY_DATA_CAPABILITY
            = new ResourceLocation(NaUtils.MOD_ID, "data_capability");
    public static final ResourceLocation KEY_DEFAULT_ANGER_HANDLER
            = new ResourceLocation(NaUtils.MOD_ID, "default_anger_handler");

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(KEY_DATA_CAPABILITY, new NaUtilsEntitySerializableCapProvider<>(
                event.getObject(), NaUtilsCaps.CAP_ENTITY_DATA, CEntityDataCapability.Impl::new));

        if (event.getObject() instanceof Mob mob && event.getObject() instanceof IUsesDefaultAngerHandler uses) {
            event.addCapability(KEY_DEFAULT_ANGER_HANDLER,
                    new CMobAngerHandlerProvider(mob, NaUtilsCaps.CAP_MOB_DEFAULT_ANGER_HANDLER, uses.getAngerRules()));
        }


    }
}
