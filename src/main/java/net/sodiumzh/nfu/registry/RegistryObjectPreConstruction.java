package net.sodiumzh.nfu.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.annotation.DontCallManually;
import net.sodiumzh.nfu.container.Tuple2;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinDeferredRegister;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinGameData;
import net.sodiumzh.nfu.object.LimitedMutable;
import net.sodiumzh.nfu.util.NFUReflectionStatics;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * {@code RegistryObjectPreConstruction} allows to construct some specific Forge registry objects
 * before registering. This is mainly for cases when a registry entry is referred by another registry
 * which needs to be registered earlier, causing missing-registry-entry error.
 * <p>If configured to be pre-constructed, the registry entry will be constructed in advance of any other.
 * <p>All pre-construction info should be configured in {@link RegistryObjectPreConstruction.SetupEvent} listeners.
 * <p>Implemented in {@link NFUMixinDeferredRegister}.
 */
public class RegistryObjectPreConstruction {

    private static final Set<DeferredRegister<?>> PRE_CONSTRUCT_REGISTERS = new HashSet<>();
    private static final Multimap<ResourceKey<?>, ResourceLocation> PRE_CONSTRUCT_ENTRIES = HashMultimap.create();
    private static final Multimap<ResourceKey<?>, String> PRE_CONSTRUCT_MODS = HashMultimap.create();

    /*
     * Set all entries of a {@link DeferredRegister} should be pre-constructed.
     */
    private static void preConstruct(DeferredRegister<?> reg) {
        PRE_CONSTRUCT_REGISTERS.add(reg);
    }

    /**
     * Set an entry of a given registry and key should be pre-constructed.
     */
    private static void preConstruct(IForgeRegistry<?> registry, ResourceLocation key) {
        PRE_CONSTRUCT_ENTRIES.put(registry.getRegistryKey(), key);
    }

    /**
     * Set an entry of a given registry and key should be pre-constructed.
     */
    private static void preConstruct(IForgeRegistry<?> registry, String modid, String key) {
        preConstruct(registry, new ResourceLocation(modid, key));
    }

    /**
     * Set all entries of a given registry and mod ID should be pre-constructed.
     */
    private static void preConstructMod(IForgeRegistry<?> registry, String modid) {
        PRE_CONSTRUCT_MODS.put(registry.getRegistryKey(), modid);
    }

    /**
     * Posted on the first time {@link DeferredRegister#register(String, Supplier)} is called,
     * collecting pre-construction info. All setup actions should be handled in this event to
     * ensure it's handled before any registering actions.
     */
    public static class SetupEvent extends Event implements IModBusEvent {

        public SetupEvent() {
        }

        /**
         * Set all entries of a {@link DeferredRegister} to be pre-constructed.
         */
        public void preConstruct(DeferredRegister<?> reg) {
            RegistryObjectPreConstruction.preConstruct(reg);
        }

        /**
         * Set an entry of a given registry and key to be pre-constructed.
         */
        public void preConstruct(IForgeRegistry<?> registry, ResourceLocation key) {
            RegistryObjectPreConstruction.preConstruct(registry, key);
        }

        /**
         * Set an entry of a given registry and key to be pre-constructed.
         */
        public void preConstruct(IForgeRegistry<?> registry, String modid, String key) {
            RegistryObjectPreConstruction.preConstruct(registry, modid, key);
        }

        /**
         * Set all entries of a given registry and mod ID to be pre-constructed.
         */
        public void preConstructMod(IForgeRegistry<?> registry, String modid) {
            RegistryObjectPreConstruction.preConstructMod(registry, modid);
        }
    }

    /**
     * Only for implementation details, don't call anywhere else
     */
    public static class Impl {

        private static final Map<DeferredRegister<?>, DeferredRegisterProperties> ALL_DEFERRED_REGISTERS = new HashMap<>();
        private static final Field FLD_REGISTRY_OBJECT_VALUE =
            NFUReflectionStatics.findFieldIfDeclared(RegistryObject.class, "value").orElseThrow();

        /**
         * Record self on deferred register create. Only called in {@link NFUMixinDeferredRegister}.
         */
        @DontCallManually
        public static void addDeferredRegister(DeferredRegister<?> reg,
                                               @Nullable ResourceKey<? extends Registry<?>> registryKey,
                                               String modid,
                                               Map<RegistryObject<?>, Supplier<?>> entriesRef) {
            ALL_DEFERRED_REGISTERS.put(reg, new DeferredRegisterProperties(registryKey, modid, entriesRef));
        }

        private static void preConstructAll(DeferredRegister<?> reg, DeferredRegisterProperties properties) {
            properties.entriesRef.entrySet().stream()
                .map(Tuple2::of)    // Fully detach from the original map to prevent any concurrent operation issue
                .forEach(entry -> {
                try {
                    Object obj = entry.getB().get();
                    FLD_REGISTRY_OBJECT_VALUE.set(entry.getA(), obj);
                    properties.entriesRef.put(entry.getA(), () -> obj);
                } catch (IllegalAccessException e) {
                    throw new ReflectionFailedException(e);
                }
            });
        }

        private static void preConstruct(DeferredRegister<?> reg, DeferredRegisterProperties properties, Collection<ResourceLocation> keys) {
            properties.entriesRef.entrySet().stream()
                .filter(entry -> keys.contains(entry.getKey().getId()))
                .map(Tuple2::of).forEach(entry -> {
                try {
                    Object obj = entry.getB().get();
                    FLD_REGISTRY_OBJECT_VALUE.set(entry.getA(), obj);
                    properties.entriesRef.put(entry.getA(), () -> obj);
                } catch (IllegalAccessException e) {
                    throw new ReflectionFailedException(e);
                }
            });
        }

        /**
         * Pre-construct entries. Only called in {@link NFUMixinGameData}.
         */
        @DontCallManually
        public static void doPreConstruction() {
            ALL_DEFERRED_REGISTERS.forEach((reg, prop) -> {
                if (PRE_CONSTRUCT_REGISTERS.contains(reg)) {
                    preConstructAll(reg, prop);
                } else if (prop.registryKey == null) {
                    return;
                } else if (PRE_CONSTRUCT_MODS.containsEntry(prop.registryKey, prop.modid)){
                    preConstructAll(reg, prop);
                } else {
                    preConstruct(reg, prop, PRE_CONSTRUCT_ENTRIES.get(prop.registryKey));
                }
            });
        }

        private static record DeferredRegisterProperties(
            @Nullable ResourceKey<? extends Registry<?>> registryKey,
            String modid,
            Map<RegistryObject<?>, Supplier<?>> entriesRef
        ){}
    }

}
