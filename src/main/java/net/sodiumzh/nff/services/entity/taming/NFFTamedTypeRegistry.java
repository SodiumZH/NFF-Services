package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Mapping from mob {@link EntityType}s to its accessing method to {@link INFFTamed} instances.
 */
public class NFFTamedTypeRegistry {

    /**
     * Accessor for mobs that directly implement INFFTamed interface.
     */
    public static final Function<Mob, INFFTamed> SELF = m -> (INFFTamed) m;

    private static final Map<ResourceLocation, Function<Mob, INFFTamed>> TABLE = new ConcurrentHashMap<>();

    private NFFTamedTypeRegistry(){}

    /**
     * Add a mob type using {@link INFFTamed} module and the accessing method of corresponding {@link INFFTamed} instance.
     * Overwrites if the same entity type already exists.
     * <p>Try not to add invalid type IDs (non-existing or non-mob entity types), but not required as the registry getters 
     * are somewhat tolerate to wrong entries. Non-mob entries are ignored in {@code getAccessor(Entity)} and {@code asTamed(Entity)}, 
     * but not in other methods.
     */
    public static void add(ResourceLocation typeID, Function<Mob, INFFTamed> accessor) {
        TABLE.put(typeID, accessor);
    }

    /**
     * Add a mob type using {@link INFFTamed} module and the accessing method of corresponding {@link INFFTamed} instance.
     * Overwrites if the same entity type already exists.
     * <p>Try not to add invalid type IDs (non-existing or non-mob entity types), but not required as the registry getters 
     * are somewhat tolerate to wrong entries. Non-mob entries are ignored in {@code getAccessor(Entity)} and {@code asTamed(Entity)}, 
     * but not in other methods.
     */
    public static void add(EntityType<? extends Mob> type, Function<Mob, INFFTamed> accessor) {
        add(ForgeRegistries.ENTITIES.getKey(type), accessor);
    }

    /**
     * Add a mob type using {@link INFFTamed} module and the accessing method of corresponding {@link INFFTamed} instance.
     * Skips if the same entity type already exists.
     * <p>Try not to add invalid type IDs (non-existing or non-mob entity types), but not required as the registry getters 
     * are somewhat tolerate to wrong entries. Non-mob entries are ignored in {@code getAccessor(Entity)} and {@code asTamed(Entity)}, 
     * but not in other methods.
     */
    public static void addIfAbsent(ResourceLocation typeID, Function<Mob, INFFTamed> accessor) {
        TABLE.putIfAbsent(typeID, accessor);
    }

    public static void addIfAbsent(EntityType<? extends Mob> type, Function<Mob, INFFTamed> accessor) {
        addIfAbsent(ForgeRegistries.ENTITIES.getKey(type), accessor);
    }
    
    public static boolean contains(ResourceLocation typeID) {
        return TABLE.containsKey(typeID);
    }
    
    public static boolean contains(EntityType<?> type) {
        return contains(ForgeRegistries.ENTITIES.getKey(type));
    }

    public static Optional<Function<Mob, INFFTamed>> getAccessor(ResourceLocation typeID) {
        return Optional.ofNullable(TABLE.get(typeID));
    }

    public static Optional<Function<Mob, INFFTamed>> getAccessor(EntityType<?> type) {
        return Optional.ofNullable(ForgeRegistries.ENTITIES.getKey(type)).map(TABLE::get);
    }

    public static Optional<Function<Mob, INFFTamed>> getAccessor(Entity entity) {
        return entity instanceof Mob ? getAccessor(entity.getType()) : Optional.empty();
    }
    
    public static Optional<INFFTamed> asTamed(Entity e) {
        return e instanceof Mob mob ? getAccessor(e).map(c -> c.apply(mob)) : Optional.empty();
    }


}
