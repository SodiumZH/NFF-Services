package net.sodiumzh.nff.services.eventlistener;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nfu.mixin.event.entity.EntityLoadEvent;

/**
 * Event listeners for porting 0.x.32 data to 0.x.33 which involves a large code clear-up.
 * Will be removed in 0.x.34, and 0.x.34 data will not be compatible with 0.x.32 or lower.
 */
@Deprecated(forRemoval = true, since = "0.x.34")
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataPort33 {

    @SubscribeEvent
    public static void onEntityLoad(EntityLoadEvent event) {
        CompoundTag caps = event.getNBT().getCompound("ForgeCaps");
        CompoundTag legacyCommonData = caps.getCompound("nffservices:nff_mob_common_data");

    }

}
