package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.util.NFUDebugStatics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * NOT IMPLEMENTED
 */
//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NFULibrary.MOD_ID)
class EntityDefaultInstances {
    private static Map<EntityType<?>, Entity> TABLE = new HashMap<>();

    /**
     * Get an entity sample of an entity type. Note that the "level" field of the
     * entity sample is {@code null}, and never put it into any level!
     */
    private static Optional<Entity> get(EntityType<?> type) {
        return Optional.ofNullable(TABLE.get(type));
    }

    @SubscribeEvent
    private static void createDefaultInstances(TickEvent.WorldTickEvent event) {
        if (!TABLE.isEmpty()) return;
        ForgeRegistries.ENTITIES.forEach(type -> {
            try {
                TABLE.put(type, type.create(event.world));
            } catch (Exception e) {
                NFUDebugStatics.errorOnce("Failed to create default instance: " + type.getDescriptionId());
            }
        });
    }

}
