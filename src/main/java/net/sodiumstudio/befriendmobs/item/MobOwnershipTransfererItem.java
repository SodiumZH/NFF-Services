package net.sodiumstudio.befriendmobs.item;

import java.util.UUID;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;

public class MobOwnershipTransfererItem extends Item
{
	
	public MobOwnershipTransfererItem(Properties pProperties)
	{
		super(pProperties.stacksTo(1));
	}
	
	public boolean isWritten(ItemStack stack)
	{
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MobOwnershipTransfererItem))
			throw new IllegalArgumentException();
		return stack.getOrCreateTag().contains("written");
	}
	
	public String getMobName(ItemStack stack)
	{
		if (!isWritten(stack))
			return null;
		else return stack.getTag().getString("mob_name");
	}
	
	public UUID getMobUUID(ItemStack stack)
	{
		if (!isWritten(stack))
			return null;
		else return stack.getTag().getUUID("mob_uuid");
	}
	
	public String getOldOwnerName(ItemStack stack)
	{
		if (!isWritten(stack))
			return null;
		else return stack.getTag().getString("owner_name");
	}
	
	public UUID getOldOwnerUUID(ItemStack stack)
	{
		if (!isWritten(stack))
			return null;
		else return stack.getTag().getUUID("owner_uuid");
	}
	
	public boolean isLocked(ItemStack stack)
	{
		if (!isWritten(stack))
			throw new IllegalStateException("MobOwnershipTransfererItem#isLocked: the item isn't written.");
		else return stack.getTag().getBoolean("locked");
	}
	
	protected void write(ItemStack stack, IBefriendedMob mob)
	{
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MobOwnershipTransfererItem))
			throw new IllegalArgumentException();
		if (!mob.isOwnerPresent())
			throw new IllegalStateException("MobOwnershipTransfererItem writing requires the owner to be present in the level.");
		stack.getOrCreateTag().putBoolean("written", true);
		stack.getTag().putUUID("mob_uuid", mob.asMob().getUUID());
		stack.getTag().putString("mob_name", mob.asMob().getName().getString());
		stack.getTag().putUUID("owner_uuid", mob.getOwnerUUID());
		stack.getTag().putString("owner_name", mob.getOwner().getName().getString());
		stack.getTag().putBoolean("locked", true);
	}
	
	/**
	 * Apply a written transferer to mob to transfer its owner
	 */
	protected InteractionResult tryTransfer(ItemStack stack, Player player, IBefriendedMob mob)
	{
		// Wrong stack type
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MobOwnershipTransfererItem))
			throw new IllegalArgumentException();
		// Unwritten
		if (!isWritten(stack))
			return InteractionResult.PASS;
		// Applying on another mob
		if (!stack.getOrCreateTag().getUUID("mob_uuid").equals(mob.asMob().getUUID()))
			return InteractionResult.PASS;
		// Player is the old owner
		if (stack.getOrCreateTag().getUUID("owner_uuid").equals(player.getUUID()))
			return InteractionResult.PASS;
		// Locked
		if (stack.getOrCreateTag().getBoolean("locked"))
			return InteractionResult.PASS;
		mob.setOwnerUUID(stack.getOrCreateTag().getUUID("owner_uuid"));
		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		// For written transferer
		if (isWritten(player.getItemInHand(usedHand)))
		{
			if (this.getOldOwnerUUID(player.getItemInHand(usedHand)).equals(player.getUUID()))			
			{
				// The old owner can lock/unlock the transferer
				player.getItemInHand(usedHand).getOrCreateTag().putBoolean("locked", !this.isLocked(player.getItemInHand(usedHand)));
				return InteractionResultHolder.success(player.getItemInHand(usedHand));
			}
			// The new owner can only apply the item on the corresponding mob
		}
		return InteractionResultHolder.pass(player.getItemInHand(usedHand));
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity living, InteractionHand usedHand)
	{
		if (living instanceof IBefriendedMob bm)
		{
			if (isWritten(stack))
			{
				if (tryTransfer(stack, player, bm) == InteractionResult.PASS)
					return InteractionResult.PASS;
				else
				{
					stack.shrink(1);
					return InteractionResult.sidedSuccess(player.level.isClientSide);
				}
			}
			else
			{
				if (bm.getOwnerUUID().equals(player.getUUID()))
				{
					write(stack, bm);
					return InteractionResult.sidedSuccess(player.level.isClientSide);
				}
			}
		}
		return InteractionResult.PASS;
	}

}
