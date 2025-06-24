package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.sodiumzh.nfu.item.INFUItem;
import net.sodiumzh.nfu.mixin.event.entity.LivingEntityDamageTakenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.entity.LivingEntitySweepHurtEvent;

@Mixin(Player.class)
public abstract class NFUMixinPlayer implements NFUMixin<Player>
{

	@Shadow public abstract InteractionResult interactOn(Entity pEntityToInteractOn, InteractionHand pHand);

	@Shadow public abstract void increaseScore(int pScore);

	// Last condition is "this.distanceToSqr(livingentity) < entityReachSq", so make it false if cancelled
	@WrapOperation(method = "attack(Lnet/minecraft/world/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"
					)
			)
	private double acceptSweepDamage(Player caller, Entity entity, Operation<Double> original)
	{
		if (entity instanceof LivingEntity living
				&& original.call(caller, entity) < Mth.square(caller.getEntityReach())
				&& MinecraftForge.EVENT_BUS.post(new LivingEntitySweepHurtEvent(living, this.caller())))
			return Double.MAX_VALUE;
		else return original.call(caller, entity);
	}

	// INaUtilsItem usage skipping features

	@WrapOperation(method = "interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			at = @At(value = "INVOKE",
					target = "net/minecraftforge/common/ForgeHooks.onInteractEntity(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			remap = false))
	private InteractionResult cancelEntityInteractEvent(Player player, Entity entity, InteractionHand hand, Operation<InteractionResult> original){
		if (player.getItemInHand(hand).getItem() instanceof INFUItem item
				&& item.shouldSkipEntityInteract(player, entity, hand))
			return null;
		return original.call(player, entity, hand);
	}

	@WrapOperation(method = "interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult cancelEntityInteract(Entity instance, Player player, InteractionHand hand, Operation<InteractionResult> original){
		if (player.getItemInHand(hand).getItem() instanceof INFUItem item
			&& item.shouldSkipEntityInteract(player, instance, hand))
			return InteractionResult.PASS;
		return original.call(instance, player, hand);
	}
/*
	@WrapOperation(method = "interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			at = @At(value = "INVOKE",
					target = "net/minecraft/world/item/ItemStack.interactLivingEntity (Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult cancelItemInteractLiving(ItemStack instance, Player player, LivingEntity entity, InteractionHand hand, Operation<InteractionResult> original){
		if (player.getItemInHand(hand).getItem() instanceof INaUtilsItem item
				&& item.shouldSkipUsagePhase(INaUtilsItem.UsagePhase.ITEM_X_INTERACTION_LIVING, INaUtilsItem.UsageContext.forInteractEntity(player, hand, entity)))
			return InteractionResult.PASS;
		return original.call(instance, player, entity, hand);
	}
*/
	@WrapOperation(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
	at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Player.setHealth (F)V"))
	private void postDamageTakenEvent(Player instance, float v, Operation<Void> original,
		@Local(argsOnly = true) DamageSource damageSource, @Local(ordinal = 1) float amount) {
		original.call(instance, v);
		if (amount > 0)
			MinecraftForge.EVENT_BUS.post(new LivingEntityDamageTakenEvent(instance, amount, damageSource));
	}






}
