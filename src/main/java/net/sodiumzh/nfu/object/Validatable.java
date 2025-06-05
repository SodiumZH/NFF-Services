package net.sodiumzh.nfu.object;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Representing an object accessible only after validation. For preventing accident access before initialization etc.
 */
public final class Validatable<T> {

    private boolean validated = false;
    private T value;
    private Supplier<? extends RuntimeException> exceptionSupplier;

    public Validatable(T value, Supplier<? extends RuntimeException> exceptionSupplier) {
        this.value = value;
        this.exceptionSupplier = exceptionSupplier;
    }

    public Validatable(T value) {
        this(value, () -> new IllegalStateException("Access before validation"));
    }

    public T get() {
        if (!validated) throw exceptionSupplier.get();
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void modify(Consumer<? super T> modification) {
        modification.accept(value);
    }

    public void validate() {
        this.validated = true;
    }

    public void modifyAndValidate(Consumer<? super T> modification) {
        modification.accept(value);
        this.validated = true;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public Optional<T> getIfValidated() {
        if (!validated) return Optional.empty();
        else return Optional.ofNullable(value);
    }

}
