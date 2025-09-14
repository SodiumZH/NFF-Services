package net.sodiumzh.nfu.container;

import net.minecraft.util.Tuple;
import org.apache.logging.log4j.util.TriConsumer;
import org.checkerframework.checker.units.qual.K;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ITable2D<R, C, V> {

    public Optional<V> get(R row, C column);

    public void put(R row, C column, V value);

    public boolean contains(R row, C column);

    public boolean containsRow(R row);

    public boolean containsColumn(C column);

    public Set<KeyPair<R, C>> getKeyPairs(V value);

    public Map<R, V> getColumn(C column);

    public Map<C, V> getRow(R row);

    public void removeValue(V value);

    public void remove(R row, C column);

    public void removeRow(R row);

    public void removeColumn(C column);

    public Stream<Entry<R, C, V>> entryStream();

    public default void forEach(Consumer<Entry<R, C, V>> action) {
        this.entryStream().forEach(action);
    }

    public default void forEach(TriConsumer<R, C, V> action) {
        this.entryStream().forEach(entry -> action.accept(entry.rowKey(), entry.columnKey(), entry.value()));
    }

    public static <R, C, V> ITable2D<R, C, V> snapshotOf(ITable2D<R, C, V> source) {
        return new Table2D.ImmutableSnapshot<>(source);
    }

    public static record KeyPair<R, C>(R row, C column) {

        public KeyPair<R, C> copy() {
            return new KeyPair<>(row, column);
        }

        public <V> Entry<R, C, V> entryOf(V value) {
            return new Entry<>(this.row(), this.column(), value);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ITable2D.KeyPair<?, ?> otherPair
                && this.row().equals(otherPair.row())
                && this.column().equals(otherPair.column());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.row(), this.column());
        }
    }

    public static record Entry<R, C, V>(R rowKey, C columnKey, V value) {

        public Entry<R, C, V> copy() {
            return new Entry<>(this.rowKey(), this.columnKey(), this.value());
        }

        public KeyPair<R, C> keyPair() {
            return new KeyPair<>(this.rowKey(), this.columnKey());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ITable2D.Entry<?, ?, ?> otherEntry
                && this.rowKey().equals(otherEntry.rowKey())
                && this.columnKey().equals(otherEntry.columnKey())
                && this.value().equals(otherEntry.value());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.rowKey(), this.columnKey(), this.value());
        }
    }
}
