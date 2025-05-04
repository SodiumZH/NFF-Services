package net.sodiumzh.nfu.capability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CapabilityObject {
    private final Entity entity;
    private final BlockEntity blockEntity;
    private final Level level;

    public CapabilityObject(@Nonnull Entity obj) {
        Objects.requireNonNull(obj);
        this.entity = obj;
        this.blockEntity = null;
        this.level = null;
    }

    public CapabilityObject(@Nonnull BlockEntity obj) {
        Objects.requireNonNull(obj);
        this.entity = null;
        this.blockEntity = obj;
        this.level = null;
    }

    public CapabilityObject(@Nonnull Level obj) {
        Objects.requireNonNull(obj);
        this.entity = null;
        this.blockEntity = null;
        this.level = obj;
    }

    public static CapabilityObject of(Object obj) {
        if (obj instanceof Entity e)
            return new CapabilityObject(e);
        else if (obj instanceof BlockEntity e)
            return new CapabilityObject(e);
        else if (obj instanceof Level l)
            return new CapabilityObject(l);
        else throw new IllegalArgumentException("Illegal capability object: only Entity, BlockEntity and Level are supported. Input: " + obj.getClass().getName());
    }

    @Nonnull
    public Object getObject() {
        return entity != null ? entity : (blockEntity != null ? blockEntity : level);
    }

    public boolean isEntity() {return entity != null;}
    public boolean isBlockEntity(){return blockEntity != null;}
    public boolean isLevel() {return level != null;}
    public boolean is(Class<?> clazz) {
        return clazz.isAssignableFrom(getObject().getClass());
    }

    @Nullable
    public Entity asEntity() {return entity;}
    @Nullable
    public BlockEntity asBlockEntity(){return blockEntity;}
    @Nullable
    public Level asLevel() {return level;}
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> clazz) {
        return this.is(clazz) ? (T) getObject() : null;
    }

    public Optional<Entity> asOptionalEntity() {return Optional.ofNullable(entity);}
    public Optional<BlockEntity> asOptionalBlockEntity(){return Optional.ofNullable(blockEntity);}
    public Optional<Level> asOptionalLevel() {return Optional.ofNullable(level);}
    @SuppressWarnings("unchecked")
    public <T> Optional<T> asOptional(Class<T> clazz) { return Optional.ofNullable(this.as(clazz));}

    public void ifEntity(Consumer<? super Entity> action) {
        this.asOptionalEntity().ifPresent(action);
    }
    public void ifBlockEntity(Consumer<? super BlockEntity> action) {
        this.asOptionalBlockEntity().ifPresent(action);
    }
    public void ifLevel(Consumer<? super Level> action) {
        this.asOptionalLevel().ifPresent(action);
    }
    public <T> void ifIs(Class<T> clazz, Consumer<? super T> action) {
        this.asOptional(clazz).ifPresent(action);
    }

    public <T> Optional<T> getIfEntity(Function<? super Entity, T> getter) {
        return this.asOptionalEntity().flatMap(e -> Optional.ofNullable(getter.apply(e)));
    }
    public <T> Optional<T> getIfBlockEntity(Function<? super BlockEntity, T> getter) {
        return this.asOptionalBlockEntity().flatMap(e -> Optional.ofNullable(getter.apply(e)));
    }
    public <T> Optional<T> getIfLevel(Function<? super Level, T> getter) {
        return this.asOptionalLevel().flatMap(e -> Optional.ofNullable(getter.apply(e)));
    }
    public <T, R> Optional<R> getIfIs(Class<T> clazz, Function<? super T, R> getter) {
        return Optional.ofNullable(this.as(clazz)).flatMap(t -> Optional.ofNullable(getter.apply(t)));
    }

    public boolean isEntityAnd(Predicate<? super Entity> predicate) {
        return this.asOptionalEntity().map(predicate::test).orElse(false);
    }
    public boolean isBlockEntityAnd(Predicate<? super BlockEntity> predicate) {
        return this.asOptionalBlockEntity().map(predicate::test).orElse(false);
    }
    public boolean isLevelAnd(Predicate<? super Level> predicate) {
        return this.asOptionalLevel().map(predicate::test).orElse(false);
    }
    public <T> boolean isAnd(Class<T> clazz, Predicate<? super T> predicate) {
        return this.asOptional(clazz).map(predicate::test).orElse(false);
    }

    public void ifEntityAnd(Predicate<? super Entity> predicate, Consumer<? super Entity> action) {
        this.asOptionalEntity().ifPresent(e -> {if (predicate.test(e)) action.accept(e);});
    }
    public void ifBlockEntityAnd(Predicate<? super BlockEntity> predicate, Consumer<? super BlockEntity> action) {
        this.asOptionalBlockEntity().ifPresent(e -> {if (predicate.test(e)) action.accept(e);});
    }
    public void ifLevelAnd(Predicate<? super Level> predicate, Consumer<? super Level> action) {
        this.asOptionalLevel().ifPresent(e -> {if (predicate.test(e)) action.accept(e);});
    }
    public <T> void ifIsAnd(Class<T> clazz, Predicate<? super T> predicate, Consumer<? super T> action) {
        this.asOptional(clazz).ifPresent(e -> {if (predicate.test(e)) action.accept(e);});
    }

    public <T> Optional<T> getIfEntityAnd(Predicate<? super Entity> predicate, Function<? super Entity, T> getter) {
        return this.asOptionalEntity().map(e -> predicate.test(e) ? getter.apply(e) : null);
    }
    public <T> Optional<T> getIfBlockEntityAnd(Predicate<? super BlockEntity> predicate, Function<? super BlockEntity, T> getter) {
        return this.asOptionalBlockEntity().map(e -> predicate.test(e) ? getter.apply(e) : null);
    }
    public <T> Optional<T> getIfLevelAnd(Predicate<? super Level> predicate, Function<? super Level, T> getter) {
        return this.asOptionalLevel().map(e -> predicate.test(e) ? getter.apply(e) : null);
    }
    public <T, R> Optional<R> getIfIsAnd(Class<T> clazz, Predicate<? super T> predicate, Function<? super T, R> getter) {
        return this.asOptional(clazz).map(e -> predicate.test(e) ? getter.apply(e) : null);
    }
}
