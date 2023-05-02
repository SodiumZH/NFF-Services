package net.sodiumstudio.befriendmobs.entity.befriending.handlerpreset;

import java.util.Random;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.befriending.BefriendableMobInteractArguments;
import net.sodiumstudio.befriendmobs.entity.befriending.BefriendableMobInteractionResult;
import net.sodiumstudio.befriendmobs.entity.befriending.BefriendingHandler;
import net.sodiumstudio.befriendmobs.util.EntityHelper;

public abstract class HandlerItemGiving extends BefriendingHandler
{
	
	protected Random rnd = new Random();
	
	// Check if the mob accepts the item
	public abstract boolean isItemAcceptable(Item item);
	public boolean isItemAcceptable(ItemStack itemstack)
	{
		return isItemAcceptable(itemstack.getItem());
	}
	
	// Additional conditions when giving items
	public abstract boolean additionalConditions(Player player, Mob mob);
	
	// If true, the action can proceed even if the player is in the hatred list
	public boolean shouldIgnoreHatred() {return false;}
	
	// Actions when the item condition is satisfied
	// If the mob is befriended immediately, return it. Otherwise return null.
	public IBefriendedMob finalActions(Player player, Mob mob)
	{
		sendParticlesOnBefriended(mob);
		return befriend(player, mob);
	}
	
	public abstract int getItemGivingCooldownTicks();

	public void sendParticlesOnBefriended(Mob target)
	{
		EntityHelper.sendHeartParticlesToLivingDefault(target);
	}
	
	// If true, disable actions if the mob is a passenger
	public boolean shouldBlockOnRiding()
	{
		return true;
	}
	
}
