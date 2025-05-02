package net.sodiumzh.nfu.entity.taming;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;

public abstract class VanillaAnimalTamingProcess implements ITamingProcess<TamableAnimal>{

    @Override
    public Mob doTaming(Player player, TamableAnimal target) {
        target.tame(player);
        return target;
    }

    @Override
    public abstract TamingInteractionResult handleInteract(Player player, TamableAnimal target, InteractionHand hand);

    @Override
    public abstract void serverTick(TamableAnimal mob);

    @Override
    public abstract void interrupt(Player player, TamableAnimal mob, boolean isQuiet);

    @Override
    public abstract boolean isInProcess(Player player, TamableAnimal mob);

    @Override
    public void onAngryAt(TamableAnimal mob, Player player, MobAngerReason reason) {
    }

    @Override
    public MobAngerRules getAngerRules() {
        return null;
    }
}
