package net.sodiumzh.nautils.item;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.mixin.mixins.NaUtilsMixinItemInput;
import net.sodiumzh.nautils.mixin.mixins.NaUtilsMixinServerPlayerGameMode;
import net.sodiumzh.nautils.object.ICastable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Base interface for NaUtils item templates. This is only a template, and all implementations are done in subclasses.
 * Do not implement this interface unless you fully understand how it works.
 */
public interface INaUtilsItem extends ICastable, ItemLike {

    /**
     * Add an {@code ItemStack}-depending {@code Component} supplier to hovering text.
     */
    public INaUtilsItem description(Function<ItemStack, ? extends Component> desc);

    /**
     * Add a description {@code Component} supplier to hovering text.
     */
    public INaUtilsItem description(Supplier<? extends Component> desc);

    /**
     * Add a description {@code Component} to hovering text.
     */
    public INaUtilsItem description(Component desc);

    /**
     * Set the item should be always foiled as if it's enchanted.
     */
    public INaUtilsItem alwaysFoil();

    /**
     * If input condition is true, the item will be foiled as if it's enchanted.
     */
    public INaUtilsItem foilCondition(Predicate<ItemStack> cond);

    /**
     * Set the default instance getter.
     * @param supplier Default instance getter. Leave {@code null} to use {@code new ItemStack(this)}.
     * @return {@code this}.
     */
    public INaUtilsItem setDefaultInstanceOverride(@Nullable Supplier<ItemStack> supplier);

    /**
     * Declare that this item should use {@code ItemStack.EMPTY} as default instance.
     * @param suppressPrintInfo If true, when accessing default item, it will print info to log. Set false to prevent repeated output.
     */
    public INaUtilsItem noDefaultInstance(boolean suppressPrintInfo);

    /**
     * Declare that this item should use another item's default instance as default instance.
     */
    public INaUtilsItem redirectDefaultInstance(Supplier<? extends Item> other);

    /**
     * Declare that this item should use another item's default instance as default instance. Input is the registry key.
     */
    public INaUtilsItem redirectDefaultInstance(ResourceLocation itemKey);

    public INaUtilsItem setGiveCommandUsesDefaultInstance();
    /**
     * Check if this item should use {@code getDefaultInstance()} instead of {@code ItemStack#new} on /give command.
     * <p>This feature is intended to prevent /give command from outputting uninitialized {@code ItemStack}s which may
     * cause problems.
     * <p>This feature is implemented through {@link NaUtilsMixinItemInput}.
     */
    public boolean shouldGiveCommandUseDefaultInstance();

    /**
     * Check if this item should be consumed if the player is in creative mode.
     * <p>This feature is implemented through {@link NaUtilsMixinServerPlayerGameMode}.
     */
    public boolean shouldConsumeInCreative();

    /**
     * Override to set the default instance. Return {@code Optional.empty()} to use {@code new ItemStack(this)}.
     * <p>Note: {@code setDefaultInstanceOverride} or variations will override this method.
     * <p>Note: To return empty {@code ItemStack}, return {@code Optional.of(ItemStack.EMPTY)}.
     */
    @Nonnull
    public Optional<ItemStack> getDefaultInstanceOverride();


}
