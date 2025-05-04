package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirectorEventListeners;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityType.class)
public class NFUMixinEntityType implements NFUMixin<EntityType<?>>
{

	@Inject(method = "create(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
			at = @At("HEAD"))
	private static void startCreate(CompoundTag nbt, Level level, CallbackInfoReturnable<Optional<Entity>> callback)
	{
		SaveDataLocationRedirectorEventListeners.doPortEntityTypes(nbt);
	}
}
