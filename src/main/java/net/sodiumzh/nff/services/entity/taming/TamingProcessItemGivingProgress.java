package net.sodiumzh.nff.services.entity.taming;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sodiumzh.nautils.entity.ItemApplyingToMobTable;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nautils.statics.NaUtilsItemStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

import javax.annotation.Nullable;


public abstract class TamingProcessItemGivingProgress extends TamingProcessItemGiving{

	protected Random rnd = new Random();

	@Override
	public TamableInteractionResult handleInteract(TamableInteractArguments args) {
		Mob target = args.getTarget();
		Player player = args.getPlayer();
		TamableInteractionResult result = new TamableInteractionResult();

		args.execServer((l) -> {

			if (!player.isShiftKeyDown() 
					&& (isItemAcceptableInternal(player.getMainHandItem(), player, l.getOwner()) || player.getMainHandItem().is(Items.DEBUG_STICK))
					&& args.isMainHand() 
					&& !(target.isPassenger() && this.shouldBlockOnRiding())
					&& additionalConditions(player, target)) {
				// Block if in hatred
				if (l.isInHatred(player) && !shouldIgnoreHatred()) {
					sendParticlesOnHatred(target);
					NaUtilsDebugStatics.debugPrintToScreen("Hatred cooldown: " + Integer.toString(args.asCap().getHatredDuration(player) / 20) + " s."
							, player);
					result.setHandled();

				}
				// Block if in cooldown
				else if (l.getPlayerTimer(player, "item_cooldown") > 0) {
					NaUtilsDebugStatics.debugPrintToScreen(
							"Action cooldown " + Integer.toString(l.getPlayerTimer(player, "item_cooldown") / 20) + " s.",
							player);
					sendParticlesOnActionCooldown(target);
					// result.setHandled();
				} 
				else
				{
					ItemStack mainhand = player.getMainHandItem();
					ItemStack givenCopy = mainhand.copy();
					boolean isDebugStick = mainhand.is(Items.DEBUG_STICK);
					// Put a zero data first, otherwise if fulfilled after giving only one item, something unexpected
					// may happen due to missing proc_value tag
					// Because this tag is also used to indicate whether the player is in process
					if (!NaUtilsNBTStatics.containsPlayerData(l.getPlayerDataNbt(), player, "proc_value"))
						NaUtilsNBTStatics.putPlayerData(DoubleTag.valueOf(0), l.getPlayerDataNbt(), player,
								"proc_value");
					// Get amount already given
					DoubleTag currentValueTag = (DoubleTag) NaUtilsNBTStatics.getPlayerData(l.getPlayerDataNbt(), player, "proc_value");
					double procValue = currentValueTag == null ? 0 : currentValueTag.getAsDouble();
					double lastProcValue = procValue;	
					if (isDebugStick)
					{
						procValue += 1.01;
						// Immediately update tag, otherwise unexpected error occurs due to out-of-date tag value
						// (possibly 0.0)
						NaUtilsNBTStatics.putPlayerData(DoubleTag.valueOf(procValue), l.getPlayerDataNbt(), player, "proc_value");
					}
					else
					{
						procValue += getProgressGainInternal(mainhand, player, target, lastProcValue);
						if (procValue <= 0)
							procValue = 0;
						if (!player.isCreative() && shouldItemConsume(player.getMainHandItem()))
							player.getMainHandItem().shrink(1);
						NaUtilsItemStatics.giveOrDrop(player, getReturnedItem(player, target, givenCopy, lastProcValue, procValue));
						if (procValue > 0)
							NaUtilsNBTStatics.putPlayerData(DoubleTag.valueOf(procValue), l.getPlayerDataNbt(), player, "proc_value");
						else interrupt(player, target, true);
					}
					NaUtilsDebugStatics.debugPrintToScreen("Progress Value: " + Double.toString(procValue), player);
					if (procValue >= 0.9999999999d)
					{	// 1.0 actually, avoiding potential float errors
						// Satisfied
						finalActions(player, target);
						result.setHandled();
					} 
					else
					{
						// Not satisfied, put data
						sendParticlesOnItemReceived(target);
						sendProgressHeart(target, lastProcValue, procValue, deltaProcPerHeart());
						l.setPlayerTimer(player, "item_cooldown", this.getItemGivingCooldownTicks()); // Set cooldown
						this.afterItemGiven(player, target, givenCopy);
						this.onItemGiven(player, target, givenCopy, lastProcValue, procValue);
						result.setHandled();
					}
				}
			}
		});

		// ...................................
		/*args.execClient((l) -> {
			{
				if (shouldIgnoreHatred() || !l.isInHatred(player)) {
					if (!player.isShiftKeyDown() && isItemAcceptable(player.getMainHandItem())
							&& args.isMainHand())
					result.handled = true;
				}
			}
		});*/
		// ==============================
		return result;
	}

	/**
	 * Get progress gain from item input. If you're using {@code ItemApplyingToMobTable}, this will be
	 * skipped, and you can just override it to 0.
	 * @param item Item given.
	 * @param player Player doing this giving action.
	 * @param mob Target item.
	 * @param oldProc The progress value before giving.
	 * @return Progress gain for this giving action.
	 */
	protected abstract double getProcValueToAdd(ItemStack item, Player player, Mob mob, double oldProc);

	private boolean isItemAcceptableInternal(ItemStack item, Player player, Mob mob)
	{
		return Optional.ofNullable(this.getItemProcValueTable())
				.map(table -> (table.getOutput(mob, item) != null))
				.orElse(this.isItemAcceptable(item));
	}

