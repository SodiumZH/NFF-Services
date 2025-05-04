package net.sodiumzh.nff.services.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * For NFF mobs extending neutral mob class to disable vanilla neutral mob features.
 */
public interface IBlockedNeutrality extends NeutralMob {

    @Override
    default int getRemainingPersistentAngerTime() {return 0;}

    @Override
    default void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime){}

    @Override
    @Nullable
    UUID getPersistentAngerTarget();

    @Override
    default void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget){}

    @Override
    default void startPersistentAngerTimer(){}

    @Override
    default void addPersistentAngerSaveData(CompoundTag pNbt) {
    }

    @Override
    default void readPersistentAngerSaveData(Level pLevel, CompoundTag pTag) {

    }

    @Override
    default void updatePersistentAnger(ServerLevel pServerLevel, boolean pUpdateAnger) {
    }

    @Override
    default boolean isAngryAt(LivingEntity pTarget) {
        return false;
    }

    @Override
    default boolean isAngryAtAllPlayers(Level pLevel) {
        return false;
    }

    @Override
    default boolean isAngry() {
        return false;
    }

    @Override
    default void playerDied(Player pPlayer) {
    }

    @Override
    default void forgetCurrentTargetAndRefreshUniversalAnger() {
    }

    @Override
    default void stopBeingAngry() {
    }

}
