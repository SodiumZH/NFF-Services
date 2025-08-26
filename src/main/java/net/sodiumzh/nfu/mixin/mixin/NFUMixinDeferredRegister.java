package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.object.LimitedMutable;
import net.sodiumzh.nfu.registry.RegistryObjectPreConstruction;
import net.sodiumzh.nfu.util.NFUReflectionStatics;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(DeferredRegister.class)
public class NFUMixinDeferredRegister implements NFUMixin<DeferredRegister<?>> {

    @Final
    @Shadow(remap = false)
    private ResourceKey<? extends Registry<?>> registryKey;

    @Final
    @Shadow(remap = false)
    private String modid;

    @Final
    @Shadow(remap = false)
    private Map<RegistryObject<?>, Supplier<?>> entries;

    private static final Field REGISTRY_OBJECT_VALUE =
        NFUReflectionStatics.findFieldIfDeclared(RegistryObject.class, "value").orElseThrow();

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Class;Ljava/lang/String;Z)V", remap = false,
    at = @At("TAIL"))
    private void recordSelf(ResourceKey<?> registryKey, Class<?> base, String modid, boolean optionalRegistry, CallbackInfo ci) {
        RegistryObjectPreConstruction.Impl.addDeferredRegister(caller(),
            (ResourceKey<? extends Registry<?>>)registryKey, modid, entries);
    }




}
