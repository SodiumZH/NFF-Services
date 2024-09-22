package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.NaUtilsMixinHooks;
import net.sodiumzh.nautils.mixin.events.entity.EntityFinalizeLoadingEvent;
import net.sodiumzh.nautils.mixin.events.entity.EntityLoadEvent;
import net.sodiumzh.nautils.mixin.events.entity.EntityTickEvent;

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
