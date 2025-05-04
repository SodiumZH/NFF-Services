package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.item.BlockItemConsumeOnPlacedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class NFUBlockItemMixin implements NFUMixin<BlockItem> {

    @WrapOperation(method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.shrink (I)V"))
    private void postBlockItemConsumeEvent(ItemStack instance, int pDecrement, Operation<Void> original, @Local BlockPos pos,
          @Local(ordinal = 1) BlockState blockstate, @Local Player player)
    {
        if (!MinecraftForge.EVENT_BUS.post(new BlockItemConsumeOnPlacedEvent(instance.copy(), blockstate, player, pos)))
            original.call(instance, pDecrement);
    }


}
