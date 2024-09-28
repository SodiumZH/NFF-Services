package net.sodiumzh.nff.services.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nautils.item.NaUtilsItem;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFMobRespawnerItem extends NaUtilsItem
{

	protected boolean retainBefriendedMobInventory = true;
	
	public NFFMobRespawnerItem(Properties pProperties)
	{
		super(pProperties.stacksTo(1));
	}

	public NFFMobRespawnerItem setRetainBefriendedMobInventory(boolean value)
	{
		this.retainBefriendedMobInventory = value;
		return this;
	}
	
	public static ItemStack fromMob(NFFMobRespawnerItem itemType, Mob mob) {
		if (mob.level.isClientSide)
			return ItemStack.EMPTY;
		NFFMobRespawnerInstance ins = NFFMobRespawnerInstance.create(new ItemStack(itemType, 1));
		ins.initFromMob(mob);

		// stack.setHoverName(NaUtilsInfoStatics.createTrans(stack.getHoverName().getString() +
		// " - " + mob.getName().getString()));
		// Check NBT correctly added
		if (ins.getCapTag().isEmpty())
			throw new IllegalStateException("Respawner missing NBT");

		return ins.get();
	}
/*
	public static ItemStack fromMob(Mob mob) {
		return fromMob((NFFMobRespawnerItem) NFFItemRegistry.MOB_RESPAWNER.get(), mob);
	}
*/
	public static Mob doRespawn(ItemStack stack, Player player, BlockPos pos, Direction direction) {
		NFFMobRespawnerInstance ins = NFFMobRespawnerInstance.create(stack);
		// Check NBT correctly added
		if (ins.getCapTag().isEmpty())
			throw new IllegalStateException("Respawner missing NBT");
		return ins.respawn(player, pos, direction);
	}

	@SuppressWarnings("resource")
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel))
		{
			return InteractionResult.SUCCESS;
		} else
		{
			Mob mob = doRespawn(context.getItemInHand(), context.getPlayer(), context.getClickedPos(),
					context.getClickedFace());
			if (mob != null)
			{

				context.getItemInHand().shrink(1);
				if (mob instanceof INFFTamed bef)
				{
					bef.init(bef.getOwnerUUID(), null);
					if (!retainBefriendedMobInventory)
					{
						if (!bef.getAdditionalInventory().isEmpty())
							bef.getAdditionalInventory().clearContent();
						bef.updateFromInventory();
						NaUtilsEntityStatics.removeAllEquipment(bef.asMob());
					}
					bef.setInit();
				}
				return InteractionResult.CONSUME;
			} else
				return InteractionResult.PASS;
		}
	}

}