	private double getProgressGainInternal(ItemStack item, Player player, Mob mob, double oldProc) {
		var table = this.getItemProcValueTable();
		if (table != null)
		{
			var output = table.getOutput(mob, item);
			return output != null ? output.amount() : 0d;
		}
		else return this.getProcValueToAdd(item, player, mob, oldProc);
	}

	/**
	 * Define progress gain for each item from table. If this method returns non-null, the progress value
	 * will be taken from the table and {@code getProcValueToAdd} will be skipped.
	 * @return Table to define progress gain for given items.
	 */
	@Nullable
	protected ItemApplyingToMobTable getItemProcValueTable() { return null; }

	protected void sendProgressHeart(Mob target, double procBefore, double procAfter, double deltaProcPerHeart)
	{
		int times = (int)(procAfter / deltaProcPerHeart) - (int)(procBefore / deltaProcPerHeart);
		for (int i = 0; i < times; ++i)
		{
			sendParticlesForProgressHeart(target);
		}
	}

	/**
	 * Get how many progress it represents for each heart particle.
	 */
	protected double deltaProcPerHeart()
	{
		return 0.2d;
	}

	@Override
	public void serverTick(Mob mob)
	{
		/*mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> {
			if (!shouldIgnoreHatred())
			{
				for (UUID uuid: l.getHatred())
				{
					if (l.hasPlayerData(mob.level.getPlayerByUUID(uuid), "proc_value")
							&& l.getPlayerDataDouble(mob.level.getPlayerByUUID(uuid), "proc_value") > 0d)
						this.interrupt(mob.level.getPlayerByUUID(uuid), mob, false);					
				}
			}
		});*/
	}
	
	@Override
	public void interrupt(Player player, Mob mob, boolean isQuiet) {
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
		{
			if (isInProcess(player, mob) && !isQuiet)
			{
				sendParticlesOnInterrupted(mob);
			}
			l.removePlayerData(player, "proc_value");			
		});
	}
	
	@Override
	public boolean interruptAll(Mob mob, boolean isQuiet)
	{
		boolean res = super.interruptAll(mob, isQuiet);
		if (res && !isQuiet)
			sendParticlesOnInterrupted(mob);
		return res;
	}
	
	@Override
	public boolean isInProcess(Player player, Mob mob)
	{
		CNFFTamable l = CNFFTamable.getCap(mob);
		return NaUtilsNBTStatics.containsPlayerData(l.getPlayerDataNbt(), player, "proc_value")
			&& ((DoubleTag) (NaUtilsNBTStatics.getPlayerData(l.getPlayerDataNbt(), player, "proc_value"))).getAsDouble() > 0;
	}
	
	/**
	 * Get current progress value for a player.
	 * @return Progress value, or -1 if player is not in process.
	 */
	public double getProgressValue(Mob mob, Player player)
	{
		if (!isInProcess(player, mob))
			return -1;
		else return ((DoubleTag) (NaUtilsNBTStatics.getPlayerData(CNFFTamable.getCap(mob).getPlayerDataNbt(), player, "proc_value"))).getAsDouble();
	}

	/**
	 * Add a delta value to a progress value.
	 * WARNING: this method will do nothing if the player is not in process.
	 * WARNING: this method will not handle interruption or befriending even if the progress reaches 0 or 1.
	 */
	public void addProgressValue(Mob mob, Player player, double deltaValue)
	{
		double oldValue = getProgressValue(mob, player);
		NaUtilsNBTStatics.putPlayerData(DoubleTag.valueOf(oldValue + deltaValue), 
				CNFFTamable.getCap(mob).getPlayerDataNbt(), player, "proc_value");
	}
	
	
	
	public void sendParticlesOnHatred(Mob target)
	{
		NaUtilsEntityStatics.sendAngryParticlesToLivingDefault(target);
	}
	
	public void sendParticlesOnActionCooldown(Mob target)
	{
		NaUtilsEntityStatics.sendSmokeParticlesToLivingDefault(target);
	}
	
	public void sendParticlesOnItemReceived(Mob target)
	{
		NaUtilsEntityStatics.sendGlintParticlesToLivingDefault(target);
	}

	public void sendParticlesOnInterrupted(Mob target)
	{
		NaUtilsEntityStatics.sendAngryParticlesToLivingDefault(target);
	}
	
	public void sendParticlesForProgressHeart(Mob target)
	{
		NaUtilsEntityStatics.sendParticlesToEntity(target, ParticleTypes.HEART, target.getBbHeight() - 0.5, 0.2d, 1, 1d);
	}

	/**
	 * Get the item stack that should be given to the player after giving an item.
	 * No item by default.
	 * @param itemGivenCopy ItemStack before giving.
	 * @param procBefore Progress value before giving.
	 * @param procAfter Progress value after giving.
	 */
	public ItemStack getReturnedItem(Player player, Mob mob, ItemStack itemGivenCopy, double procBefore, double procAfter)
	{
		return ItemStack.EMPTY;
	}
	
	/**
	 * Invoked after an item is given and after {@link TamingProcessItemGiving#afterItemGiven(Player, Mob, ItemStack)}.
	 * Not executed when the condition is satisfied after giving. Handle this case in finalActions().
	 * @param itemGivenCopy ItemStack before giving.
	 * @param procBefore Progress value before giving.
	 * @param procAfter Progress value after giving.
	 */
	public void onItemGiven(Player player, Mob mob, ItemStack itemGivenCopy, double procBefore, double procAfter) {}
	
}
