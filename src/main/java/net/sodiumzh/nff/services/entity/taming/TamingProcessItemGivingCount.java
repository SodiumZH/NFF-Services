package net.sodiumzh.nff.services.entity.taming;

import java.util.UUID;

import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.entity.capability.CNFFTamable;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

/**
 * @author SodiumZH
 * Do item giving process which requires to give a certain count of items.
 * In this preset item-giving process handler, mob receives a given count of items, and
 * after the count demand is satisfied, it does the final actions (customizable, befriend by default)
 * <p> WARNING: this class is not well-tested and is probably not working well.
 * 
 * Related NBT tags:
 * (Player Data) overall_amount: the demanded count. It will not change for one player but varies for different players.
 * (Player Data) already_given: the items player already given. 
 * (Player Timer) item_cooldown: cooldown ticks before the next item can be given.
 */

public abstract class TamingProcessItemGivingCount extends TamingProcessItemGiving
{

	@Override
	public TamableInteractionResult handleInteract(TamableInteractArguments args) {
		Mob target = args.getTarget();
		Player player = args.getPlayer();
		TamableInteractionResult result = new TamableInteractionResult();

		args.execServer((l) ->
		{
			if (!player.isShiftKeyDown() 
					&& isItemAcceptable(player.getMainHandItem()) 
					&& args.isMainHand()
					&& additionalConditions(player, target))
			{
				// Block if in hatred
				if (l.isInHatred(player) && !shouldIgnoreHatred())
				{
					sendParticlesOnHatred(target);
					NaUtilsDebugStatics.debugPrintToScreen("Unable to befriend: in hatred list.", player);
					result.setHandled();

				}
				// Block if in cooldown
				else if (l.getPlayerTimer(player, "item_cooldown") > 0)
				{
					NaUtilsDebugStatics.debugPrintToScreen(
							"Action cooldown " + Integer.toString(l.getPlayerTimer(player, "item_cooldown") / 20) + " s.",
							player);
					sendParticlesOnActionCooldown(target);
				} else
				{
					// Get overall cake amount needed, or create if not existing
					IntTag overallAmountTag = (IntTag) NaUtilsNBTStatics.getPlayerData(l.getPlayerDataNbt(), player,
							"overall_amount");
					int overallAmount;
					if (overallAmountTag == null)
					{
						overallAmount = getRequiredAmount(); 						
						NaUtilsNBTStatics.putPlayerData(IntTag.valueOf(overallAmount), l.getPlayerDataNbt(), player,
								"overall_amount");
					} else
						overallAmount = overallAmountTag.getAsInt();
					// Get amount already given
					IntTag alreadyGivenTag = (IntTag) NaUtilsNBTStatics.getPlayerData(l.getPlayerDataNbt(), player,
							"already_given");
					int alreadyGiven = alreadyGivenTag == null ? 0 : alreadyGivenTag.getAsInt();
					// Give cake
					if (!player.isCreative() && shouldItemConsume(player.getMainHandItem()))
					{
						player.getMainHandItem().shrink(1);
					}
					alreadyGiven++;
					NaUtilsMiscStatics.printToScreen(
							"Item(s) given: " + Integer.toString(alreadyGiven) + " / " + Integer.toString(overallAmount),
							player);
					if (alreadyGiven == overallAmount)
					{
						// Satisfied
						finalActions(player, target);
						result.setHandled();
					} else
					{
						sendParticlesOnItemReceived(target);
						// Not satisfied, put data
						NaUtilsNBTStatics.putPlayerData(IntTag.valueOf(alreadyGiven), l.getPlayerDataNbt(), player,
								"already_given");
						l.setPlayerTimer(player, "item_cooldown", this.getItemGivingCooldownTicks()); // Set 10s cooldown
						this.afterItemGiven(player, target, player.getMainHandItem());
						result.setHandled();
					}
				}
			}
		});

		// ...................................
		args.execClient((l) ->
		{
			{
				if (l.isInHatred(player) && !shouldIgnoreHatred())
				{
					if (!player.isShiftKeyDown() 
							&& isItemAcceptable(player.getMainHandItem()) 
							&& args.isMainHand()
							&& additionalConditions(player, target))
						result.handled = true;
				} else
				{
				}
			}
		});
		// ==============================
		return result;
	}

	public abstract int getRequiredAmount(); 
	
	@Override
	public void serverTick(Mob mob)
	{
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> {
			/*for (String key: l.getNbt().getAllKeys())
			{

			}*/
			
			if (!shouldIgnoreHatred())
			{
				for (UUID uuid: l.getHatred())
				{
					if (isInProcess(mob.level.getPlayerByUUID(uuid), mob))
						this.interrupt(mob.level.getPlayerByUUID(uuid), mob, false);					
				}
			}
		});
	}
	
	@Override
	public void interrupt(Player player, Mob mob, boolean isQuiet) {
		mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
		{
			if (l.hasPlayerData(player, "already_given") 
					&& l.getPlayerDataInt(player, "already_given") > 0
					&& !isQuiet)
			{
				sendParticlesOnInterrupted(mob);
			}
			l.removePlayerData(player, "already_given");
		});
	}
	
	@Override
	public boolean isInProcess(Player player, Mob mob)
	{
		CNFFTamable l = CNFFTamable.getCap(mob);
		return l.hasPlayerData(player, "already_given") && l.getPlayerDataInt(player, "already_given") > 0;
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
	
}

