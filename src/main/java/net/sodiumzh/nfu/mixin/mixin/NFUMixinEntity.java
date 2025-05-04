package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.NFUMixinHooks;
import net.sodiumzh.nfu.mixin.event.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class NFUMixinEntity implements NFUMixin<Entity> {

	@Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> callback)
	{
		if (NFUMixinHooks.onNonLivingEntityHurt(caller(), src, amount))
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
							@Local(ordinal = 0) Throwable throwable,
							@Local(ordinal = 0, argsOnly = true) CompoundTag nbt)
	{
		EntityLoadFailedEvent event = new EntityLoadFailedEvent(caller(), throwable, nbt);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isShouldIgnore()) {
			MinecraftForge.EVENT_BUS.post(new EntityFinalizeLoadingEvent(caller(), nbt));
			ci.cancel();
		}
	}

	@Inject(method = "discard()V", at = @At("HEAD"))
	private void onDiscard(CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new EntityDiscardEvent(caller()));
	}
}
