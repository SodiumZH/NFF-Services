package net.sodiumzh.nff.services.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.*;
import net.sodiumzh.nfu.entity.component.*;
import net.sodiumzh.nfu.entity.component.preset.*;
import net.sodiumzh.nfu.network.AvailableSide;
import net.sodiumzh.nfu.object.HierarchyPath;
import net.sodiumzh.nfu.registry.NFUEntityComponents;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;
import org.antlr.v4.codegen.model.SrcOp;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFEntityComponents {

    public static final NFURegistryEntryCollection<EntityComponentType<?, ?>> COLLECTION = NFURegistryEntryCollection.create(
        NFURegistries.ENTITY_COMPONENT_TYPES, NFFServices.MOD_ID);

    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableComponent>> TAMABLE = COLLECTION.register("tamable", () ->
        new EntityComponentType<>(Mob.class, NFFTamableComponent.class, AvailableSide.SERVER, NFFTamableComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableDataComponent>> TAMABLE_DATA = COLLECTION.register("tamable_data", () ->
        new EntityComponentType<>(Mob.class, NFFTamableDataComponent.class, AvailableSide.SERVER, NFFTamableDataComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, EntityTimerComponent<Mob>>> MOB_TIMER = COLLECTION.register("mob_timer", () ->
        new EntityComponentType<>(Mob.class, EntityTimerComponent.class, EntityTimerComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamableAngerHandlerComponent>> TAMABLE_ANGER_HANDLER = COLLECTION.register("tamable_anger_handler", () ->
        new EntityComponentType<>(Mob.class, NFFTamableAngerHandlerComponent.class, AvailableSide.SERVER, NFFTamableAngerHandlerComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamedDataComponent>> TAMED_DATA = COLLECTION.register("tamed_data", () ->
        new EntityComponentType<>(Mob.class, NFFTamedDataComponent.class, AvailableSide.SERVER, NFFTamedDataComponent::new));
    public static final NFURegistry.Accessor<EntityComponentType<Mob, NFFTamedSyncherComponent>> TAMED_SYNCHER = COLLECTION.register("tamed_syncher", () ->
        new EntityComponentType<>(Mob.class, NFFTamedSyncherComponent.class, NFFTamedSyncherComponent::new));

    public static final HierarchyPath PATH_NFF = HierarchyPath.byLiteral("/nff");
    public static final HierarchyPath PATH_TAMABLE = HierarchyPath.byLiteral("/nff/tamable");
    public static final HierarchyPath PATH_TAMABLE_DATA = HierarchyPath.byLiteral("/nff/tamable/data");
    public static final HierarchyPath PATH_TAMABLE_TIMER = HierarchyPath.byLiteral("/nff/tamable/timer");
    public static final HierarchyPath PATH_TAMABLE_ANGER_HANDLER = HierarchyPath.byLiteral("/nff/tamable/anger_handler");
    public static final HierarchyPath PATH_TAMED = HierarchyPath.byLiteral("/nff/tamed");
    public static final HierarchyPath PATH_TAMED_SYNCHER = HierarchyPath.byLiteral("/nff/tamed/syncher");
    public static final HierarchyPath PATH_TAMED_DATA = HierarchyPath.byLiteral("/nff/tamed/data");
    public static final HierarchyPath PATH_TAMED_HEALING_HANDLER = HierarchyPath.byLiteral("/nff/tamed/healing_handler");
    public static final HierarchyPath PATH_ITEM_STACK_MONITOR = HierarchyPath.byLiteral("/nff/item_stack_monitor");

    public static final SubComponentAccessor<Mob, NFFTamableComponent> ACCESSOR_TAMABLE =
        new SubComponentAccessor<>(PATH_TAMABLE, TAMABLE);
    public static final SubComponentAccessor<Mob, NFFTamableDataComponent> ACCESSOR_TAMABLE_DATA =
        new SubComponentAccessor<>(PATH_TAMABLE_DATA, TAMABLE_DATA);
    public static final SubComponentAccessor<Mob, EntityTimerComponent<Mob>> ACCESSOR_TAMABLE_TIMER=
        new SubComponentAccessor<>(PATH_TAMABLE_TIMER, MOB_TIMER);
    public static final SubComponentAccessor<Mob, NFFTamableAngerHandlerComponent> ACCESSOR_TAMABLE_ANGER_HANDLER=
        new SubComponentAccessor<>(PATH_TAMABLE_ANGER_HANDLER, TAMABLE_ANGER_HANDLER);
    public static final SubComponentAccessor<Mob, NFFTamedSyncherComponent> ACCESSOR_TAMED_SYNCHER =
        new SubComponentAccessor<>(PATH_TAMED_SYNCHER, TAMED_SYNCHER);
    public static final SubComponentAccessor<Mob, NFFTamedDataComponent> ACCESSOR_TAMED_DATA =
        new SubComponentAccessor<>(PATH_TAMED_DATA, TAMED_DATA);
    public static final SubComponentAccessor<LivingEntity, HealingHandlerComponent> ACCESSOR_TAMED_HEALING_HANDLER =
        new SubComponentAccessor<>(PATH_TAMED_HEALING_HANDLER, EntityComponentTypes.HEALING_HANDLER);
    public static final SubComponentAccessor<Entity, EntityItemStackMonitorComponent> ACCESSOR_ITEM_STACK_MONITOR =
        new SubComponentAccessor<>(PATH_ITEM_STACK_MONITOR, EntityComponentTypes.ITEM_STACK_MONITOR);

    @SubscribeEvent
    public static void attach(EntityComponentSetupEvent event) {
        event.addNode(PATH_NFF);
        if (event.getEntity() instanceof Mob mob
            && NFFTamedTypeRegistry.contains(mob.getType())) {
            event.addNode(PATH_TAMED);
            event.addComponent(PATH_TAMED_SYNCHER, TAMED_SYNCHER.get());
            event.addComponent(PATH_TAMED_DATA, TAMED_DATA.get(), AvailableSide.SERVER);
            event.addComponent(PATH_TAMED_HEALING_HANDLER, EntityComponentTypes.HEALING_HANDLER.get());
        }
        else if (event.getEntity() instanceof Mob mob && NFFTamingMapping.getAllTamableTypes().contains(event.getEntity().getType())) {
            event.addComponent(PATH_TAMABLE, TAMABLE.get(), AvailableSide.SERVER);
            event.addComponent(PATH_TAMABLE_DATA, TAMABLE_DATA.get(), AvailableSide.SERVER);
            event.addComponent(PATH_TAMABLE_TIMER, MOB_TIMER.get(), AvailableSide.SERVER);
            event.addComponent(PATH_TAMABLE_ANGER_HANDLER, TAMABLE_ANGER_HANDLER.get(), AvailableSide.SERVER);
        }
        if (event.getEntity() instanceof Mob mob) {
            event.addComponent(PATH_ITEM_STACK_MONITOR, EntityComponentTypes.ITEM_STACK_MONITOR.get());
        }
    }

    public static EntityItemStackMonitorComponent getItemStackMonitor(LivingEntity living) {
        return EntityComponentAPI.getComponentByPathOrFallback(living, PATH_ITEM_STACK_MONITOR, EntityComponentTypes.ITEM_STACK_MONITOR.get());
    }


}
