package net.sodiumzh.nfu.object;


import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nfu.exception.IllegalGenericsException;

import java.util.function.Consumer;

public interface IChainModifiable<T> {

    /**
     * Do some action to self. For making modifications in the chain declaration.
     */
    @DontOverride
    public default T modify(Consumer<T> action) {
        T t;
        try {
            t = (T)this;
        } catch (ClassCastException e) {
            throw new IllegalGenericsException("Wrong IChainModifiable usage: the generic type must be the class' own or super type.", e);
        }
        action.accept(t);
        return t;
    }

}
