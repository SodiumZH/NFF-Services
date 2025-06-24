package net.sodiumzh.nfu.compat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sodiumzh.nfu.util.NFUInfoStatics;
import net.sodiumzh.nfu.util.NFUMiscStatics;

/**
 * A fallback for a mod-depending item's dependency is absent. It will print
 * info to tell the player to add the dependency.
 */
public class ModDependencyFallbackItem extends Item {

    private final String dependencyName;

    public ModDependencyFallbackItem(String dependencyName, Item.Properties properties)
    {
        super(properties);
        this.dependencyName = dependencyName;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide)
        {
            NFUMiscStatics.printToScreen(NFUInfoStatics.createTranslatable(
                    "info.nfulib.item_missing_dependency", this.dependencyName), pPlayer);
            return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }
}
