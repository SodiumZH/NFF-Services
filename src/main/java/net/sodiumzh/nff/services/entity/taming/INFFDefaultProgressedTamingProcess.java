package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.entity.component.preset.EntityTimerComponent;
import net.sodiumzh.nfu.entity.taming.ITamingProcessWithProgress;
import net.sodiumzh.nfu.util.NFUEntityStatics;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link ITamingProcessWithProgress} of NFF. This progress allows only one player in progress,
 * and can be reset if the ongoing player is not present in the game.
 * <p>Note: this implementation is CNFFTamable specific.
 */
public interface INFFDefaultProgressedTamingProcess<T extends Mob> extends ITamingProcessWithProgress<T> {

    public static final String TIMER_KEY_ITEM_COOLDOWN = "itemCooldown";
    // Indicates if a player is in progress and the progress. DO NOT CALL IT ANYWHERE other than getProgressValue and setProgressValue!
    public static final String NBT_KEY_PROGRESS_VALUE = "progress";
    public static final String NBT_KEY_ONGOING_PLAYER = "ongoingPlayer";
    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    /**
     * Get the progress if the UUID is ongoing, or empty if not. To get the progress of whoever in process, use the
     * UUID-insensitive version.
     */
    @Override
    public default Optional<Double> getProgressValue(T mob, UUID playerUUID)
    {
        if (this.getOngoingPlayerUUID(mob).map(uuid -> Objects.equals(playerUUID, uuid)).orElse(false))
            return this.getProgressValue(mob);
        return Optional.empty();
    }

    /**
     * Note: this method DON'T DO ANYTHING if the ongoing player is not the input player to prevent unintentional ongoing
     * player change. Use {@link INFFDefaultProgressedTamingProcess#setOngoingPlayer} to switch ongoing player before setting this value.
     * To ensure the value is set, Use the UUID-insensitive version.
     */
    @Override
    public default void setProgressValue(T mob, UUID playerUUID, double value) {
        if (this.getOngoingPlayerUUID(mob).map(uuid -> !Objects.equals(uuid, playerUUID)).orElse(false)) return;
        NFFTamableComponent.getOptional(mob).ifPresent(c -> {
            c.getGeneralNBT().putUUID(NBT_KEY_ONGOING_PLAYER, playerUUID);
            c.getGeneralNBT().putDouble(NBT_KEY_PROGRESS_VALUE, value);
        });
    }

    /**
     * Always set player and progress value. This method will remove the current ongoing player and force set the ongoing player
     * to the input player. To prevent player changing, use {@link INFFDefaultProgressedTamingProcess#setProgressValue}.
     */
    public default void forceSetProgressValue(T mob, UUID playerUUID, double value) {
        this.setOngoingPlayer(mob, playerUUID);
        this.setProgressValue(mob, playerUUID, value);
        this.setProgressValue(mob, playerUUID, value);
    }
    
    /**
     * Note: this method DON'T DO ANYTHING if the ongoing player is not the input player to prevent unintentional removal.
     * Use {@link TamingProcessItemGivingProgress#setOngoingPlayer} to switch ongoing player before removing.
     * To ensure the value is set, Use the UUID-insensitive version.
     */
    @Override
    public default void removeProgressValue(T mob, UUID playerUUID) {
        if (this.getOngoingPlayerUUID(mob).map(uuid -> Objects.equals(uuid, playerUUID)).orElse(true))
        {
            NFFTamableComponent.getOptional(mob).ifPresent(c -> {
                c.getGeneralNBT().remove(NBT_KEY_ONGOING_PLAYER);
                c.getGeneralNBT().remove(NBT_KEY_PROGRESS_VALUE);
            });
        }
    }

    /**
     * Set the progress value. As it allows only on player in progress, it's not necessary to specify the player UUID, 
     * and works correctly even if the player is not online.
     */
    public default Optional<Double> getProgressValue(T mob)
    {
        return this.getOngoingPlayerUUID(mob).isPresent() ? 
                Optional.of(NFFTamableComponent.getOptional(mob).map(c -> c.getGeneralNBT().getDouble(NBT_KEY_PROGRESS_VALUE)).orElseThrow()) :
                Optional.empty();
    }

    /**
     * Set the progress value. As it allows only on player in progress, it's not necessary to specify the player UUID, 
     * and works correctly even if the player is not online.
     * <p>Note: this method will not do anything if it's not in progress with any player, but works if the player is not online.
     * To set the ongoing player together with the value if it's previously not in progress, use the UUID-sensitive version.
     */
    public default void setProgressValue(T mob, double value) {
        if (this.getOngoingPlayerUUID(mob).isPresent())
            NFFTamableComponent.getOptional(mob).ifPresent(c -> c.getGeneralNBT().putDouble(NBT_KEY_PROGRESS_VALUE, value));
    }

    public default void setProgressValueIfPlayerAbsent(T mob, UUID playerUUID, double value) {
        if (this.getOngoingPlayerUUID(mob)
                .map(uuid -> NFUEntityStatics.findPlayerInAllDimensions(uuid, mob.level())
                .isEmpty()).orElse(true)) {
            this.removeProgressValue(mob);
            this.setProgressValue(mob, playerUUID, value);
        }
            
            
    }
    
