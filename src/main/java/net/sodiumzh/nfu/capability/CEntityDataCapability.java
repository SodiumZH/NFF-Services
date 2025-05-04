package net.sodiumzh.nfu.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nfu.registry.NFUCaps;

/**
 * A simple capability serving as an additional data container.
 */
public interface CEntityDataCapability extends INBTSerializable<CompoundTag> {

    public CompoundTag getNBT();

    public static class Impl implements CEntityDataCapability {

        private CompoundTag nbt = new CompoundTag();

        public Impl(){}

        @Override
        public CompoundTag getNBT() {
            return nbt;
        }

        @Override
        public CompoundTag serializeNBT() {
            return nbt.copy();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.nbt = nbt.copy();
        }
    }

    public static CEntityDataCapability get(Entity e) {
        return e.getCapability(NFUCaps.CAP_ENTITY_DATA).orElse(new Impl());
    }

}
