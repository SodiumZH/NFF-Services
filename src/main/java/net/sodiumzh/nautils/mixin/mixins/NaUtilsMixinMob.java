package net.sodiumzh.nautils.mixin.mixins;

import net.sodiumzh.nautils.mixin.events.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.NaUtilsMixinHooks;

@Mixin(Mob.class)
public class NaUtilsMixinMob implements NaUtilsMixin<Mob>
{
	@WrapOperation(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
	at = @At(value = "INVOKE", target = "net/minecraft/world/entity/Mob.registerGoals()V"))
	private void onRegisterGoals(Mob caller, Operation<Void> original)
	{
		original.call(caller);
		MinecraftForge.EVENT_BUS.post(new MobRegisterGoalsEvent(caller));
	}

	@Inject(method = "isSunBurnTick()Z", at = @At("RETURN"), cancellable = true)
	private void isSunBurnTick(CallbackInfoReturnable<Boolean> callback)
	{
		if (callback.getReturnValueZ())
		{
			if (NaUtilsMixinHooks.onMobSunBurnTick(caller()))
				callback.setReturnValue(false);
		}
	}
	
	@WrapOperation(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
	private void onPickUpItem(Mob caller, ItemEntity target, Operation<Void> original)
	{
		if (!MinecraftForge.EVENT_BUS.post(new MobPickUpItemEvent(caller, target)))
		{
			original.call(caller, target);
			MinecraftForge.EVENT_BUS.post(new MobFinalizePickingUpItemEvent(caller, target.getItem().copy()));
		}
	}
	
	@WrapOperation(method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			at = @At(value = "INVOKE",
			target = "net/minecraft/world/entity/Mob.mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult onMobInteract(Mob caller, Player player, InteractionHand hand, Operation<InteractionResult> original)
	{
		MobInteractEvent event = new MobInteractEvent(caller, player, hand);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getInteractionResult().consumesAction())
			return event.getInteractionResult();
		else return original.call(caller, player, hand);
	}
	
	@Inject(method = "checkDespawn()V", at = @At("HEAD"), cancellable = true)
	private void beforeCheckingDespawn(CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new MobCheckDespawnEvent(this.caller())))
		{
			callback.cancel();
			caller().setNoActionTime(0);	// This action is done if despawn is cancelled in vanilla or forge ways
		}
			
	}
}
