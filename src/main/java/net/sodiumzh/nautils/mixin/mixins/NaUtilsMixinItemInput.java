package net.sodiumzh.nautils.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.item.INaUtilsItem;
import net.sodiumzh.nautils.item.NaUtilsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInput.class)
public class NaUtilsMixinItemInput {

    /**
     * Implementation of {@link NaUtilsItem} give command override feature.
     */
    @ModifyReturnValue(method = "createItemStack(IZ)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"))
    private ItemStack checkNaUtilsItemOverride(ItemStack original)
    {
        if (original.getItem() instanceof INaUtilsItem item
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

