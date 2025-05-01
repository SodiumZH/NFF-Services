package net.sodiumzh.nfu.mixin.event.item;


import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted when a Grindstone checks if an item can be placed into the input slots.
 * <p>Not cancellable, but has result. Allow = can always place; Default = uses
 * vanilla behaviors; Deny = can never place.
 */
@Event.HasResult
public class GrindstoneAcceptItemEvent extends Event {

    private final ItemStack stackIn;
    private final boolean isTop;


    public GrindstoneAcceptItemEvent(ItemStack stackIn, boolean isTop) {
        this.stackIn = stackIn;
        this.isTop = isTop;
    }


    public boolean isTop() {
        return isTop;
    }

    public ItemStack getInput() {
        return stackIn;
    }



}
