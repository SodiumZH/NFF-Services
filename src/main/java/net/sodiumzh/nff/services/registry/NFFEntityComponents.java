package net.sodiumzh.nff.services.registry;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.*;
import net.sodiumzh.nfu.entity.component.EntityComponentSetupEvent;
import net.sodiumzh.nfu.entity.component.EntityComponentType;
import net.sodiumzh.nfu.entity.component.EntityComponentTypes;
import net.sodiumzh.nfu.entity.component.IEntityComponent;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFEntityComponents {

    public static final NFURegistryEntryCollection<EntityComponentType<?, ?>> COLLECTION = NFURegistryEntryCollection.create(
        NFURegistries.ENTITY_COMPONENT_TYPES, NFFServices.MOD_ID);

    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableComponent>> TAMABLE = COLLECTION.register("tamable", () ->
        new EntityComponentType<>(Mob.class, NFFTamableComponent.class, NFFTamableComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamedDataComponent>> TAMED_DATA = COLLECTION.register("tamed_data", () ->
        new EntityComponentType<>(Mob.class, NFFTamedDataComponent.class, NFFTamedDataComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamedSyncherComponent>> TAMED_SYNCHER = COLLECTION.register("tamed_syncher", () ->
        new EntityComponentType<>(Mob.class, NFFTamedSyncherComponent.class, NFFTamedSyncherComponent::new));



    @SubscribeEvent
    public static void attach(EntityComponentSetupEvent event) {
        event.addNode("/nff");
        if (INFFTamed.get(event.getEntity()).isPresent()) {
            event.addNode("/nff/tamed");
            event.addComponent("/nff/tamed/syncher", TAMED_SYNCHER.get());
            event.addComponent("/nff/tamed/data", TAMED_DATA.get());
        }
        else if (event.getEntity() instanceof Mob mob && NFFTamingMapping.getAllTamableTypes().contains(event.getEntity().getType())) {
            event.addNode("/nff/tamable");
            event.addComponent("/nff/tamable/data", EntityComponentTypes.DATA.get());
            event.addComponent("/nff/tamable/timer", EntityComponentTypes.TIMER.get());
        }
    }


}
