package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sodiumzh.nautils.entity.MobApplicableItemTable;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nautils.entity.taming.TamingInteractionResult;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsItemStatics;
import net.sodiumzh.nautils.statics.NaUtilsParticleStatics;
import net.sodiumzh.nff.services.entity.capability.CNFFTamable;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;


public abstract class TamingProcessItemGivingProgress extends TamingProcessItemGiving{

	protected Random rnd = new Random();
	protected String NBT_KEY_ITEM_COOLDOWN = "item_cooldown";
	protected String NBT_KEY_PROGRESS_VALUE = "proc_value";
	@Nullable
	protected Supplier<MobApplicableItemTable> tamingItemTableOverride = null;
	@Override
	public TamingInteractionResult handleInteract(Player player, Mob target, InteractionHand hand) {
		TamingInteractionResult result = TamingInteractionResult.unhandled(player.level());
		CNFFTamable tamable = target.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).orElse(
				new CNFFTamableImpl(target, MobAngerRules.NO_ANGER.get()));
		if (!player.level().isClientSide)
		{
			if (!player.isShiftKeyDown()
					&& (isItemAcceptableInternal(player.getMainHandItem(), player, tamable.getEntity())
						|| player.getMainHandItem().is(Items.DEBUG_STICK))
					&& hand.equals(InteractionHand.MAIN_HAND)
					&& !(target.isPassenger() && this.shouldBlockOnRiding())
					&& additionalConditions(player, target)) {
				// Block if in hatred
				if (tamable.isAngryAt(player) && !shouldIgnoreHatred()) {
					sendParticlesOnHatred(target);
					NaUtilsDebugStatics.debugPrintToScreen("Anger cooldown: "
									+ Integer.toString(tamable.getRemainingForgivingTicks(player) / 20) + " s.", player);
					result = TamingInteractionResult.handled(player.level());

				}
				// Block if in cooldown
				else if (tamable.getPlayerTimerRemainingTime(player, NBT_KEY_ITEM_COOLDOWN) > 0) {
					NaUtilsDebugStatics.debugPrintToScreen(
							"Action cooldown " + Integer.toString(tamable.getPlayerTimerRemainingTime(player, NBT_KEY_ITEM_COOLDOWN) / 20) + " s.",
							player);
					sendParticlesOnActionCooldown(target);
					// result.setHandled();
				} else {
					ItemStack mainhand = player.getMainHandItem();
					ItemStack givenCopy = mainhand.copy();
					boolean isDebugStick = mainhand.is(Items.DEBUG_STICK);
					// Put a zero data first, otherwise if fulfilled after giving only one item, something unexpected
					// may happen due to missing proc_value tag
					// Because this tag is also used to indicate whether the player is in process
					if (!tamable.getOrCreatePlayerSpecificNBT(player).contains(NBT_KEY_PROGRESS_VALUE))
						tamable.getOrCreatePlayerSpecificNBT(player).putDouble(NBT_KEY_PROGRESS_VALUE, 0d);
					// Get amount already given
					double procValue = tamable.getOrCreatePlayerSpecificNBT(player).getDouble(NBT_KEY_PROGRESS_VALUE);
					double lastProcValue = procValue;
					if (isDebugStick) {
						procValue += 1.01;
						// Immediately update tag, otherwise unexpected error occurs due to out-of-date tag value
						// (possibly 0.0)
						tamable.getOrCreatePlayerSpecificNBT(player).putDouble(NBT_KEY_PROGRESS_VALUE, procValue);
					} else {
						procValue += getProgressGainInternal(mainhand, player, target, lastProcValue);
						if (procValue <= 0)
							procValue = 0;
						if (!player.isCreative() && shouldItemConsumeInternal(player.getMainHandItem(), target)) {
							player.getMainHandItem().shrink(1);
							NaUtilsItemStatics.giveOrDrop(player,
								player.getMainHandItem().getItem().getContainerItem(player.getMainHandItem()));
						}
						NaUtilsItemStatics.giveOrDrop(player, getReturnedItem(player, target, givenCopy, lastProcValue, procValue));
						if (procValue > 0)
							tamable.getOrCreatePlayerSpecificNBT(player).putDouble(NBT_KEY_PROGRESS_VALUE, procValue);
						else interrupt(player, target, true);
					}
					NaUtilsDebugStatics.debugPrintToScreen("Progress Value: " + Double.toString(procValue), player);
					if (procValue >= 0.9999999999d) {    // 1.0 actually, avoiding potential float errors
						// Satisfied
						finalActions(player, target);
						result.setHandled();
					} else {
						// Not satisfied, put data
						sendParticlesOnItemReceived(target);
						sendProgressHeart(target, lastProcValue, procValue, deltaProcPerHeart());
						tamable.putPlayerTimer(player, NBT_KEY_ITEM_COOLDOWN, this.getItemGivingCooldownTicks());
						this.afterItemGiven(player, target, givenCopy);
						this.onItemGiven(player, target, givenCopy, lastProcValue, procValue);
						result.setHandled();
					}
				}
			}
		}

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
	protected double getProcValueToAdd(ItemStack item, Player player, Mob mob, double oldProc) {
		throw new IllegalStateException("NFFServices-TamingProcessItemGivingProgress: missing acceptable item info. " +
				"You must either use ItemApplyingToMobTable by calling setItemGivingTableOverride(), " +
				"or override both isItemAcceptable() and getProcValueToAdd() to define it in code.");
	};

