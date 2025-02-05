package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nautils.events.NaUtilsEntityEvent;

/**
 * Posted before {@link Player#interactOn} calls {@link Entity#interact} defined in entity class,
 * after {@link PlayerInteractEvent.EntityInteract}.
 * <p>{@link Cancelable}. If cancelled, {@link Entity#interact} will be skipped,
 * and the interaction will be passed to the next steps (e.g. {@link ItemStack#interactLivingEntity}).
 */
@Cancelable
public class EntitySpecificInteractionEvent extends NaUtilsEntityEvent<Entity> {
    private final Player player;
    private final InteractionHand hand;

    public EntitySpecificInteractionEvent(Entity entity, Player player, InteractionHand hand) {
        super(entity);
        this.player = player;
        this.hand = hand;
    }

    public Player getPlayer() {
        return player;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
