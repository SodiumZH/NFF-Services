package net.sodiumzh.nfu.entity.taming;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Base interface of all taming processes. This interface doesn't provide any implementation through events.
 * <p>Usually taming process should be singleton for each type of process. On usage, the process instance only operates
 * external objects (player, mob etc.) and do not store any data in itself.
 * @param <T>The tamable mob's base class.
 */
public interface ITamingProcess<T extends Mob> {

    /**
     * Tame the mob.
     */
    public Mob doTaming(Player player, T target);

    public TamingInteractionResult handleInteract(Player player, T target, InteractionHand hand);

    /**
     * Invoked on mob tick on server.
     */
    public void serverTick(T mob);

    /**
     * Invoked when a taming process should be aborted and reset.
     * @param player Target player.
     * @param mob Target mob.
     * @param isQuiet If true, it should not send signals (like particles).
     */
    public void interrupt(Player player, T mob, boolean isQuiet);

    /**
     * Interrupt all players' processes.
     */
    @DontOverride
    public boolean interruptAll(T mob, boolean isQuiet);

    /**
     * If true, the process will not be interrupted when the player dies.
     */
    public boolean dontInterruptOnPlayerDie();

    /** Indicates if the player is this mob's taming process. */
    public boolean isInProcess(Player player, T mob);

    /** Indicates if any player is in this mob's taming process. */
    @SuppressWarnings("resource")
    @DontOverride
    public default boolean isInAnyProcess(T mob)
    {
        if (mob.level().isClientSide)
            return false;
        for (Player player: mob.level().players())
        {
            if (isInProcess(player, mob))
                return true;
        }
        return false;
    }

    /** Execute when the mob attacks the player in taming process with it
     * Requires manual invoke in subclasses by listening to events.
     */
    public void onAttackProcessingPlayer(T mob, Player player, double damage);

    /**
     * Execute when the mob is attacked by the player in taming process with it.
     * Requires manual invoke in subclasses by listening to events.
     * */
    public void onAttackedByProcessingPlayer(T mob, Player player, double damage);

    /**
     * Invoked when the mob gets angry with a player.
     */
    public void onAngryAt(T mob, Player player, @Nullable MobAngerReason reason);

    /**
     * Get the rules about how the mob will get angry with a player.
     */
    public abstract MobAngerRules getAngerRules();

    /**
     * If true, the mob will not despawn if any player in the level is in process with it.
     */
    public boolean persistentIfInProcess();

    /* Util */

    /**
     * Do an action for all players in process that are present in the dimension.
     */
    @DontOverride
    public default void forAllPlayersInProcess(T mob, Consumer<Player> todo)
    {
        for (Player player: mob.level().players())
        {
            if (isInProcess(player, mob))
                todo.accept(player);
        }
    }

}
