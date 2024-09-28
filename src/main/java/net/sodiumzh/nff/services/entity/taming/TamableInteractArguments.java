package net.sodiumzh.nff.services.entity.taming;

import javax.annotation.Nonnull;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.LogicalSide;
import net.sodiumzh.nff.services.entity.capability.CNFFTamable;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class TamableInteractArguments {

	private LogicalSide side;
	private Player player;
	private Mob target;
	private InteractionHand hand;
	
	private TamableInteractArguments()
	{
		side = LogicalSide.SERVER;
		player = null;
		target = null;
		hand = null;
	}
	
	public static TamableInteractArguments of(LogicalSide side, @Nonnull Player player, @Nonnull Mob target, @Nonnull InteractionHand hand)
	{
		TamableInteractArguments res = new TamableInteractArguments();
		res.side = side;
		res.player = player;
		res.target = target;
		res.hand = hand;
		if (!target.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent())
			throw new IllegalStateException("BefriendableMobInteraction event: target is not befriendable.");
		return res;
	}
	
	public LogicalSide getSide()
	{
		return side;
	}
	
	public boolean isClient()
	{
		return side == LogicalSide.CLIENT;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Mob getTarget()
	{
		return target;
	}
	
	public InteractionHand getHand()
	{
		return hand;
	}
	
	public boolean isMainHand()
	{
		return hand == InteractionHand.MAIN_HAND;
	}
	
	private void execInternal(NonNullConsumer<CNFFTamable> consumer)
	{
		target.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent(consumer);
	}
	
	// Do something with the mob's capability, both on server and client
	public void execBoth(NonNullConsumer<CNFFTamable> consumer)
	{
		execInternal(consumer);
	}
	
	// Do something with the mob's capability, only on client
	public void execClient(NonNullConsumer<CNFFTamable> consumer)
	{
		if (isClient())
			execInternal(consumer);
	}
	
	// Do something with the mob's capability, only on server	
	public void execServer(NonNullConsumer<CNFFTamable> consumer)
	{
		if (!isClient())
			execInternal(consumer);
	}
	
	// Get target mob as CNFFTamable capability
	public CNFFTamable asCap()
	{
		return CNFFTamable.getCap(target);
		
	}
	
}
