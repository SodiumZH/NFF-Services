package net.sodiumzh.nautils.statics;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nautils.compat.ModDependent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Static methods for inter-mod compatibility related stuff.
 */
public class NaUtilsCompatStatics {

    public static <T> Optional<RegistryObject<T>> registerModDependent(DeferredRegister<T> register,
                    String path, String dependingModId, Supplier<T> entry)
    {
        if (ModList.get().isLoaded(dependingModId))
        {
            return Optional.of(register.register(path, entry));
        }
        else return Optional.empty();
    }
}
