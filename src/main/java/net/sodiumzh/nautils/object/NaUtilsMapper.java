package net.sodiumzh.nautils.object;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.*;

/**
 * {@code NaUtilsMapper} receives an input, test if it meets a certain condition, and
 * map it to an {@link Optional} instance of the output type.
 * @param <T> Input type.
 * @param <R> Output type.
 */
public class NaUtilsMapper<T, R> implements BiFunction<T, Object[], Optional<R>> {

    public static <T, R> NaUtilsMapper<T, R> instanceOfMapper(Class<T> inType, Class<R> outType) {
        return NaUtilsMapper.noVararg(inType, outType, t -> outType.isAssignableFrom(t.getClass()), t -> (R)t);
    }

    private final Class<T> in;
    private final Class<R> out;
    private final int varargSize;
    private final BiPredicate<T, Object[]> condition;
    private final BiFunction<T, Object[], R> mapper;

    public NaUtilsMapper(Class<T> in, Class<R> out, int varargSize, BiPredicate<T, Object[]> condition,
                         BiFunction<T, Object[], R> mapper) {
        this.in = in;
        this.out = out;
        this.varargSize = varargSize;
        this.condition = condition;
        this.mapper = mapper;
    }

    public static <T, R> NaUtilsMapper<T, R> unconditional
            (Class<T> in, Class<R> out, int varargSize, BiFunction<T, Object[], R> mapper) {
        return new NaUtilsMapper<>(in, out, varargSize,
                (t, vararg) -> mapper.apply(t, vararg) != null, mapper);
    }

    public static <T, R> NaUtilsMapper<T, R> noVararg
            (Class<T> in, Class<R> out, Predicate<T> condition, Function<T, R> mapper) {
        return new NaUtilsMapper<>(in, out, 0, (t, varargs) -> condition.test(t),
                (t, varargs) -> mapper.apply(t));
    }
    public static <T, R> NaUtilsMapper<T, R> unconditionalNoVararg
            (Class<T> in, Class<R> out, Function<T, R> mapper) {
        return unconditional(in, out, 0, (t, varargs) -> mapper.apply(t));
    }

    @Override
    public Optional<R> apply(T t, Object... varargs) {
        Object[] resizedVarargs = new Object[this.varargSize];
        Arrays.fill(resizedVarargs, null);
        for (int i = 0; i < resizedVarargs.length; ++i) {
            if (i < varargs.length) resizedVarargs[i] = varargs[i];
        }
        return condition.test(t, resizedVarargs) ? Optional.ofNullable(mapper.apply(t, resizedVarargs)) : Optional.empty();
    }

}
