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

public class DebugTargetSetterItem extends Item
{
	public DebugTargetSetterItem(Properties pProperties)
	{
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	
	@Nullable
	private Mob getAttacker(ItemStack stack, Level level)
	{
		stack.getOrCreateTag();
		if (level.isClientSide)
			return null;
		Mob attacker = null;
		if (stack.getTag().hasUUID("attacker"))
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
			stack.getTag().putUUID("attacker", attacker.getUUID());
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) 
	{
		stack.getOrCreateTag();
		if (!player.level.isClientSide)
		{
			Mob attacker = getAttacker(stack, player.level);
			// If targeting a mob attacking the user player, remove the target and reset item
			if (target instanceof Mob mob && mob.getTarget() == player)
			{
				attacker.setTarget(null);
				NaMiscUtils.printToScreen("Removed the target of " + mob.getName(), player);
				if (attacker != null)
				{
					setAttacker(stack, null);
					NaMiscUtils.printToScreen("Target Setter reset", player);
				}
				return InteractionResult.sidedSuccess(player.level.isClientSide);
			}
			// If no attacker, set attacker first
			else if (attacker == null) 
			{
				if (target instanceof Mob m)
				{
					setAttacker(stack, m);
					NaMiscUtils.printToScreen("Set attacker to " + m.getName(), player);
					return InteractionResult.sidedSuccess(player.level.isClientSide);
				}
			}
			else
			{
				// If re-click the attack, remove the target
				if (target == attacker)
				{
					if (attacker.getTarget() != null)
					{
						attacker.setTarget(null);
						NaMiscUtils.printToScreen("Removed the target of " + attacker.getName(), player);
						return InteractionResult.sidedSuccess(player.level.isClientSide);
					}
					else return InteractionResult.PASS;
				}
				// Otherwise set target and reset the item
				else
				{
					attacker.setTarget(target);
					NaMiscUtils.printToScreen("Set the target of " + attacker.getName() + " to " + target.getName(), player);
					setAttacker(stack, null);
					return InteractionResult.sidedSuccess(player.level.isClientSide);
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack= player.getItemInHand(hand);
		Mob mob = getAttacker(stack, player.level);
		if (mob != null)
		{
			if (!player.isShiftKeyDown())
			{
				setAttacker(stack, null);
				NaMiscUtils.printToScreen("Target Setter reset", player);
				return InteractionResultHolder.sidedSuccess(stack, player.level.isClientSide);
			}
			else
			{
				mob.setTarget(player);
				NaMiscUtils.printToScreen("Set the target of " + mob.getName() + " to self (" + player.getName() + ")!", player);
				return InteractionResultHolder.sidedSuccess(stack, player.level.isClientSide);
			}
		}
		return InteractionResultHolder.pass(stack);
			
		
		
	}
}
