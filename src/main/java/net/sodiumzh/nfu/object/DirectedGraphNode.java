package net.sodiumzh.nfu.object;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Indicates the class is a node of some directed graph,
 * i.e. keeping a set of other instances of the same class ("children nodes").
 */
public interface DirectedGraphNode<T extends DirectedGraphNode<T>> {

    @SuppressWarnings("unchecked")
    public default T self() {
        return (T) this;
    }

    public Set<T> children();

    /**
     * Search if there are cycles derived from self, and output one.
     * @return The path from self to the cycle starting node, then the whole cycle. For example, the cycle is
     * (b -> c -> d -> b), and path from self to b is (self -> a -> b), then the output will be
     * [self, a, b, c, d, b]. Null if there isn't a cyclic path.
     */
    @Nullable
    public default List<T> getCycle() {
        T root = self();
        Set<T> directChildren = root.children();
        if (directChildren.contains(root)) return List.of(root, root);
        Set<ArrayList<T>> currentPaths = new HashSet<>();
        directChildren.forEach(child -> currentPaths.add(new ArrayList<>(List.of(root, child))));
        while (!currentPaths.isEmpty()) {
            // Container for the result of next search
            Set<ArrayList<T>> next = new HashSet<>();
            for (ArrayList<T> path: currentPaths) {
                // Child nodes of each path
                Set<T> children = path.get(path.size() - 1).children();
                for (T child: children) {
                    // Cyclic path found
                    if (path.contains(child)) {
                        path.add(child);
                        return path;
                    }
                    // Otherwise put all paths of going forward
                    else {
                        ArrayList<T> nextPath = new ArrayList<>(path);
                        nextPath.add(child);
                        next.add(nextPath);
                    }
                    // If it's a leaf node, the children set is empty, and it will not be present in the next path set
                }
            }
            currentPaths.clear();
            currentPaths.addAll(next);
        }
        return null;
    }

    /**
     * Check if this node is in the upstream of the input node, i.e. there's a path from this node to the test node.
     * <p>Note: this node is NOT regarded as in the upstream.
     */
    public default boolean isUpstreamNodeOf(T test) {
        if (test == self()) return false;
        Set<T> scannedNodes = new HashSet<>();
        scannedNodes.add(self());
        Set<T> next = new HashSet<>(this.children());
        while (!next.isEmpty()) {
            if (next.contains(test)) return true;
            next.removeIf(scannedNodes::contains);  // Break cycle
            scannedNodes.addAll(next);
            Set<T> newNext = new HashSet<>();
            next.forEach(t -> newNext.addAll(t.children()));
            next = newNext;
        }
        return false;
    }

    /**
     * Check if this node is in the downstream of the test node, i.e. there's a path from the test node to this node.
     * <p>Note: this node is NOT regarded as in the upstream.
     */
    public default boolean isDownstreamNodeOf(T test) {
        return test.isUpstreamNodeOf(self());
    }

    /**
     * Check if the input node is in the downstream of this node, i.e. there's a path from this node to the test node.
     * <p>Note: this node is NOT regarded as in the downstream.
     * @Deprecated The method name is confusing. Use {@code isUpstreamNodeOf} instead.
     */
    @Deprecated
    public default boolean isDownstreamNode(T test) {
        return this.isUpstreamNodeOf(test);
    }

    /**
     * Check if the input node is in the upstream of this node, i.e. there's a path from the test node to this node.
     * <p>Note: this node is NOT regarded as in the upstream.
     * @Deprecated The method name is confusing. Use {@code isDownstreamNodeOf} instead.
     */
    @Deprecated
    public default boolean isUpstreamNode(T test) {
        return test.isDownstreamNode(self());
    }

    /**
     * Sort an unordered node collection to ensure that any node will appear before all its downstream nodes,
     * and after all its upstream nodes. For nodes not having upstream/downstream relationships, the order is
     * not specified.
     */
    public static <T extends DirectedGraphNode<T>> List<T> sortByOccurrenceOrder(Collection<T> nodes)
    {
        return nodes.stream().sorted(Comparator.comparing(n -> n, (n1, n2) -> {
            if (n1.isUpstreamNodeOf(n2.self())) return -1;
            else if (n2.isDownstreamNodeOf(n1.self())) return 1;
            else return 0;
        })).collect(Collectors.toList());
    }


}
