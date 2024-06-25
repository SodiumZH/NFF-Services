package net.sodiumstudio.nautils.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class NaUtilsMixinMob implements NaUtilsMixin<Mob>
{
	@Inject(method = "isSunBurnTick()Z", at = @At("RETURN"), cancellable = true)
	private void isSunBurnTick(CallbackInfoReturnable<Boolean> callback)
	{
		if (callback.getReturnValueZ())
		{
			if (NaUtilsMixinHooks.onMobSunBurnTick(get()))
				callback.setReturnValue(false);
		}
	}
}
