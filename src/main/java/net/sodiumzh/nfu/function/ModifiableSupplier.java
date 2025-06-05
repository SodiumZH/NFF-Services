package net.sodiumzh.nfu.function;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Representing an object provider that initialized by a {@link Supplier} and modified stepwise with {@link UnaryOperator}s.
 */
public class ModifiableSupplier<T> implements Supplier<T> {

    private final Supplier<T> init;
    private final List<UnaryOperator<T>> modifications = new ArrayList<>();

    public ModifiableSupplier(Supplier<T> init) {
        this.init = init;
    }

    /**
     * Add a modification that applies to the current object and generates a new reference.
     * The object instance can change in the action, but not necessarily. After running, the
     * object reference will become the action's return value.
     */
    public ModifiableSupplier<T> modify(UnaryOperator<T> action) {
        modifications.add(action);
        return this;
    }

    /**
     * Add a modification that only do something to the object but not changing the object instance.
     */
    public ModifiableSupplier<T> modify(Consumer<T> action) {
        modifications.add(t -> {
            action.accept(t);
            return t;
        });
        return this;
    }

    @Override
    public T get() {
        AtomicReference<T> res = new AtomicReference<>(init.get());
        modifications.forEach(m -> {
            T old = res.get();
            res.set(m.apply(old));
        });
        return res.get();
    }
}
