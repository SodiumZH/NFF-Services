package net.sodiumzh.nff.services.item;

import java.util.function.BiPredicate;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.item.NaUtilsItem;
import net.sodiumzh.nff.services.item.event.MobCatcherCatchMobEvent;

public class MobCatcherItem extends NaUtilsItem
{
	
	protected BiPredicate<Mob, Player> canCatchCondition = null;
	protected NFFMobRespawnerItem respawnerType;
	protected boolean respawnerNoExpire = true;
	protected boolean respawnerRecoverInVoid = true;
	protected boolean respawnerInvulnerable = true;
	
	public MobCatcherItem(Properties pProperties, NFFMobRespawnerItem respawnerType)
	{
		super(pProperties);
		this.respawnerType = respawnerType;
	}

	public MobCatcherItem canCatchCondition(BiPredicate<Mob, Player> condition)
	{
		this.canCatchCondition = condition;
		return this;
	}
	
	public MobCatcherItem setRespawnerNoExpire(boolean value)
	{
		this.respawnerNoExpire = value;
		return this;
	}
	
	public MobCatcherItem setRespawnerRecoverInVoid(boolean value)
	{
		this.respawnerRecoverInVoid = value;
		return this;
	}
	
	public MobCatcherItem setRespawnerInvulnerable(boolean value)
	{
		this.respawnerInvulnerable = value;
		return this;
	}
	
	@Override
	public InteractionResult interactLivingEntity(Player player, LivingEntity interactionTarget, InteractionHand usedHand) 
	 {
		 if (!(interactionTarget instanceof Mob))
			 return InteractionResult.PASS;
		 Mob mob = (Mob)interactionTarget;
		 if (canCatchCondition == null || canCatchCondition.test(mob, player))
		 {
			 if (!player.level.isClientSide)
			 {
				 NFFMobRespawnerInstance resp = NFFMobRespawnerInstance.create(NFFMobRespawnerItem.fromMob(respawnerType, mob));
				 MobCatcherCatchMobEvent event = new MobCatcherCatchMobEvent(mob, player, resp);
				 if (MinecraftForge.EVENT_BUS.post(event))
					 return InteractionResult.PASS;
				 resp = event.respawnerAfterCatching;
				 if (resp != null && resp.get() != null && !resp.get().isEmpty())
				 {
					 resp.setNoExpire(respawnerNoExpire);
					 resp.setRecoverInVoid(respawnerRecoverInVoid);
					 resp.setInvulnerable(respawnerInvulnerable);
					 if (!player.addItem(resp.get()))
					 {
						 player.spawnAtLocation(resp.get());
					 }
					 interactionTarget.discard();
					 player.getItemInHand(usedHand).shrink(1);
					 return InteractionResult.sidedSuccess(player.level.isClientSide);
				 }
			 }
		 }
		 return InteractionResult.PASS;
	 }
	
}
