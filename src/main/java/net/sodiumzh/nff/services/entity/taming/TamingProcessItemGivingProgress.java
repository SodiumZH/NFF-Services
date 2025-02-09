package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sodiumzh.nautils.entity.MobApplicableItemTable;
import net.sodiumzh.nautils.entity.taming.ITamingProcess;
import net.sodiumzh.nautils.entity.taming.TamingInteractionResult;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;
import net.sodiumzh.nautils.statics.NaUtilsItemStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;
import net.sodiumzh.nautils.statics.NaUtilsParticleStatics;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;


public abstract class TamingProcessItemGivingProgress extends TamingProcessItemGiving implements INFFDefaultProgressedTamingProcess<Mob> {

	@Deprecated
	protected Random rnd = new Random();

	@Nullable
	protected Supplier<MobApplicableItemTable> tamingItemTableOverride = null;
	@Override
	public TamingInteractionResult handleInteract(Player player, Mob mob, InteractionHand hand) {

		TamingInteractionResult result = TamingInteractionResult.unhandled(player.level);
		CNFFTamable tamable = CNFFTamable.getOptional(mob).resolve().orElse(null);
		if (tamable == null) return TamingInteractionResult.unhandled(player.level);

		if (!player.level.isClientSide)
		{
			if (!player.isShiftKeyDown()
					&& (isItemAcceptableInternal(player.getMainHandItem(), player, tamable.getEntity())
						|| player.getMainHandItem().is(Items.DEBUG_STICK))	// Item is acceptable.
					&& hand.equals(InteractionHand.MAIN_HAND)	// Debug Stick is resolved as a progress+1.00 debug tool
					&& !(mob.isPassenger() && this.shouldBlockOnRiding())	// Prevent player from sticking the mob with boat to tame
					&& additionalConditions(player, mob)) {
				// Now player is holding an acceptable item and using on the mob

				// Fail if another online player is ongoing
				if (this.isOtherPresentingPlayerOngoing(mob, player.getUUID()))
				{
					sendParticlesOnActionCooldown(mob);
					NaUtilsMiscStatics.printToScreen(NaUtilsInfoStatics.createTranslatable(
							"info.nffservices.other_player_ongoing", this.getOngoingPlayer(mob).map(Player::getName).orElseThrow()),
							player);
					result.setHandled();
				}
				// Fail if the mob is angry
				if (tamable.isAngryAt(player) && !shouldIgnoreAnger()) {
					sendParticlesOnAngry(mob);
					this.debugPrint(player, "Anger cooldown: " + Integer.toString(tamable.getRemainingForgivingTicks(player) / 20) + " s.");
					result.setHandled();
				}
				// Fail if in cooldown
				else if (TIMER_KEY_ITEM_COOLDOWN.getRemainingTime(mob) != 0) {
					this.debugPrint(player,"Action cooldown " + Integer.toString(this.getCurrentCooldown(mob) / 20) + " s.");
					sendParticlesOnActionCooldown(mob);
					result.setHandled();
				}
				// Success, process the progress value
				else {
					ItemStack mainhand = player.getMainHandItem();
					ItemStack givenCopy = mainhand.copy();
					this.setProgressIfAbsent(mob, player.getUUID(), 0d);
					double currentProgress = this.getProgressValue(mob, player.getUUID()).orElseThrow();
					double oldProgress = currentProgress;

					// Debug Stick is reserved, it will immediately give the mob 1.01 progress.
					if (mainhand.is(Items.DEBUG_STICK)) {
						currentProgress += 1.01;
						this.setProgressValue(mob, player.getUUID(), currentProgress);
					}
					// A normal item is given now
					else {
						// Calculate the progress
						currentProgress += getProgressGainInternal(mainhand, player, mob, oldProgress);
						if (currentProgress <= 0)
							currentProgress = 0;
						// Handle item consume
						if (!player.isCreative() && shouldItemConsumeInternal(player.getMainHandItem(), mob)) {
							player.getMainHandItem().shrink(1);
							NaUtilsItemStatics.giveOrDrop(player, player.getMainHandItem().getCraftingRemainingItem());
						}
						NaUtilsItemStatics.giveOrDrop(player, getReturnedItem(player, mob, givenCopy, oldProgress, currentProgress));
						// Assign the progress
						if (currentProgress > 0)
							this.setProgressValue(mob, player.getUUID(), currentProgress);
						else interrupt(player, mob, true);
					}
					this.debugPrint(player, "Progress Value: " + Double.toString(currentProgress));
					// Progress value processing end, finalize
					// Check and tame if reaches 1
					if (currentProgress >= 1 - 1e-12d) {    // 1.0 actually, avoiding potential float errors
						// Reaches 1, enter the final actions. Usually the mobs will be tamed after the final actions.
						result.setTamedMob(finalActions(player, mob));
					} else {
						// Not satisfied, put data
						this.setOngoingPlayer(mob, player.getUUID());
						TIMER_KEY_ITEM_COOLDOWN.setTimer(mob, this.getItemGivingCooldownTicks());
						this.afterItemGiven(player, mob, givenCopy);
						this.onItemGiven(player, mob, givenCopy, oldProgress, currentProgress);
						sendParticlesOnItemReceived(mob);
						sendProgressHeart(mob, oldProgress, currentProgress, deltaProgressPerHeart());
						result.setHandled();
					}
				}
			}
		}
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
	protected double getProgressToAdd(ItemStack item, Player player, Mob mob, double oldProc) {
		throw new IllegalStateException("NFFServices-TamingProcessItemGivingProgress: missing acceptable item info. " +
				"You must either use ItemApplyingToMobTable by calling setItemGivingTableOverride(), " +
				"or override both isItemAcceptable() and getProcValueToAdd() to define it in code.");
	};

	/**
	 * Don't force override here as sometimes we use item tables ({@code setItemGivingTableOverride})
	 */
	@Override
	public boolean isItemAcceptable(ItemStack itemstack) {
		throw new IllegalStateException("NFFServices-TamingProcessItemGivingProgress: missing acceptable item info. " +
				"You must either use ItemApplyingToMobTable override by calling setItemGivingTableOverride(), " +
				"or override both isItemAcceptable() and getProgressValueToAdd() to define it in code.");
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
		else return this.getProgressToAdd(item, player, mob, oldProc);
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
	 * Get how much progress it represents for each heart particle.
	 */
	protected double deltaProgressPerHeart()
	{
		return 0.2d;
	}

	@Override
	public void serverTick(Mob mob)
	{
	}
	
	@Override
	public void interrupt(Player player, Mob mob, boolean isQuiet) {
		CNFFTamable.getOptional(mob).ifPresent((l) ->
		{
			if (isInProcess(player, mob) && !isQuiet)
			{
				sendParticlesOnInterrupted(mob);
			}
			this.removeProgressValue(mob, player.getUUID());
		});
	}
	
	@Override
	public boolean interruptAll(Mob mob, boolean isQuiet)
	{
		boolean res = this.getOngoingPlayerUUID(mob).isPresent();
		this.setOngoingPlayer(mob, null);
		if (res && !isQuiet)
			sendParticlesOnInterrupted(mob);
		return res;
	}
	
	@Override
	public boolean isInProcess(Player player, Mob mob)
	{
		return this.getProgressValue(mob, player.getUUID()).isPresent();
	}

	@Override
	public Optional<Double> getProgressValue(Mob mob, UUID playerUUID)
	{
		if (this.getOngoingPlayerUUID(mob).map(uuid -> !Objects.equals(playerUUID, uuid)).orElse(true))
			return Optional.empty();
		return Optional.of(CNFFTamable.get(mob).getGeneralNBT().getDouble(NBT_KEY_PROGRESS_VALUE));
	}

	/**
	 * Note: this method DON'T DO ANYTHING if the ongoing player is not the input player to prevent unintentional ongoing
	 * player change. Use {@link TamingProcessItemGivingProgress#setOngoingPlayer} to switch ongoing player before setting this value.
	 */
	@Override
	public void setProgressValue(Mob mob, UUID playerUUID, double value) {
		if (this.getOngoingPlayerUUID(mob).map(uuid -> !Objects.equals(uuid, playerUUID)).orElse(false)) return;
		CNFFTamable.get(mob).getGeneralNBT().putUUID(NBT_KEY_ONGOING_PLAYER, playerUUID);
		CNFFTamable.get(mob).getGeneralNBT().putDouble(NBT_KEY_PROGRESS_VALUE, value);
	}

	/**
	 * Note: this method DON'T DO ANYTHING if the ongoing player is not the input player to prevent unintentional removal.
	 * Use {@link TamingProcessItemGivingProgress#setOngoingPlayer} to switch ongoing player before removing.
	 */
	@Override
	public void removeProgressValue(Mob mob, UUID playerUUID) {
		if (this.getOngoingPlayerUUID(mob).map(uuid -> Objects.equals(uuid, playerUUID)).orElse(true))
		{
			CNFFTamable.get(mob).getGeneralNBT().remove(NBT_KEY_ONGOING_PLAYER);
			CNFFTamable.get(mob).getGeneralNBT().remove(NBT_KEY_PROGRESS_VALUE);
		}
	}

	public void sendParticlesOnAngry(Mob target)
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

	@Override
	public ITamingProcess<Mob> asProcess() {
		return this;
	}
}
