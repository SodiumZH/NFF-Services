package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.entity.component.preset.EntityDataComponent;
import net.sodiumzh.nfu.util.NFUNBTStatics;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class NFFTamableDataComponent extends EntityDataComponent<Mob> {

    protected final Map<UUID, CompoundTag> playerSpecificNBT = new HashMap<>();

    public NFFTamableDataComponent(Mob entity) {
        super(entity);
    }

    @Nonnull
    public Optional<CompoundTag> getPlayerSpecificNBT(Player player) {
        return Optional.ofNullable(playerSpecificNBT.get(player.getUUID()));
    }

    @Nonnull
    public Optional<CompoundTag> getPlayerSpecificNBT(UUID uuid) {
        return Optional.ofNullable(playerSpecificNBT.get(uuid));
    }

    /**
     * Get the nbt for a specific player if it's present, and create if absent.
     * This nbt is separated from general nbt, and has one nbt for each player.
     */
    public CompoundTag getOrCreatePlayerSpecificNBT(Player player) {
        return playerSpecificNBT.computeIfAbsent(player.getUUID(), p -> new CompoundTag());
    }

    public CompoundTag getOrCreatePlayerSpecificNBT(UUID uuid) {
        return playerSpecificNBT.computeIfAbsent(uuid, p -> new CompoundTag());
    }

    public boolean hasPlayerSpecificNBT(Player player) {
        return playerSpecificNBT.containsKey(player.getUUID());
    }

    public boolean hasPlayerSpecificNBT(UUID uuid) {
        return playerSpecificNBT.containsKey(uuid);
    }

    /**
     * Get all players which have a <i>non-empty</i> nbt in this mob.
     * <p>Note: It will return all UUIDs of which the nbt is present. There's no guarantee that the player is present
     * in the level.
     * <p>Note: This is not the timer.
     */
    public Set<UUID> getAllPlayersWithNBT() {
        return playerSpecificNBT.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.put("playerSpecificNBT", NFUNBTStatics.compoundTagFromMap(this.playerSpecificNBT, UUID::toString, c -> c));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        playerSpecificNBT.clear();
        playerSpecificNBT.putAll(NFUNBTStatics.mapFromCompoundTag(nbt.getCompound("playerSpecificNBT"), UUID::fromString, c -> (CompoundTag) c));
    }


}
