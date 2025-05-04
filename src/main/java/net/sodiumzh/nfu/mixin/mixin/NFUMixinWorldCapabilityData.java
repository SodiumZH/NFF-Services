package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.WorldCapabilityData;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.level.WorldCapabilityDataLoadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCapabilityData.class)
public class NFUMixinWorldCapabilityData implements NFUMixin<WorldCapabilityData>
{
	@Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraftforge/common/util/INBTSerializable;)Lnet/minecraftforge/common/util/WorldCapabilityData;",
			at = @At("HEAD"), remap = false)
	private static void modifyData(CompoundTag nbt, INBTSerializable<CompoundTag> serializable, CallbackInfoReturnable<WorldCapabilityData> callback)
	{
		MinecraftForge.EVENT_BUS.post(new WorldCapabilityDataLoadEvent(nbt));
	}

}