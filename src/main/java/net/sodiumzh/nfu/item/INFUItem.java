package net.sodiumzh.nfu.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinItemInput;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinPlayer;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinServerPlayerGameMode;
import net.sodiumzh.nfu.object.ICastable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.*;

/**
 * Base interface for NaUtils item templates. This is only a template, and all implementations are done in subclasses.
 * Do not implement this interface unless you fully understand how it works.
 */
public interface INFUItem extends ICastable, ItemLike {

    // Dynamic Description //

    /**
     * Add an {@code ItemStack}-depending {@code Component} supplier to hovering text.
     */
    public INFUItem description(Function<ItemStack, ? extends Component> desc);

    /**
     * Add a description {@code Component} supplier to hovering text.
     */
    public INFUItem description(Supplier<? extends Component> desc);

    /**
     * Add a description {@code Component} to hovering text.
     */
    public INFUItem description(Component desc);

    /**
     * Set the item should be always foiled as if it's enchanted.
     */
    public INFUItem alwaysFoil();

    /**
     * If input condition is true, the item will be foiled as if it's enchanted.
     */
    public INFUItem foilCondition(Predicate<ItemStack> cond);


    // Default Instance Operation //

    /**
     * Set the default instance getter.
     * @param supplier Default instance getter. Leave {@code null} to use {@code new ItemStack(this)}.
     * @return {@code this}.
     */
    public INFUItem setDefaultInstanceOverride(@Nullable Supplier<ItemStack> supplier);

    /**
     * Declare that this item should use {@code ItemStack.EMPTY} as default instance.
     * @param suppressPrintInfo If true, when accessing default item, it will print info to log. Set false to prevent repeated output.
     */
    public INFUItem noDefaultInstance(boolean suppressPrintInfo);

    /**
     * Declare that this item should use another item's default instance as default instance.
     */
    public INFUItem redirectDefaultInstance(Supplier<? extends Item> other);

    /**
     * Declare that this item should use another item's default instance as default instance. Input is the registry key.
     */
    public INFUItem redirectDefaultInstance(ResourceLocation itemKey);

    public INFUItem setGiveCommandUsesDefaultInstance();
    /**
     * Check if this item should use {@code getDefaultInstance()} instead of {@code ItemStack#new} on /give command.
     * <p>This feature is intended to prevent /give command from outputting uninitialized {@code ItemStack}s which may
     * cause problems.
     * <p>This feature is implemented through {@link NFUMixinItemInput}.
     */
    public boolean shouldGiveCommandUseDefaultInstance();

    /**
     * Override to set the default instance. Return {@code Optional.empty()} to use {@code new ItemStack(this)}.
     * <p>Note: {@code setDefaultInstanceOverride} or variations will override this method.
     * <p>Note: To return empty {@code ItemStack}, return {@code Optional.of(ItemStack.EMPTY)}.
     */
    @Nonnull
    public Optional<ItemStack> getDefaultInstanceOverride();

    // Force Creative Consumption //

    /**
     * Check if this item should be consumed if the player is in creative mode.
     * <p>This feature is implemented through {@link NFUMixinServerPlayerGameMode}.
     */
    public boolean shouldConsumeInCreative();

    // Usage Skipping //

    /**
     * Whether this item's interaction should skip {@link Entity#interact} to ensure {@link Item#interactLivingEntity} being invoked.
     * <p>Implemented through {@link NFUMixinPlayer}.
     */
    public boolean shouldSkipEntityInteract(Player user, Entity target, InteractionHand hand);


/*
    public boolean shouldSkipUsagePhase(UsagePhase phase, UsageContext ctx);

    public static enum UsagePhase {

        ENTITY_X_INTERACT,

        ITEM_X_INTERACTION_LIVING,

        ITEM_X_USE,

        ITEM_X_USE_ON

    }

    public static class UsageContext {
        // 0 = simple use; 1 = use on block; 2 = use on entity
        private final int type;
        private final Player player;
        private final InteractionHand hand;
        private final BlockPos onBlockPos;
        private final BlockState onBlockState;
        private final Entity onEntity;

        private UsageContext(int type, Player player, InteractionHand hand, BlockPos onBlockPos, BlockState onBlockState, Entity onEntity) {
            this.type = type;
            this.player = player;
            this.hand = hand;
            this.onBlockPos = onBlockPos;
            this.onBlockState = onBlockState;
            this.onEntity = onEntity;
        }

        public static UsageContext forUse(Player player, InteractionHand hand) {
            return new UsageContext(0, player, hand, null, null, null);
        }

        public static UsageContext forUseOn(Player player, InteractionHand hand, BlockPos pos) {
            return new UsageContext(1, player, hand, pos, player.level().getBlockState(pos), null);
        }

        public static UsageContext forInteractEntity(Player player, InteractionHand hand, Entity entity) {
            return new UsageContext(2, player, hand, null, null, entity);
        }

        public boolean isUse() {
            return type == 0;
        }

        public boolean isUseOn() {
            return type == 1;
        }

        public boolean isInteractEntity() {
            return type == 2;
        }

        public Player getPlayer() {
            return player;
        }

        public InteractionHand getHand() {
            return hand;
        }

        @Nullable
        public BlockPos getBlockPos() {
            return onBlockPos;
        }

        @Nullable
        public BlockState getOnBlockState() {
            return onBlockState;
        }

        @Nullable
        public Entity getOnEntity() {
            return onEntity;
        }
    }
*/

    /**
     * If non-null, it will override rarity and style the item name.
     * Implemented through {@NFUGuiMixin#onItemNameStyle}.
     * <p>Note: the input {@link Component} of the bi-function is already formatted by the rarity style. Use the function
     * to re-format it.
     */
    @Nullable
    public BiFunction<ItemStack, MutableComponent, MutableComponent> getNameStyle();


    public INFUItem setNameStyle(BiFunction<ItemStack, MutableComponent, MutableComponent> styleModifier);

    public default INFUItem setNameStyle(UnaryOperator<MutableComponent> styleModifier) {
        return setNameStyle((i, c) -> {return styleModifier.apply(c);});
    }

    public default INFUItem setNameStyle(Consumer<MutableComponent> styleModifier) {
        return setNameStyle((i, c) -> {styleModifier.accept(c); return c;});
    }

    public default INFUItem setNameStyle(BiConsumer<ItemStack, MutableComponent> styleModifier) {
        return setNameStyle((i, c) -> {styleModifier.accept(i, c); return c;});
    }


}
