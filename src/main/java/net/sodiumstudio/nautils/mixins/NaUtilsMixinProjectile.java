package net.sodiumstudio.nautils.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.ProjectileHitEvent;

@Mixin(Projectile.class)
public class NaUtilsMixinProjectile implements NaUtilsMixin<Projectile>
{

	@Inject(method = "onHit(Lnet/minecraft/world/phys/HitResult;)V", at = @At("HEAD"), cancellable = true)
	private void onHit(HitResult hitResult, CallbackInfo callback)
	{
		if (hitResult.getType() != HitResult.Type.MISS && MinecraftForge.EVENT_BUS.post(new ProjectileHitEvent(get(), hitResult)))
		{
			callback.cancel();
		}
	}
	
}
