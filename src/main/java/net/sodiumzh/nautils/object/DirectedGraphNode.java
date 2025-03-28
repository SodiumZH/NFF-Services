package net.sodiumzh.nautils.object;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Indicates the class is a node of some directed graph, i.e. keeping a set of other instances of the same class ("children nodes").
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

}
