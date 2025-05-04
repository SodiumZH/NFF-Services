package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.entity.ProjectileHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class NFUMixinProjectile implements NFUMixin<Projectile>
{

	@Inject(method = "onHit(Lnet/minecraft/world/phys/HitResult;)V", at = @At("HEAD"), cancellable = true)
	private void onHit(HitResult hitResult, CallbackInfo callback)
	{
		if (hitResult.getType() != HitResult.Type.MISS && MinecraftForge.EVENT_BUS.post(new ProjectileHitEvent(caller(), hitResult)))
		{
			callback.cancel();
		}
	}
	
}
