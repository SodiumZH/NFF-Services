package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.item.GrindstoneAcceptItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$2")
public class NFUMixinGrindstoneMenuUpperSlot implements NFUMixin<Slot> {

    @Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z",
      at = @At("HEAD"), cancellable = true)
    private void acceptUpperSlot(ItemStack stackIn, CallbackInfoReturnable<Boolean> callback){
        var event = new GrindstoneAcceptItemEvent(stackIn, true);
        MinecraftForge.EVENT_BUS.post(event);
        switch (event.getResult()) {
            case ALLOW -> callback.setReturnValue(true);
            case DENY -> callback.setReturnValue(false);
            default -> {}
        }
    }
}
