package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.LivingEntitySweepHurtEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(Player.class)
public class NaUtilsMixinPlayer implements NaUtilsMixin<Player>
{
	@ModifyExpressionValue(method = "attack(Lnet/minecraft/world/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/common/extensions/IForgePlayer;canHit(Lnet/minecraft/world/entity/Entity;D)Z"
					)
			)
	// This is the last condition of sweeping, so canceling this means canceling sweeping
	private boolean shouldAcceptSweep(boolean original)
	{
		// If originally true, all sweeping conditions are satisfied, so post event and check if cancelled
		if (original)
			return !MinecraftForge.EVENT_BUS.post(new LivingEntitySweepHurtEvent(this.get()));
		// If originally false, it shouldn't take sweep hurt at all, so no posting and return false
		else return original;
	}
}
