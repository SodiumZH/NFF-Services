package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.entity.EntityFinalizeLoadingEvent;
import net.sodiumstudio.nautils.events.entity.EntityLoadEvent;
import net.sodiumstudio.nautils.events.entity.EntityTickEvent;
import net.sodiumstudio.nautils.events.entity.NonLivingEntityHurtEvent;
import net.sodiumstudio.nautils.events.entity.NonLivingEntityOutOfWorldEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;
import net.sodiumstudio.nautils.mixins.NaUtilsMixinHooks;

@Mixin(Entity.class)
public class NaUtilsMixinEntity implements NaUtilsMixin<Entity> {
	
	@Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (NaUtilsMixinHooks.onNonLivingEntityHurt(caller(), src, amount))
			callback.setReturnValue(false);
	}
	
	@Inject(at = @At("HEAD"), method = "tick()V", cancellable = true)
	private void tick(CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new EntityTickEvent(caller())))
			callback.cancel();
	}
	
	@Inject(at = @At("HEAD"), method = "load(Lnet/minecraft/nbt/CompoundTag;)V")
	private void beforeLoad(CompoundTag nbt, CallbackInfo callback)
	{
		MinecraftForge.EVENT_BUS.post(new EntityLoadEvent(caller(), nbt));
	}
	
	@Inject(at = @At("TAIL"), method = "load(Lnet/minecraft/nbt/CompoundTag;)V")
	private void afterLoad(CompoundTag nbt, CallbackInfo callback)
	{
		MinecraftForge.EVENT_BUS.post(new EntityFinalizeLoadingEvent(caller(), nbt));
	}
}
