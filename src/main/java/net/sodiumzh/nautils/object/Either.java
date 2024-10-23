package net.sodiumzh.nautils.object;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

public class Either<A, B> {
    private A a;
    private B b;

    private Either(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Either<A, B> ofA(A a)
    {
        return new Either<>(a, null);
    }

    public static <A, B> Either<A, B> ofB(B b)
    {
        return new Either<>(null, b);
    }

    public Optional<A> getA()
    {
        return Optional.ofNullable(a);
    }

    public Optional<B> getB()
    {
        return Optional.ofNullable(b);
    }

    public void setA(@Nonnull A a)
    {
        this.a = a;
        this.b = null;
    }

    public void setB(@Nonnull B b)
    {
        this.a = null;
        this.b = b;
    }

    public Either<A, B> ifA(Consumer<A> action)
    {
        this.getA().ifPresent(action);
        return this;
    }

    public Either<A, B> ifB(Consumer<B> action)
    {
        this.getB().ifPresent(action);
        return this;
    }




}
