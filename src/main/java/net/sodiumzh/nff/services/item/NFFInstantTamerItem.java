package net.sodiumzh.nff.services.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamableComponent;
import net.sodiumzh.nff.services.entity.taming.NFFTamableDataComponent;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nfu.exception.UnimplementedException;
import net.sodiumzh.nfu.item.NFUItem;
import net.sodiumzh.nfu.util.NFUDebugStatics;
import net.sodiumzh.nfu.util.NFUParticleStatics;

public class NFFInstantTamerItem extends NFUItem
{

	public NFFInstantTamerItem(Properties pProperties)
	{
		super(pProperties);
	}

	@Override
	@SuppressWarnings("unchecked")
	public InteractionResult interactLivingEntity(Player player, LivingEntity target, InteractionHand hand)
	{
		if (player.isCreative() && !player.level.isClientSide)
		{
			if (target instanceof INFFTamed bef)
			{
				bef.init(player.getUUID(), null);
				NFUDebugStatics.debugPrintToScreen("Mob " + target.getName().getString() + " initialized", player);
			}
			else 
			{
				NFFTamableComponent.getOptional(target).ifPresent((l) ->
				{
					Mob bef = NFFTamingMapping.getProcess((EntityType<Mob>)target.getType()).doTaming(player, l.getEntity());
					if (bef != null)
					{
						NFUParticleStatics.sendHeartParticlesToEntityDefault(bef);
						NFUDebugStatics.debugPrintToScreen("Mob " + target.getName().getString() + " befriended", player);
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
