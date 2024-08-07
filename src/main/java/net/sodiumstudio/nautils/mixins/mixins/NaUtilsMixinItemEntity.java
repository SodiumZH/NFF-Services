package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.entity.ItemEntityHurtEvent;
import net.sodiumstudio.nautils.events.entity.ItemEntityOutOfWorldEvent;
import net.sodiumstudio.nautils.events.entity.NonLivingEntityHurtEvent;
import net.sodiumstudio.nautils.events.entity.NonLivingEntityOutOfWorldEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(ItemEntity.class)
public class NaUtilsMixinItemEntity implements NaUtilsMixin<ItemEntity> {
	@Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (caller().level.isClientSide || caller().isRemoved()) //Forge: Fixes MC-53850
		{
			callback.setReturnValue(false);
		}
		else
		{
			if (src == DamageSource.OUT_OF_WORLD && amount != Integer.MAX_VALUE)
			{
				if (MinecraftForge.EVENT_BUS.post(new ItemEntityOutOfWorldEvent(caller(), amount)))
					callback.setReturnValue(false);
			}
			else
			{
				if (MinecraftForge.EVENT_BUS.post(new ItemEntityHurtEvent(caller(), src, amount)))
					callback.setReturnValue(false);
			}
		}
	}
}
