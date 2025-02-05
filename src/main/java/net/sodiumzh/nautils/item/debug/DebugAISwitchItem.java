package net.sodiumzh.nautils.item.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.item.NaUtilsItem;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;

public class DebugAISwitchItem extends NaUtilsItem
{

	public DebugAISwitchItem(Properties pProperties)
	{
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public InteractionResult interactLivingEntity(Player player, LivingEntity target, InteractionHand hand)
	{
		if (!player.level().isClientSide && target instanceof Mob mob)
		{
			mob.setNoAi(!mob.isNoAi());
			String key = mob.isNoAi() ? "info.nautils.item.debug_ai_switch_off" : "info.nautils.item.debug_ai_switch_on";
			MutableComponent info = Component.translatable(key, target.getName().getString());
			NaUtilsInfoStatics.printMessage(player, info);
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		else return InteractionResult.PASS;
	}

}
