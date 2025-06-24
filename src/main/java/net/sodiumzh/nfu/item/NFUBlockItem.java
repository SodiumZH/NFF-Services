package net.sodiumzh.nfu.item;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.mixin.mixin.NFUMixinItemInput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class NFUBlockItem extends ItemNameBlockItem implements INFUItem {
    protected List<Function<ItemStack, ? extends Component>> descriptions = new ArrayList<>();
    protected Predicate<ItemStack> shouldBeFoil = null;
    protected boolean shouldGiveCommandUseDefaultInstance = false;
    protected boolean shouldUseBlockDescriptionId = true;
    protected Optional<Supplier<String>> blockDescriptionIdOverride = Optional.empty();
    protected Optional<Supplier<ItemStack>> defaultInstanceSupplier = Optional.empty();
    @Nullable
    protected BiFunction<ItemStack, MutableComponent, MutableComponent> nameStyle = null;

    public NFUBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    /**
     * Add an {@code ItemStack}-depending {@code Component} supplier to hovering text.
     */
    public NFUBlockItem description(Function<ItemStack, ? extends Component> desc)
    {
        descriptions.add(desc);
        return this;
    }

    /**
     * Add a description {@code Component} supplier to hovering text. 
     */
    public NFUBlockItem description(Supplier<? extends Component> desc)
    {
        descriptions.add(i -> desc.get());
        return this;
    }

    /**
     * Add a description {@code Component} to hovering text. 
     */
    public NFUBlockItem description(Component desc)
    {
        descriptions.add(i -> desc);
        return this;
    }

    /**
     * Set the item should be always foiled as if it's enchanted.
     */
    public final NFUBlockItem alwaysFoil()
    {
        shouldBeFoil = (i) -> true;
        return this;
    }

    /**
     * If input condition is true, the item will be foiled as if it's enchanted.
     */
    public NFUBlockItem foilCondition(Predicate<ItemStack> cond)
    {
        shouldBeFoil = cond;
        return this;
    }

    /**
     * Set the default instance getter.
     * @param supplier Default instance getter. Leave {@code null} to use {@code new ItemStack(this)}.
     * @return {@code this}.
     */
    public NFUBlockItem setDefaultInstanceOverride(@Nullable Supplier<ItemStack> supplier)
    {
        this.defaultInstanceSupplier = Optional.ofNullable(supplier);
        return this;
    }

    /**
     * Set the default instance getter.
     * @param getter Default instance getter (input = {@code this}). Leave {@code null} to use {@code new ItemStack(this)}.
     * @return {@code this}.
     */
    public NFUBlockItem setDefaultInstanceOverride(@Nullable Function<NFUBlockItem, ItemStack> getter)
    {
        if (getter == null) return this.setDefaultInstanceOverride((Supplier<ItemStack>) null);
        return this.setDefaultInstanceOverride(() -> getter.apply(this));
    }

    /**
     * Declare that this item should use {@code ItemStack.EMPTY} as default instance.
     * @param suppressPrintInfo If true, when accessing default item, it will print info to log. Set false to prevent repeated output.
     */
    public NFUBlockItem noDefaultInstance(boolean suppressPrintInfo)
    {
        return this.setDefaultInstanceOverride(() -> {
            if (!suppressPrintInfo)
                LogUtils.getLogger().info(String.format("Item class \"%s\" has no default instance.", this.getClass().getSimpleName()));
            return ItemStack.EMPTY;
        });
    }

    /**
     * Declare that this item should use another item's default instance as default instance.
     */
    public NFUBlockItem redirectDefaultInstance(Supplier<? extends Item> other)
    {
        return this.setDefaultInstanceOverride(() -> Optional.ofNullable(other.get()).map(Item::getDefaultInstance).orElseGet(() -> ItemStack.EMPTY));
    }

    /**
     * Declare that this item should use another item's default instance as default instance. Input is the registry key.
     */
    public NFUBlockItem redirectDefaultInstance(ResourceLocation itemKey)
    {
        return this.setDefaultInstanceOverride(() -> Optional.ofNullable(ForgeRegistries.ITEMS.getValue(itemKey)).map(Item::getDefaultInstance).orElseGet(() -> ItemStack.EMPTY));
    }

    public NFUBlockItem setGiveCommandUsesDefaultInstance() {
        this.shouldGiveCommandUseDefaultInstance = true;
        return this;
    }

    public NFUBlockItem usesBlockDescriptionId(boolean val) {
        this.shouldUseBlockDescriptionId = val;
        return this;
    }

    public NFUBlockItem setBlockDescriptionIdOverride(Supplier<String> getter) {
        this.blockDescriptionIdOverride = Optional.ofNullable(getter);
        return this;
    }

    public final String getDescriptionId() {
        return this.blockDescriptionIdOverride.map(Supplier::get)
            .orElseGet(() -> shouldGiveCommandUseDefaultInstance ? this.getBlock().getDescriptionId() : this.getOrCreateDescriptionId());
    }

    /**
     * Check if this item should use {@code getDefaultInstance()} instead of {@code ItemStack#new} on /give command.
     * <p>This feature is intended to prevent /give command from outputting uninitialized {@code ItemStack}s which may
     * cause problems.
     * <p>This feature is implemented through {@link NFUMixinItemInput}.
     */
    @Override
    public boolean shouldGiveCommandUseDefaultInstance() {
        return shouldGiveCommandUseDefaultInstance;
    }

    @Override
    public boolean shouldConsumeInCreative() {
        return false;
    }

    @Override
    public boolean shouldSkipEntityInteract(Player user, Entity target, InteractionHand hand) {
        return false;
    }


    /**
     * Override to set the default instance. Return {@code Optional.empty()} to use {@code new ItemStack(this)}.
     * <p>Note: {@code setDefaultInstanceOverride} or variations will override this method.
     * <p>Note: To return empty {@code ItemStack}, return {@code Optional.of(ItemStack.EMPTY)}.
     */
    @Nonnull
    public Optional<ItemStack> getDefaultInstanceOverride()
    {
        return Optional.empty();
    }

    /**
     * Fixed here. Override {@code getDefaultInstanceOverride} or call {@code setDefaultInstanceOverride} instead.
     */
    public final ItemStack getDefaultInstance()
    {
        return this.defaultInstanceSupplier.map(Supplier::get).orElseGet(() -> getDefaultInstanceOverride().orElseGet(() -> super.getDefaultInstance()));
    }

    /**
     * Fixed here. Override by calling {@code foilCondition} instead.
     */
    @Override
    public final boolean isFoil(ItemStack stack)
    {
        if (shouldBeFoil == null)
            return false;
        return shouldBeFoil.test(stack);
    }

    public InteractionResult interactLivingEntity(Player player, LivingEntity target, InteractionHand hand)
    {
        return InteractionResult.PASS;
    }

    /**
     * Final here because the input {@code ItemStack} is a copy which may confuse the developers
     * and cause bugs hard to find when attempting to modify the ItemStack NBT.
     * <p> Override the version without {@code ItemStack} instead. To access the copy, use {@code player.getItemInHand(hand).copy()} instead.
     */
    @Override
    public final InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
    {
        return interactLivingEntity(player, target, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, level, list, tooltipFlag);
        this.beforeAddingHoveringDescriptions(stack, level, list, tooltipFlag);
        for (var c: descriptions)
        {
            Component cpnt = c.apply(stack);
            if (cpnt != null)
                list.add(cpnt);
        }
    }

    /**
     * Invoked before adding description text to hovering text. No action by default.
     */
    @OnlyIn(Dist.CLIENT)
    public void beforeAddingHoveringDescriptions(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {}

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public BiFunction<ItemStack, MutableComponent, MutableComponent> getNameStyle() {
        return this.nameStyle;
    }

    public NFUBlockItem setNameStyle(BiFunction<ItemStack, MutableComponent, MutableComponent> styleModifier) {
        this.nameStyle = styleModifier;
        return this;
    }

    @Override
    public @Nonnull Component getName(ItemStack pStack) {
        Component c = super.getName(pStack);
        if (c instanceof MutableComponent mc)
            return Optional.ofNullable(getNameStyle()).map(f -> f.apply(pStack, mc)).orElse(mc);
        else return c;
    }
}
