package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nfu.event.NFULivingEvent;
/**
 * Posted on both sides before {@link Mob#mobInteract} is called. This event will not be posted if {@link Mob#mobInteract} is not invoked somehow.
 * <p>Not cancellable or having an event result ({@link Event.Result}), but holds an {@link InteractionResult} as result. If the result is set to "consumes action" i.e. {@code SUCCESS},
 * {@code CONSUME} or {@code CONSUME_PARTIAL}, the following {@link Mob#mobInteract} will be skipped.
 * <p>This event is handled in {@link Mob#interact}, and will be skipped by cancelling {@link EntitySpecificInteractionEvent}.
 */
public class MobInteractEvent extends NFULivingEvent<Mob>
{
	public final Player player;
	public final InteractionHand hand;
	private InteractionResult result;
	
	public MobInteractEvent(Mob entity, Player player, InteractionHand hand)
	{
		super(entity);
		this.player = player;
		this.hand = hand;
		this.result = InteractionResult.PASS;
	}

	public InteractionResult getInteractionResult() {
		return result;
	}

	public void setInteractionResult(InteractionResult result) {
		this.result = result;
	}

}
