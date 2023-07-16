package net.sodiumstudio.befriendmobs.item;

import java.util.function.Predicate;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemMobCatcher extends Item
{

	protected Predicate<Mob> canCatchCondition = null;
	protected ItemMobRespawner respawnerType;
	protected boolean respawnerNoExpire = true;
	protected boolean respawnerRecoverInVoid = true;
	protected boolean respawnerInvulnerable = true;
	
	public ItemMobCatcher(Properties pProperties, ItemMobRespawner respawnerType)
	{
		super(pProperties);
		this.respawnerType = respawnerType;
	}

	public ItemMobCatcher canCatchCondition(Predicate<Mob> condition)
	{
		this.canCatchCondition = condition;
		return this;
	}
	
	public ItemMobCatcher setRespawnerNoExpire(boolean value)
	{
		this.respawnerNoExpire = value;
		return this;
	}
	
	public ItemMobCatcher setRespawnerRecoverInVoid(boolean value)
	{
		this.respawnerRecoverInVoid = value;
		return this;
	}
	
	public ItemMobCatcher setRespawnerInvulnerable(boolean value)
	{
		this.respawnerInvulnerable = value;
		return this;
	}
	
	 @Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) 
	 {
		 if (!(interactionTarget instanceof Mob))
			 return InteractionResult.PASS;
		 Mob mob = (Mob)interactionTarget;
		 if (canCatchCondition == null || canCatchCondition.test(mob))
		 {
			 if (!player.level.isClientSide)
			 {
				 MobRespawnerInstance resp = MobRespawnerInstance.create(ItemMobRespawner.fromMob(respawnerType, mob));
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
					 stack.shrink(1);
					 return InteractionResult.sidedSuccess(player.level.isClientSide);
				 }
			 }
		 }
		 return InteractionResult.PASS;
	 }
	
}
