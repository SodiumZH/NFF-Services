package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.entity.MobFinalizePickingUpItemEvent;
import net.sodiumstudio.nautils.events.entity.MobPickUpItemEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;
import net.sodiumstudio.nautils.mixins.NaUtilsMixinHooks;

@Mixin(Mob.class)
public class NaUtilsMixinMob implements NaUtilsMixin<Mob>
{
	@Inject(method = "isSunBurnTick()Z", at = @At("RETURN"), cancellable = true)
	private void isSunBurnTick(CallbackInfoReturnable<Boolean> callback)
	{
		if (callback.getReturnValueZ())
		{
			if (NaUtilsMixinHooks.onMobSunBurnTick(get()))
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
}
