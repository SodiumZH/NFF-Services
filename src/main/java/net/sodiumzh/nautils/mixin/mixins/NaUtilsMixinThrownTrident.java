package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.event.entity.ThrownTridentSetBaseDamageEvent;
import net.sodiumzh.nautils.mixin.event.entity.ThrownTridentSetFinalDamageEvent;

@Mixin(ThrownTrident.class)
public class NaUtilsMixinThrownTrident implements NaUtilsMixin<ThrownTrident>
{

	@ModifyVariable(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", 
			at = @At("STORE"), ordinal = 0)
	private float modifyInitialDamage(float original)
	{
		var event = new ThrownTridentSetBaseDamageEvent(caller(), original);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getDamage();
	}
	
	@ModifyVariable(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
			shift = At.Shift.BEFORE),
			ordinal = 0)
	private float modifyFinalDamage(float original)
	{
		var event = new ThrownTridentSetFinalDamageEvent(caller(), original);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getDamage();
	}
}
