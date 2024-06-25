package net.sodiumstudio.nautils.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.nautils.events.LivingStartDeathEvent;
import net.sodiumstudio.nautils.events.LootCheckPlayerKillEvent;

@Mixin(LivingEntity.class)
public class NaUtilsMixinLivingEntity implements NaUtilsMixin<LivingEntity>
{
	@WrapOperation(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
			at = @At(value = "INVOKE",
				target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;",
				shift = At.Shift.BEFORE))
	private Entity startDie(DamageSource source, Operation<Entity> original)
	{
		MinecraftForge.EVENT_BUS.post(new LivingStartDeathEvent(get(), source));
		return original.call(source);
	}
	
	@ModifyVariable(method = "dropAllDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;)V",
			at = @At("STORE"), ordinal = 0)
	private boolean canDropPlayerKill(boolean original, @Local(ordinal = 0) DamageSource dmg)
	{
		var event = new LootCheckPlayerKillEvent(this.get(), dmg, original);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Event.Result.DEFAULT) return original;
		else if (event.getResult() == Event.Result.ALLOW) return true;
		else if (event.getResult() == Event.Result.DENY) return false;
		else throw new RuntimeException();
	}
}
