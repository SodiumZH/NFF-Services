package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.item.INFUItem;
import net.sodiumzh.nfu.mixin.NFUMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class NFUMixinServerPlayerGameMode implements NFUMixin<ServerPlayerGameMode> {

    // Implement INaUtilsItem#shouldConsumeInCreative feature
    @ModifyExpressionValue(method = "useItem(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
    at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayerGameMode.isCreative()Z"))
    private boolean setForceConsumeOnUse(boolean original, @Local(ordinal = 1) ItemStack usingItemStack) {
        if (usingItemStack.getItem() instanceof INFUItem utilsItem && utilsItem.shouldConsumeInCreative())
            return false;
        return original;
    }

    // Implement INaUtilsItem#shouldConsumeInCreative feature
    @ModifyExpressionValue(method = "useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayerGameMode.isCreative()Z"))
    private boolean setForceConsumeOnUseOn(boolean original, @Local(argsOnly = true) ItemStack usingItemStack) {
        if (usingItemStack.getItem() instanceof INFUItem utilsItem && utilsItem.shouldConsumeInCreative())
            return false;
        return original;
    }

}
