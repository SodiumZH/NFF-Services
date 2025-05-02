package net.sodiumzh.nfu.object;

/**
 * A field that can be changed for limited times. If exceeded, the setter will not do anything.
 */
public class LimitedMutable<T> {

    private T value;
    private int modificationCount = 0;
    private final int maxModification;

    public LimitedMutable(T obj, int maxModificationCount) {
        this.value = obj;
        assert(maxModificationCount >= 0);
        this.maxModification = maxModificationCount;
    }

    public T get() {
        return value;
    }

    /**
     * Try setting the value. If the modification count has exceeded the limit, it will not do anything.
     * @return Whether the value is set.
     */
    public boolean trySet(T newValue) {
        if (this.modificationCount < this.maxModification) {
            value = newValue;
            this.modificationCount++;
            return true;
        }
        else return false;
    }

}
