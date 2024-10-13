package net.sodiumzh.nff.services.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.exceptions.UnimplementedException;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsParticleStatics;
import net.sodiumzh.nautils.exceptions.UnimplementedException;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class NFFInstantTamerItem extends Item
{

	public NFFInstantTamerItem(Properties pProperties)
	{
		super(pProperties);
	}

	@Override
	@SuppressWarnings("unchecked")
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) 
	{
		if (player.isCreative() && !player.level.isClientSide)
		{
			if (target instanceof INFFTamed bef)
			{
				bef.init(player.getUUID(), null);
				NaUtilsDebugStatics.debugPrintToScreen("Mob " + target.getName().getString() + " initialized", player);
			}
			else 
			{
				target.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
				{
					INFFTamed bef = NFFTamingMapping.getHandler((EntityType<Mob>)target.getType()).doTaming(player, l.getOwner());
					if (bef != null)
					{
						NaUtilsParticleStatics.sendHeartParticlesToEntityDefault(bef.asMob());
						NaUtilsDebugStatics.debugPrintToScreen("Mob " + target.getName().getString() + " befriended", player);
					} else
						throw new UnimplementedException(
								"Entity type befriend method unimplemented: " + target.getType().toShortString()
								+ ", handler class: " + NFFTamingMapping.getHandler((EntityType<Mob>)target.getType()).toString());
	
				});
			}
			return InteractionResult.sidedSuccess(player.level.isClientSide);
		}
		else return InteractionResult.PASS;
	}
}
