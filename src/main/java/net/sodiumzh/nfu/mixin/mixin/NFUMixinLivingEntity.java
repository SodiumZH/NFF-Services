package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.sodiumzh.nfu.mixin.event.entity.LivingEntityDamageTakenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.entity.LivingStartDeathEvent;
import net.sodiumzh.nfu.mixin.event.entity.LootCheckPlayerKillEvent;

@Mixin(LivingEntity.class)
public abstract class NFUMixinLivingEntity implements NFUMixin<LivingEntity>
{
	@Shadow public abstract void indicateDamage(double p_270514_, double p_270826_);

	@Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
			at = @At(value = "INVOKE",
				target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"))
	private void startDie(DamageSource dmgSource, CallbackInfo callback)
	{
		MinecraftForge.EVENT_BUS.post(new LivingStartDeathEvent(caller(), dmgSource));
	}
	
	@ModifyVariable(method = "dropAllDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;)V",
			at = @At("STORE"), ordinal = 0)
	private boolean canDropPlayerKill(boolean original, @Local(ordinal = 0, argsOnly = true) DamageSource dmg)
	{
		var event = new LootCheckPlayerKillEvent(this.caller(), dmg, original);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Event.Result.DEFAULT) return original;
		else if (event.getResult() == Event.Result.ALLOW) return true;
		else if (event.getResult() == Event.Result.DENY) return false;
		else throw new RuntimeException();
	}

	@WrapOperation(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
	at = @At(value = "INVOKE", target = "net/minecraft/world/entity/LivingEntity.setAbsorptionAmount (F)V"))
	private void postDamageTakenEvent(LivingEntity instance, float pAbsorptionAmount, Operation<Void> original,
		@Local(argsOnly = true) DamageSource damageSource, @Local(ordinal = 1) float amount) {
		original.call(instance, pAbsorptionAmount);
		if (amount > 0f)
			MinecraftForge.EVENT_BUS.post(new LivingEntityDamageTakenEvent(instance, amount, damageSource));
	}
}
