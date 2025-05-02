package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nfu.item.INFUItem;
import net.sodiumzh.nfu.item.NFUItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInput.class)
public class NFUMixinItemInput {

    /**
     * Implementation of {@link NFUItem} give command override feature.
     */
    @ModifyReturnValue(method = "createItemStack(IZ)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"))
    private ItemStack checkNaUtilsItemOverride(ItemStack original)
    {
        if (original.getItem() instanceof INFUItem item
            && item.shouldGiveCommandUseDefaultInstance())
        {
            ItemStack res = item.asItem().getDefaultInstance().copy();
            if (res.isEmpty()) return res;
            if (original.hasTag()) {    // Merge tag input to the new ItemStack
                for (String key: original.getTag().getAllKeys()) {
                    res.getOrCreateTag().put(key, original.getTag().get(key).copy());
                }
            }
            return res;
        }
        else return original;
    }
}

