package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.WorldCapabilityData;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.event.level.WorldCapabilityDataLoadEvent;

@Mixin(WorldCapabilityData.class)
public class NaUtilsMixinWorldCapabilityData implements NaUtilsMixin<WorldCapabilityData>
{
	@Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraftforge/common/util/INBTSerializable;)Lnet/minecraftforge/common/util/WorldCapabilityData;",
			at = @At("HEAD"), remap = false)
	private static void modifyData(CompoundTag nbt, INBTSerializable<CompoundTag> serializable, CallbackInfoReturnable<WorldCapabilityData> callback)
	{
		MinecraftForge.EVENT_BUS.post(new WorldCapabilityDataLoadEvent(nbt));
	}

}