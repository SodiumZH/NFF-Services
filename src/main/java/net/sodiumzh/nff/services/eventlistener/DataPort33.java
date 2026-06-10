package net.sodiumzh.nff.services.eventlistener;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.*;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.registry.NFFDataSerializers;
import net.sodiumzh.nff.services.registry.NFFEntityComponents;
import net.sodiumzh.nfu.entity.component.EntityComponentAPI;
import net.sodiumzh.nfu.mixin.event.entity.EntityFinalizeLoadingEvent;
import net.sodiumzh.nfu.mixin.event.entity.EntityLoadEvent;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import net.sodiumzh.nfu.network.NFUDataSerializers;

import java.util.UUID;

/**
 * Event listeners for porting 0.x.32 data to 0.x.33 which involves a large code clear-up.
 * Will be removed in 0.x.34, and 0.x.34 data will not be compatible with 0.x.32 or lower.
 */
@Deprecated(forRemoval = true, since = "0.x.34")
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataPort33 {

    @SubscribeEvent
    public static void onEntityLoad(EntityFinalizeLoadingEvent event) {
        if (INFFTamed.get(event.getEntity()).isPresent()) {
            // Port common data
            INFFTamed tamed = INFFTamed.get(event.getEntity()).get();
            CompoundTag caps = event.getNBT().getCompound("ForgeCaps");
            CompoundTag legacyCommonData = caps.getCompound("nffservices:nff_mob_common_data");
            if (!legacyCommonData.isEmpty()) {
                CNFFTamedCommonData.Values temp = new CNFFTamedCommonData.Values(tamed);
                temp.deserializeNBT(legacyCommonData);
                NFFTamedSyncherComponent syncher = EntityComponentAPI.getComponentByPath(event.getEntity(), "/nff/tamed/syncher", NFFEntityComponents.TAMED_SYNCHER.get()).orElseThrow();
                NFFTamedDataComponent data = EntityComponentAPI.getComponentByPath(event.getEntity(), "/nff/tamed/data", NFFEntityComponents.TAMED_DATA.get()).orElseThrow();
                NFFTamedDataAccessor accessor = tamed.getDataAccessor();
                // Port nbt
                temp.getAdditionalNBT().getAllKeys().forEach(k -> data.getNBT().put(k, temp.getAdditionalNBT().get(k).copy()));
                // Port synched values
                syncher.setSynchedData("identifier", UUID.class, temp.getIdentifier());
                accessor.setEncounteredDate(temp.getEncounteredDate());
                accessor.setOwnerUUID(temp.getOwnerUUID());
                accessor.setOwnerName(temp.getOwnerName());
                accessor.setAIState(temp.getAIState());
                // Port other values
                data.putPermanentVariable("initialType", ForgeRegistries.ENTITY_TYPES.getKey(temp.getInitialEntityType()), NFUDataSerializers.RESOURCE_LOCATION);
                data.putPermanentVariable("randomStrollAnchor", temp.getAnchor(), NFUDataSerializers.VEC3);
                data.putPermanentVariable("inventory", temp.getAdditionalInventory(), NFFDataSerializers.TAMED_MOB_INVENTORY.get());
            }
        }
    }

}
