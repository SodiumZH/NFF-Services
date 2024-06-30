package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.entity.LivingEntitySweepHurtEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(Player.class)
public class NaUtilsMixinPlayer implements NaUtilsMixin<Player>
{

	// Last condition is "this.distanceToSqr(livingentity) < entityReachSq", so make it false if cancelled
	@WrapOperation(method = "attack(Lnet/minecraft/world/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"
					)
			)
	private double acceptSweepDamage(Player caller, Entity entity, Operation<Double> original)
	{
		if (entity instanceof LivingEntity living
				&& original.call(caller, entity) < Mth.square(caller.getEntityReach())
				&& MinecraftForge.EVENT_BUS.post(new LivingEntitySweepHurtEvent(living, this.get())))
			return Double.MAX_VALUE;
		else return original.call(caller, entity);
	}
}
