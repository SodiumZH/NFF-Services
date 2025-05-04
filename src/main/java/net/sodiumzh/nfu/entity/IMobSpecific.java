package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.Mob;

/**
 * For interfaces dedicated for mobs. This interface is for avoiding collision of mob getter methods among different
 * mob-specific interfaces.
 */
public interface IMobSpecific<T extends Mob> {

    public default T asMob() {
        return (T) this;
    }

}