    /**
     * Remove the progress value. As it allows only on player in progress, it's not necessary to specify the player UUID, 
     * and works correctly even if the player is not online.
     */
    public default void removeProgressValue(T mob) {
        NFFTamableComponent.getOptional(mob).ifPresent(c -> {
            c.getGeneralNBT().remove(NBT_KEY_ONGOING_PLAYER);
            c.getGeneralNBT().remove(NBT_KEY_PROGRESS_VALUE);
        });
    }
    
    /**
     * Get the mob's ongoing player UUID. The player is not necessarily present in the world. Empty if it doesn't have one.
     */
    public default Optional<UUID> getOngoingPlayerUUID(T mob) {
        return NFFTamableComponent.getOptional(mob)
            .filter(tamable -> tamable.getGeneralNBT().hasUUID(NBT_KEY_ONGOING_PLAYER))
            .map(tamable -> tamable.getGeneralNBT().getUUID(NBT_KEY_ONGOING_PLAYER));
    }

    /**
     * Get the mob's ongoing player if present in the world (in any dimension). Empty if the player is offline or the uuid
     * isn't present.
     */
    public default Optional<Player> getOngoingPlayer(T mob) {
        UUID uuid = getOngoingPlayerUUID(mob).orElse(null);
        if (uuid == null) return Optional.empty();
        return NFUEntityStatics.findPlayerInAllDimensions(uuid, mob.level());
    }

    /**
     * Get the mob's ongoing player only if the player is in the same level (dimension) of the mob.
     */
    public default Optional<Player> getOngoingPlayerInLevel(T mob) {
        UUID uuid = getOngoingPlayerUUID(mob).orElse(null);
        if (uuid == null) return Optional.empty();
        return Optional.ofNullable(mob.level().getPlayerByUUID(uuid));
    }

    /**
     * Set the ongoing player. Input null to remove ongoing player.
     */
    public default void setOngoingPlayer(T mob, @Nullable UUID player) {
        NFFTamableComponent.getOptional(mob).ifPresent(tamable -> {
            if (player != null && !Objects.equals(player, EMPTY_UUID)) {
                tamable.getGeneralNBT().putUUID(NBT_KEY_ONGOING_PLAYER, player);
            } else {
                tamable.getGeneralNBT().remove(NBT_KEY_ONGOING_PLAYER);
                tamable.getGeneralNBT().remove(NBT_KEY_PROGRESS_VALUE);
            }
        });
    }

    /**
     * Check if the player (uuid) is ongoing.
     */
    public default boolean isOngoingPlayer(T mob, UUID player) {
        return this.getOngoingPlayerUUID(mob).map(uuid -> Objects.equals(uuid, player)).orElse(false);
    }

    /**
     * Check if there is an ongoing player other than the input player (uuid), no matter online or offline.
     */
    public default boolean isOtherPlayerOngoing(T mob, UUID player) {
        return this.getOngoingPlayerUUID(mob).map(p -> !Objects.equals(p, player)).orElse(false);
    }

    /**
     * Check if there is an ongoing player online other than the input player (uuid).
     */
    public default boolean isOtherPresentingPlayerOngoing(T mob, UUID player) {
        return this.getOngoingPlayer(mob).map(p -> !Objects.equals(p.getUUID(), player)).orElse(false);
    }

    public default int getCurrentCooldown(T mob) {
        return NFFTamableComponent.getOptional(mob).orElseThrow().getTimerComponent().getGeneralTimer(TIMER_KEY_ITEM_COOLDOWN)
            .map(EntityTimerComponent.Timer::getTicksRemaining).orElse(0);
    }

    /**
     * Set the current cooldown ticks.
     * <p>If non-positive, it will remove cooldown, but not posting timer expire event. It's recommended to use
     * {@link TamingProcessItemGivingProgress#removeCurrentCooldown} instead to remove.
     */
    public default void setCurrentCooldown(T mob, int ticks) {
        if (ticks <= 0) removeCurrentCooldown(mob, false);
        else NFFTamableComponent.getTimerComponent(mob).ifPresent(tc -> tc.addTimer(TIMER_KEY_ITEM_COOLDOWN, ticks, true));
    }

    public default void removeCurrentCooldown(T mob, boolean postExpireEvent) {
        NFFTamableComponent.getTimerComponent(mob).ifPresent(tc -> tc.removeGeneralTimer(TIMER_KEY_ITEM_COOLDOWN));
    }

    /**
     * Add a delta value to a progress value.
     * WARNING: this method will do nothing if the player is not in process.
     * WARNING: this method will not handle interruption or finalization if the progress reaches 0 or 1.
     */
    public default void addProgressValue(T mob, double deltaValue)
    {
        this.setProgressValue(mob, this.getProgressValue(mob)
                .map(val -> val + deltaValue).orElse(deltaValue));
    }
}
