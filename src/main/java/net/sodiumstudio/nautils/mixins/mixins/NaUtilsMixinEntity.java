package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.NonLivingEntityHurtEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(Entity.class)
public class NaUtilsMixinEntity implements NaUtilsMixin<Entity> {
	
	@Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (!get().level().isClientSide
				&& !(get() instanceof LivingEntity)
				&& !(get() instanceof ItemEntity)
				&& MinecraftForge.EVENT_BUS.post(new NonLivingEntityHurtEvent(get(), src, amount)))
		{
			callback.setReturnValue(false);
		}
	}
	
}