	/**
	 * Don't force override here as sometimes we use item tables ({@code setItemGivingTableOverride})
	 */
	public boolean isItemAcceptable(ItemStack itemstack) {
		throw new IllegalStateException("NFFServices-TamingProcessItemGivingProgress: missing acceptable item info. " +
				"You must either use ItemApplyingToMobTable override by calling setItemGivingTableOverride(), " +
				"or override both isItemAcceptable() and getProcValueToAdd() to define it in code.");
	}

	private boolean isItemAcceptableInternal(ItemStack item, Player player, Mob mob)
	{
		return Optional.ofNullable(this.getItemGivingTableOverride())
				.map(table -> (table.get().getOutput(mob, item) != null))
				.orElseGet(() -> this.isItemAcceptable(item));
	}

	private double getProgressGainInternal(ItemStack item, Player player, Mob mob, double oldProc) {
		var table = this.getItemGivingTableOverride();
		if (table != null)
		{
			var output = table.get().getOutput(mob, item);
			return output != null ? output.amount() : 0d;
		}
		else return this.getProcValueToAdd(item, player, mob, oldProc);
	}

	@Nullable
	public final Supplier<MobApplicableItemTable> getItemGivingTableOverride() { return tamingItemTableOverride; }

	public final TamingProcessItemGivingProgress setItemGivingTableOverride(Supplier<MobApplicableItemTable> override)
	{
		this.tamingItemTableOverride = override;
		return this;
	}

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
	public void interrupt(Player player, Mob mob, boolean isQuiet) {
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
		{
			if (isInProcess(player, mob) && !isQuiet)
			{
				sendParticlesOnInterrupted(mob);
			}
			l.getPlayerSpecificNBT(player).ifPresent(nbt -> nbt.remove(NBT_KEY_PROGRESS_VALUE));
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
		CNFFTamable tamable = CNFFTamable.get(mob);
		return tamable.getPlayerSpecificNBT(player).map(nbt -> nbt.getDouble(NBT_KEY_PROGRESS_VALUE) > 0)
				.orElse(false);
	}
	
	/**
	 * Get current progress value for a player.
	 * @return Progress value, or -1 if player is not in process.
	 */
	public double getProgressValue(Mob mob, Player player)
	{
		if (!isInProcess(player, mob))
			return -1;
		return CNFFTamable.get(mob).getPlayerSpecificNBT(player).map(nbt -> nbt.getDouble(NBT_KEY_PROGRESS_VALUE)).orElse(0d);
	}

	/**
	 * Add a delta value to a progress value.
	 * WARNING: this method will do nothing if the player is not in process.
	 * WARNING: this method will not handle interruption or befriending even if the progress reaches 0 or 1.
	 */
	public void addProgressValue(Mob mob, Player player, double deltaValue)
	{
		double oldValue = getProgressValue(mob, player);
		CNFFTamable.get(mob).getOrCreatePlayerSpecificNBT(player).putDouble(NBT_KEY_PROGRESS_VALUE,oldValue + deltaValue);
	}

	public void sendParticlesOnHatred(Mob target)
	{
		NaUtilsParticleStatics.sendAngryParticlesToEntityDefault(target);
	}
	
	public void sendParticlesOnActionCooldown(Mob target)
	{
		NaUtilsParticleStatics.sendSmokeParticlesToEntityDefault(target);
	}
	
	public void sendParticlesOnItemReceived(Mob target)
	{
		NaUtilsParticleStatics.sendGlintParticlesToEntityDefault(target);
	}

	public void sendParticlesOnInterrupted(Mob target)
	{
		NaUtilsParticleStatics.sendAngryParticlesToEntityDefault(target);
	}
	
	public void sendParticlesForProgressHeart(Mob target)
	{
		NaUtilsParticleStatics.sendParticlesToEntity(target, ParticleTypes.HEART, target.getBbHeight() - 0.5, 0.2d, 1, 1d);
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

	private boolean shouldItemConsumeInternal(ItemStack itemstack, Mob mob)
	{
		if (this.getItemGivingTableOverride() != null && this.getItemGivingTableOverride().get() != null)
		{
			var output = this.getItemGivingTableOverride().get().getOutputGetter(mob, itemstack);
			if (output != null)
				return !output.isNoConsume();
			else return true;
		}
		else return shouldItemConsume(itemstack);
	}
}
