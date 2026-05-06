package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class TamingProcessItemGiving extends NFFTamingProcess
{

	@Deprecated
	protected Random rnd = new Random();
	protected int itemGivingCooldownTicks = 100;

	/**
	 * Check if the mob accepts the item.
	 */
	public abstract boolean isItemAcceptable(ItemStack itemstack);

	/**
	 * If true, the item should consume after using
	 */
	public boolean shouldItemConsume(ItemStack itemstack)
	{
		return true;
	}
	
	// Additional conditions when giving items
	public abstract boolean additionalConditions(Player player, Mob mob);
	
	// If true, the action can proceed even if the mob is angry with the player
	public boolean shouldIgnoreAnger() {return false;}
	
	// Actions when the item condition is satisfied
	// If the mob is befriended immediately, return it. Otherwise return null.
	@Nullable
	public Mob finalActions(Player player, Mob mob)
	{
		sendParticlesOnBefriended(mob);
		return doTaming(player, mob);
	}

    @ApiStatus.NonExtendable
	public int getItemGivingCooldownTicks() {
        return itemGivingCooldownTicks;
    }

    @ApiStatus.NonExtendable
    public TamingProcessItemGiving setItemGivingCooldownTicks(int value) {
        this.itemGivingCooldownTicks = value;
        return this;
    }

	public void sendParticlesOnBefriended(Mob target)
	{
		NFUEntityStatics.sendHeartParticlesToLivingDefault(target);
	}
	
	// If true, disable actions if the mob is a passenger
	public boolean shouldBlockOnRiding()
	{
		return true;
	}
	
	/**
	 * Executed after item is given.
	 * Not executed when the condition is satisfied after giving. Handle this case in finalActions().
	 */
	public void afterItemGiven(Player player, Mob mob, ItemStack item) {}
	
}
