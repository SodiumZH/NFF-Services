package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.sodiumzh.nautils.entity.vanillatrade.CVanillaMerchant;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.statics.NaUtilsReflectionStatics;

@Mixin(MerchantMenu.class)
public class NaUtilsMerchantMenuMixin implements NaUtilsMixin<MerchantMenu>
{
	/**
	 * In this method, {@code Merchant} is directly casted to {@code Entity} which will cause error on non-entity Merchant implementations,
	 * so perform an extra type check here.
	 */
	@WrapOperation(method = "playTradeSound()V",
			at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/world/item/trading/Merchant;isClientSide()Z"))
	private boolean typeCheck(Merchant caller, Operation<Boolean> original)
	{
		return original.call(caller) || !(caller instanceof Entity);
	}
	
	@Inject(method = "playTradeSound()V",
			at = @At("HEAD"), cancellable = true)
	private void handleCapability(CallbackInfo callback)
	{
		Merchant trader = NaUtilsReflectionStatics.forceGet(caller(), MerchantMenu.class, "f_40027_").cast();		// MerchantMenu#trader
		if (trader instanceof CVanillaMerchant cap && !cap.isClientSide())
		{
			cap.playTradeSound();
			callback.cancel();
		}
	}
}
