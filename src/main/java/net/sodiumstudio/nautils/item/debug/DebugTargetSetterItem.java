package net.sodiumstudio.nautils.item.debug;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.sodiumstudio.nautils.EntityHelper;
import net.sodiumstudio.nautils.InfoHelper;
import net.sodiumstudio.nautils.NaMiscUtils;
import net.sodiumstudio.nautils.NbtHelper;
import net.sodiumstudio.nautils.item.NaUtilsItem;

/**
 * NOT IMPLEMENTED!!!!
 */
//@Deprecated
public class DebugTargetSetterItem extends NaUtilsItem
{
	public DebugTargetSetterItem(Properties pProperties)
	{
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	
	@Nullable
	private Mob getAttacker(ItemStack stack, Level level)
	{
		if (level.isClientSide)
			return null;
		Mob attacker = null;
		if (stack.getOrCreateTag().hasUUID("attacker"))
		{
			Entity attackerEntity = EntityHelper.getEntityByUUID(level, stack.getTag().getUUID("attacker"));
			if (attackerEntity != null && attackerEntity instanceof Mob m)
				attacker = m;
		}
		return attacker;
	}
	
	private void setAttacker(ItemStack stack, @Nullable Mob attacker)
	{
		if (attacker == null)
			stack.getOrCreateTag().remove("attacker");
		else 
			stack.getOrCreateTag().putUUID("attacker", attacker.getUUID());
	}
	
	@Override
	public InteractionResult interactLivingEntity(Player player, LivingEntity target, InteractionHand hand) 
	{
		Mob attacker = getAttacker(player.getItemInHand(hand), player.level());
		// If targeting a mob attacking the user player, remove the target and reset
		if (player.level().isClientSide)
			return InteractionResult.SUCCESS;
		if (target instanceof Mob mob && mob.getTarget() == player)
		{
			attacker.setTarget(null);
			NaMiscUtils.printToScreen("Removed the target of [" + mob.getName().getString() + "].", player);
			if (attacker != null)
			{
				setAttacker(player.getItemInHand(hand), null);
				NaMiscUtils.printToScreen("Target Setter reset.", player);
			}
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		// If no attacker, set attacker first
		else if (attacker == null)
		{
			if (target instanceof Mob m)
			{
				setAttacker(player.getItemInHand(hand), m);
				NaMiscUtils.printToScreen("Setting the target of [" + m.getName().getString() + "]...", player);
				return InteractionResult.sidedSuccess(player.level().isClientSide);
			}
		} else
		{
			// If re-click the attack, remove the target
			if (target == attacker)
			{
				if (attacker.getTarget() != null)
				{
					attacker.setTarget(null);
					NaMiscUtils.printToScreen("Removed the target of [" + attacker.getName().getString() + "].", player);
					return InteractionResult.sidedSuccess(player.level().isClientSide);
				} else
				{
					NaMiscUtils.printToScreen("Setting the target of [" + attacker.getName().getString() + "]...", player);
					return InteractionResult.sidedSuccess(player.level().isClientSide);
				}
			}
			// Otherwise set target
			else
			{
				attacker.setTarget(target);
				NaMiscUtils.printToScreen(
						"Set the target of [" + attacker.getName().getString() + "] to [" + target.getName().getString() + "].",
						player);
				return InteractionResult.sidedSuccess(player.level().isClientSide);
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if (player.level().isClientSide) 
			return InteractionResultHolder.success(stack);
		Mob mob = getAttacker(stack, player.level());
		if (mob != null)
		{
			if (!player.isShiftKeyDown())
			{
				setAttacker(player.getItemInHand(hand), null);
				NaMiscUtils.printToScreen("Target Setter reset.", player);
				return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide);
			}
			else
			{
				mob.setTarget(player);
				NaMiscUtils.printToScreen("Set the target of [" + mob.getName().getString() + "] to self (" + player.getName().getString() + ")!", player);
				return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide);
			}
		}
		return InteractionResultHolder.pass(stack);
	}
}
