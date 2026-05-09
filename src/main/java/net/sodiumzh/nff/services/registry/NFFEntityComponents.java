package net.sodiumzh.nff.services.registry;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamableComponent;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nfu.entity.component.EntityComponentInitEvent;
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


    @SubscribeEvent
    public static void attach(EntityComponentInitEvent event) {
        event.getComponentManager().setRequired("/nff", EntityComponentTypes.NODE.get());
        if (INFFTamed.get(event.getEntity()).isPresent()) {
            event.getComponentManager().addSubComponentByPath("/nff/tamed", EntityComponentTypes.NODE.get().create(event.getEntity()));
            event.getComponentManager().addSubComponentByPath("/nff/tamed/synched_data", EntityComponentTypes.SYNCHER.get().create(event.getEntity()));
            event.getComponentManager().addSubComponentByPath("/nff/tamed/data", EntityComponentTypes.DYNAMIC_DATA.get().create(event.getEntity()));
        }
        else if (event.getEntity() instanceof Mob mob && NFFTamingMapping.getAllTamableTypes().contains(event.getEntity().getType())) {
            event.getComponentManager().addSubComponentByPath("/nff/tamable", TAMABLE.get().create(mob));
        }
    }


}
