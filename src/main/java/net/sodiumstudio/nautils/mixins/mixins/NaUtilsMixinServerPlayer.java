package net.sodiumstudio.nautils.mixins.mixins;

import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.entity.MonsterPreventSleepEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(ServerPlayer.class)
public class NaUtilsMixinServerPlayer implements NaUtilsMixin<ServerPlayer>
{
	@WrapOperation(method = "startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;",
			at = @At(value = "INVOKE", target = "java/util/List.isEmpty()Z"))
	private boolean hasSleepPreventingMobs(List<Monster> list, Operation<Boolean> original)
	{	
		// Not empty = should prevent sleep
		// If it's originally empty, do nothing
		if (original.call(list)) return true;
		// Otherwise post event to each mob
		MutableObject<Integer> cancelled = new MutableObject<>(0);
		list.forEach(m -> {
			if (MinecraftForge.EVENT_BUS.post(new MonsterPreventSleepEvent(m, caller())))
			{
				cancelled.setValue(cancelled.getValue() + 1);
			}
		});
		// If all mob are cancelled, don't prevent sleep i.e. regard as empty
		return cancelled.getValue() == list.size();
	}
}
