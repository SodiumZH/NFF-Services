package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LevelCapabilityData;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.events.level.LevelCapabilityDataLoadEvent;

@Mixin(LevelCapabilityData.class)
public class NaUtilsMixinLevelCapabilityData implements NaUtilsMixin<LevelCapabilityData>
{
	@Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraftforge/common/util/INBTSerializable;)Lnet/minecraftforge/common/util/LevelCapabilityData;",
			at = @At("HEAD"), remap = false)
	private static void modifyData(CompoundTag nbt, INBTSerializable<CompoundTag> serializable, CallbackInfoReturnable<LevelCapabilityData> callback)
	{
		MinecraftForge.EVENT_BUS.post(new LevelCapabilityDataLoadEvent(nbt));
	}

}