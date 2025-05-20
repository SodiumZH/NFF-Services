package net.sodiumzh.nfu.util;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utilities for vanilla or Forge registration-related things
 */
public class NFURegistrationStatics {

    public static <T> Optional<Supplier<? extends T>> getDeferredRegisterSupplier(DeferredRegister<T> register, String key) {
        Map<RegistryObject<T>, Supplier<? extends T>> entries = NFUReflectionStatics.forceGet(
            register, DeferredRegister.class, "entries").cast();
        if (entries == null) return Optional.empty();
        RegistryObject<T> entryKey = null;
        for (RegistryObject<T> entry: register.getEntries()) {
            if (entry.getId().getPath().equals(key)) {
                entryKey = entry;
                break;
            }
        }
        if (entryKey == null) return Optional.empty();
        return Optional.ofNullable(entries.get(entryKey));
    }

}
