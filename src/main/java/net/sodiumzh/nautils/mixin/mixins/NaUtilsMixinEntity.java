package net.sodiumzh.nautils.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.sodiumzh.nautils.mixin.events.entity.EntityLoadFailedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.CrashReport;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.NaUtilsMixinHooks;
import net.sodiumzh.nautils.mixin.event.entity.EntityFinalizeLoadingEvent;
import net.sodiumzh.nautils.mixin.event.entity.EntityLoadEvent;
import net.sodiumzh.nautils.mixin.event.entity.EntityTickEvent;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;

@Mixin(Entity.class)
public class NaUtilsMixinEntity implements NaUtilsMixin<Entity> {

	@Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (NaUtilsMixinHooks.onNonLivingEntityHurt(caller(), src, amount))
			callback.setReturnValue(false);
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	private void tick(CallbackInfo callback)
	{
		MinecraftForge.EVENT_BUS.post(new EntityTickEvent(caller()));
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

	@Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at =
			@At(value = "INVOKE", target = "net/minecraft/CrashReport.forThrowable(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/CrashReport;"),
	cancellable = true)
	private void loadFailed(CompoundTag pCompound, CallbackInfo ci,
							@Local(ordinal = 0) Throwable throwable, @Local(ordinal = 0) CompoundTag nbt)
	{
		EntityLoadFailedEvent event = new EntityLoadFailedEvent(caller(), throwable, nbt);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isShouldIgnore()) {
			MinecraftForge.EVENT_BUS.post(new EntityFinalizeLoadingEvent(caller(), nbt));
			ci.cancel();
		}
	}
}
