package net.sodiumzh.nff.services.registry;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.*;
import net.sodiumzh.nfu.entity.component.*;
import net.sodiumzh.nfu.entity.component.preset.*;
import net.sodiumzh.nfu.registry.NFUEntityComponents;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFEntityComponents {

    public static final NFURegistryEntryCollection<EntityComponentType<?, ?>> COLLECTION = NFURegistryEntryCollection.create(
        NFURegistries.ENTITY_COMPONENT_TYPES, NFFServices.MOD_ID);

    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableComponent>> TAMABLE = COLLECTION.register("tamable", () ->
        new EntityComponentType<>(Mob.class, NFFTamableComponent.class, NFFTamableComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableDataComponent>> TAMABLE_DATA = COLLECTION.register("tamable_data", () ->
        new EntityComponentType<>(Mob.class, NFFTamableDataComponent.class, NFFTamableDataComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, EntityTimerComponent<Mob>>> MOB_TIMER = COLLECTION.register("mob_timer", () ->
        new EntityComponentType<>(Mob.class, EntityTimerComponent.class, EntityTimerComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableAngerHandlerComponent>> TAMABLE_ANGER_HANDLER = COLLECTION.register("tamable_anger_handler", () ->
        new EntityComponentType<>(Mob.class, NFFTamableAngerHandlerComponent.class, NFFTamableAngerHandlerComponent::new));
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
            event.addComponent("/nff/tamed/healing_handler", NFUEntityComponents.HEALING_HANDLER.get());
        }
        else if (event.getEntity() instanceof Mob mob && NFFTamingMapping.getAllTamableTypes().contains(event.getEntity().getType())) {
            event.addComponent("/nff/tamable", TAMABLE.get());
            event.addComponent("/nff/tamable/data", TAMABLE_DATA.get());
            event.addComponent("/nff/tamable/timer", MOB_TIMER.get());
            event.addComponent("/nff/tamable/anger_handler", TAMABLE_ANGER_HANDLER.get());
        }
        if (event.getEntity() instanceof Mob mob) {
            event.addComponent("/nff/attribute_monitor", NFUEntityComponents.ATTRIBUTE_MONITOR.get());
            event.addComponent("/nff/item_stack_monitor", NFUEntityComponents.ITEM_STACK_MONITOR.get());
        }
    }


}
