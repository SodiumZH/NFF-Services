package net.sodiumstudio.nautils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.ItemEntityHurtEvent;

@Mixin(ItemEntity.class)
public class NaUtilsMixinItemEntity implements NaUtilsMixin<ItemEntity> {
	
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/damagesource/DamageSource;F", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (get().level.isClientSide || get().isRemoved()) //Forge: Fixes MC-53850
		{
			callback.setReturnValue(false);
		}
		else if (MinecraftForge.EVENT_BUS.post(new ItemEntityHurtEvent(get(), src, amount)))
		{
			callback.setReturnValue(false);
		}
	}
	
}
