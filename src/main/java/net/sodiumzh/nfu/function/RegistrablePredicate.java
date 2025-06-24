package net.sodiumzh.nfu.function;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nfu.container.Tuple2;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.util.NFUInfoStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * A {@link Predicate} with some meta info for NFU Predicate registry.
 */
public class RegistrablePredicate<T> implements Predicate<T> {

    @Nonnull
    private final Predicate<T> predicate;
    @Nonnull
    private final String name;
    @Nullable
    private String translationKey = null;
    @Nullable
    private Object[] translationArgs = null;

    public RegistrablePredicate(@Nonnull String name, @Nonnull Predicate<T> predicate) {
        this.predicate = predicate;
        this.name = name;
    }

    public RegistrablePredicate<T> setTranslation(String key, Object... args) {
        this.translationKey = key;
        this.translationArgs = args;
        return this;
    }

    public boolean test(T t) {
        return predicate.test(t);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public MutableComponent getTranslation() {
        if (translationKey != null)
            return NFUInfoStatics.createTranslatable(translationKey, translationArgs == null ? new Object[]{} : translationArgs);
        else return null;
    }

    @Nullable
    public ResourceLocation getRegistryKey() {
        return NFURegistries.PREDICATES.getKey(this);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("RegistrablePredicate {name = '").append(name) .append("'");
        ResourceLocation regKey = this.getRegistryKey();
        if (regKey != null)
            s.append(", registryKey = '").append(regKey).append("'");
        if (translationKey != null)
            s.append(", translationKey = '").append(translationKey).append("'");
        return s.append("}").toString();
    }

    public static <T> RegistrablePredicate<T> and(String name, Predicate<? super T> a, Predicate<? super T> b) {
        return new RegistrablePredicate<>(name, (T t) -> a.test(t) && b.test(t));
    }

    public static <T> RegistrablePredicate<T> or(String name, Predicate<? super T> a, Predicate<? super T> b) {
        return new RegistrablePredicate<>(name, (T t) -> a.test(t) || b.test(t));
    }

    public static <T> RegistrablePredicate<T> not(String name, Predicate<? super T> a) {
        return new RegistrablePredicate<>(name, (T t) -> !a.test(t));
    }

    public static <T> RegistrablePredicate<T> xor(String name, Predicate<? super T> a, Predicate<? super T> b) {
        return new RegistrablePredicate<>(name, (T t) -> a.test(t) != b.test(t));
    }

    /**
     * Cast self to predicate of a context-referred subclass.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> RegistrablePredicate<U> cast() {
        return (RegistrablePredicate<U>)this;
    }

}
