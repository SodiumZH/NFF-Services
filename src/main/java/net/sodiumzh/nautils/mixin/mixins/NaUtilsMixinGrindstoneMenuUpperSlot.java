package net.sodiumzh.nautils.mixin.mixins;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.events.item.GrindstoneAcceptItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$2")
public class NaUtilsMixinGrindstoneMenuUpperSlot implements NaUtilsMixin<Slot> {

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
