package net.sodiumzh.nautils.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nautils.capability.CEntityTimerCapability;
import net.sodiumzh.nautils.capability.NaUtilsEntityCapProvider;
import net.sodiumzh.nautils.capability.NaUtilsEntitySerializableCapProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface CVanillaAnimalTamingProcessHandler extends INBTSerializable<CompoundTag>, CEntityTimerCapability<TamableAnimal> {

    public CompoundTag getNBT();

    public static class Impl implements CVanillaAnimalTamingProcessHandler
    {
        private CompoundTag nbt = new CompoundTag();
        private final Map<String, Integer> timer = new HashMap<>();
        private final IUsesTamingProcess mob;

        public Impl(IUsesTamingProcess mob) {
            this.mob = mob;
        }

        @Override
        public Map<String, Integer> getTimerMap() {
            return timer;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag save = new CompoundTag();
            save.put("timer", this.saveTimer());
            save.put("nbt", this.nbt);
            return save;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.nbt = nbt.getCompound("nbt").copy();
            this.loadTimerFromNBT(nbt.getCompound("timer"));
        }

        @Override
        public void tick() {
            mob.getProcess().serverTick(mob.asMob());
        }

        @Override
        public TamableAnimal getEntity() {
            return mob.asMob();
        }

        @Override
        public CompoundTag getNBT() {
            return nbt;
        }
    }

}
