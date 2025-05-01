package net.sodiumzh.nfu.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Static methods for inter-mod compatibility related stuff.
 */
public class NFUCompatStatics {

    public static <T> Optional<RegistryObject<T>> registerModDependent(DeferredRegister<? super T> register,
        String path, String dependingModId, Supplier<T> entry)
    {
        if (ModList.get().isLoaded(dependingModId))
        {
            return Optional.of(register.register(path, entry));
        }
        else return Optional.empty();
    }

    public static <T1 extends Item, T2 extends Item> Either<RegistryObject<T1>, RegistryObject<T2>>
    registerModDependentOrElse(DeferredRegister<Item> register, String path, String dependingModId,
        Supplier<? extends T1> entrySupplier, Supplier<? extends T2> fallbackSupplier)
    {
        if (ModList.get().isLoaded(dependingModId))
        {
            return Either.left(register.register(path, entrySupplier));
        }
        else return Either.right(register.register(path, fallbackSupplier));
    }
}
