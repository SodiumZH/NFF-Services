package net.sodiumzh.nfu.compat;


import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The {@code ModDependent} is a safe wrapper for an object valid only when a certain mod is loaded. If the mod is
 * not present, it always returns null.
 * <p>You can safely use classes, methods etc. of the dependent mod in the supplier body. It's safe as the supplier
 * will never be invoked if the mod isn't present.
 */
public class ModDependent<T> implements Supplier<T>
{
    private final String modId;
    private final Supplier<T> supplier;

    public ModDependent(String dependingModId, Supplier<T> accessor)
    {
        this.modId = Objects.requireNonNull(dependingModId);
        this.supplier = Objects.requireNonNull(accessor);
    }

    @Override
    @Nullable
    public T get()
    {
        return ModList.get().isLoaded(this.modId) ? this.supplier.get() : null;
    }

    @Nonnull
    public Optional<T> getOptional()
    {
        return Optional.ofNullable(this.get());
    }

}
