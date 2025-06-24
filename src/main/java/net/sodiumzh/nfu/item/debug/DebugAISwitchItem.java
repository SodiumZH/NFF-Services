package net.sodiumzh.nfu.item.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.item.NFUItem;
import net.sodiumzh.nfu.util.NFUInfoStatics;

public class DebugAISwitchItem extends NFUItem
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
			String key = mob.isNoAi() ? "info.nfulib.item.debug_ai_switch_off" : "info.nfulib.item.debug_ai_switch_on";
			MutableComponent info = Component.translatable(key, target.getName().getString());
			NFUInfoStatics.printMessage(player, info);
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		else return InteractionResult.PASS;
	}

}
