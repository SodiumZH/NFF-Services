package net.sodiumzh.nfu.container;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A container mapping two keys (row keys, column keys) to a single value.
 * @param <R>Row key type.
 * @param <C>Column key type.
 * @param <V>Value type.
 */
public class Table2D<R, C, V> implements ITable2D<R, C, V> {

    private final Map<R, Map<C, V>> tableFromRow = new HashMap<>();
    private final Map<C, Map<R, V>> tableFromColumn = new HashMap<>();
    private final Multimap<V, ITable2D.KeyPair<R, C>> tableFromValue = HashMultimap.create();
    private final Map<C, V> emptyRow = Map.of();
    private final Map<R, V> emptyColumn = Map.of();

    public Table2D() {
    }

    public Optional<V> get(R row, C column) {
        if (tableFromRow.containsKey(row)
            && tableFromRow.get(row).containsKey(column))
            return Optional.of(tableFromRow.get(row).get(column));
        else return Optional.empty();
    }

    public void put(R row, C column, V value) {
        this.tableFromRow.putIfAbsent(row, new HashMap<>());
        this.tableFromRow.get(row).put(column, value);
        this.tableFromColumn.putIfAbsent(column, new HashMap<>());
        this.tableFromColumn.get(column).put(row, value);
        this.tableFromValue.put(value, new ITable2D.KeyPair<>(row, column));
    }

    public boolean contains(R row, C column) {
        return tableFromRow.containsKey(row)
            && tableFromRow.get(row).containsKey(column);
    }

    public boolean containsRow(R row) {
        return this.tableFromRow.containsKey(row) && !this.tableFromRow.get(row).isEmpty();
    }

    public boolean containsColumn(C column) {
        return this.tableFromColumn.containsKey(column) && !this.tableFromColumn.get(column).isEmpty();
    }

    public Set<ITable2D.KeyPair<R, C>> getKeyPairs(V value) {
        return Set.copyOf(tableFromValue.get(value));
    }

    public Map<R, V> getColumn(C column) {
        return Map.copyOf(tableFromColumn.getOrDefault(column, emptyColumn));
    }

    public Map<C, V> getRow(R row) {
        return Map.copyOf(tableFromRow.getOrDefault(row, emptyRow));
    }

    public void removeValue(V value) {
        this.tableFromValue.get(value).forEach(rc -> {
            tableFromRow.get(rc.row()).remove(rc.column());
            tableFromColumn.get(rc.column()).remove(rc.row());
        });
        this.tableFromValue.removeAll(value);
    }

    public void remove(R row, C column) {
        Optional<V> oldV = this.get(row, column);
        if (oldV.isEmpty()) return;
        this.tableFromRow.get(row).remove(column);
        this.tableFromColumn.get(column).remove(row);
        this.tableFromValue.remove(oldV.get(), new ITable2D.KeyPair<>(row, column));
    }

    public void removeRow(R row) {
        if (!this.containsRow(row)) return;
        this.getRow(row).forEach((key, value) -> {
            this.tableFromColumn.get(key).remove(row);
            this.tableFromValue.remove(value, new ITable2D.KeyPair<>(row, key));
        });
        this.tableFromRow.remove(row);
    }

    public void removeColumn(C column) {
        if (!this.containsColumn(column)) return;
        this.getColumn(column).forEach((key, value) -> {
            this.tableFromRow.get(key).remove(column);
            this.tableFromValue.remove(value, new ITable2D.KeyPair<>(key, column));
        });
        this.tableFromColumn.remove(column);
    }

    public Stream<ITable2D.Entry<R, C, V>> entryStream() {
        return this.tableFromValue.entries().stream()
            .map(entry -> new ITable2D.Entry<>(entry.getValue().row(), entry.getValue().column(), entry.getKey()));
    }

    public boolean isEmpty() {
        return this.tableFromValue.isEmpty();
    }

    public static class ImmutableSnapshot<R, C, V> implements ITable2D<R, C, V> {

        private final List<ITable2D.Entry<R, C, V>> entries;

        public ImmutableSnapshot(ITable2D<R, C, V> source) {
            this.entries = source.entryStream().toList();
        }

        public Optional<V> get(R row, C column) {
            return entries.stream().filter(e -> e.rowKey().equals(row) && e.columnKey().equals(column))
                .findFirst().map(Entry::value);
        }

        public void put(R row, C column, V value) {
            throw new UnsupportedOperationException("Table2D.ImmutableCopy doesn't allow modification.");
        }

        public boolean contains(R row, C column) {
            return entries.stream().anyMatch(e -> e.rowKey().equals(row) && e.columnKey().equals(column));
        }

        public boolean containsRow(R row) {
            return this.entries.stream().anyMatch(e -> e.rowKey().equals(row));
        }

        public boolean containsColumn(C column) {
            return this.entries.stream().anyMatch(e -> e.columnKey().equals(column));
        }

        public Set<ITable2D.KeyPair<R, C>> getKeyPairs(V value) {
            return this.entries.stream().filter(e -> e.value().equals(value)).map(Entry::keyPair)
                .collect(Collectors.toSet());
        }

        public Map<R, V> getColumn(C column) {
            return Map.copyOf(this.entries.stream().filter(entry -> entry.columnKey().equals(column))
                .collect(Collectors.toMap(Entry::rowKey, Entry::value)));
        }

        public Map<C, V> getRow(R row) {
            return Map.copyOf(this.entries.stream().filter(entry -> entry.rowKey().equals(row))
                .collect(Collectors.toMap(Entry::columnKey, Entry::value)));
        }

        public void removeValue(V value) {
            throw new UnsupportedOperationException("Table2D.ImmutableCopy doesn't allow modification.");
        }

        public void remove(R row, C column) {
            throw new UnsupportedOperationException("Table2D.ImmutableCopy doesn't allow modification.");
        }

        public void removeRow(R row) {
            throw new UnsupportedOperationException("Table2D.ImmutableCopy doesn't allow modification.");
        }

        public void removeColumn(C column) {
            throw new UnsupportedOperationException("Table2D.ImmutableCopy doesn't allow modification.");
        }

        public Stream<ITable2D.Entry<R, C, V>> entryStream() {
            return this.entries.stream();
        }

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }
    }
}
