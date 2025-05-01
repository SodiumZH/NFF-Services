package net.sodiumzh.nfu.entity.vanillatrade;

import com.google.common.collect.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection as a snapshot of certain "merchant level -> listings" mapping.
 */
class UnmodifiableVanillaTradeListingCollection<T extends IVanillaTradeListing>
        implements IVanillaTradeListingCollection<T> {

    private final SetMultimap<Integer, T> table;

    public UnmodifiableVanillaTradeListingCollection(SetMultimap<Integer, T> source) {
        this.table = Multimaps.unmodifiableSetMultimap(source);
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public Set<T> forLevel(int level) {
        return this.table.get(level).stream().filter(IVanillaTradeListing::isValid)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Integer> allLevels() {
        return table.keySet().stream()
                .filter(i -> !table.get(i).stream().filter(IVanillaTradeListing::isValid).collect(Collectors.toSet()).isEmpty())
                .sorted(Comparator.comparingInt(i -> i)).toList();
    }

    @Override
    public SetMultimap<Integer, T> allLevelsAndListings() {
        List<Integer> allLevels = allLevels();
        SetMultimap<Integer, T> res = HashMultimap.create();
        allLevels.forEach(i -> res.putAll(i, table.get(i).stream().filter(IVanillaTradeListing::isValid).collect(Collectors.toSet())));
        return res;
    }
}
