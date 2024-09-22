package net.sodiumzh.nautils.mixin.mixins;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.event.entity.MonsterPreventSleepEvent;

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
