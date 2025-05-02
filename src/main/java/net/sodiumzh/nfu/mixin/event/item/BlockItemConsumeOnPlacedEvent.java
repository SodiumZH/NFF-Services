package net.sodiumzh.nfu.mixin.event.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted before an {@link ItemStack} of {@link BlockItem} is about to be consumed
 * after being placed onto the level.
 * <p>Cancellable. If cancelled, the item will not be consumed, but the placed block will
 * still be there.
 */
public class BlockItemConsumeOnPlacedEvent extends Event {

    private final ItemStack itemStackCopy;
    private final BlockState blockState;
    private final Player player;
    private final BlockPos pos;

    public BlockItemConsumeOnPlacedEvent(ItemStack itemStackCopy, BlockState blockState, Player player, BlockPos pos) {
        this.itemStackCopy = itemStackCopy;
        this.blockState = blockState;
        this.player = player;
        this.pos = pos;
    }

    public ItemStack getItemStackCopy() {
        return itemStackCopy;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockPos getPos() {
        return pos;
    }

}
