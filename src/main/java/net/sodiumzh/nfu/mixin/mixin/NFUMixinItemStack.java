package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirectorEventListeners;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class NFUMixinItemStack implements NFUMixin<ItemStack>
{
	/**
	 * As ItemStack loading is too frequent, don't post event here to prevent potential resource waste, but just handle porting
	 */
	@Inject(method = "of(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
	private static void modifyItemStackNBT(CompoundTag nbt, CallbackInfoReturnable<ItemStack> callback)
	{
		SaveDataLocationRedirectorEventListeners.doPortItems(nbt);
	}
	
}
