package net.sodiumzh.nautils.item.debug;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;

public class DebugAISwitchItem extends Item
{

	public DebugAISwitchItem(Properties pProperties)
	{
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) 
	{
		if (target instanceof Mob mob)
		{
			mob.setNoAi(!mob.isNoAi());
			// TODO: change the key to nautils after separating NaUtils out
			String key = mob.isNoAi() ? "info.nautils.debug_ai_switch_off" : "info.nautils.debug_ai_switch_on";	
			NaUtilsMiscStatics.printToScreen(NaUtilsInfoStatics.createTranslatable(key, target.getName().getString()), player);
			return InteractionResult.sidedSuccess(player.level.isClientSide);
		}
		else return InteractionResult.PASS;
	}

}
