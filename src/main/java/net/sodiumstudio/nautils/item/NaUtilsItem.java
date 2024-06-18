package net.sodiumstudio.nautils.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * {@code NaUtilsItem} is an {@link Item} template with some simplifications, e.g. foiling, hovering descriptions, etc.
 */
public class NaUtilsItem extends Item
{
	protected List<Supplier<MutableComponent>> descriptions = new ArrayList<>();
	protected Predicate<ItemStack> shouldBeFoil = null;
	
	public NaUtilsItem(Properties pProperties)
	{
		super(pProperties);
	}

	/**
	 * Add a description {@code Component} supplier to hovering text. 
	 */
	public NaUtilsItem description(Supplier<MutableComponent> desc)
	{
		descriptions.add(desc);
		return this;
	}
	
	/**
	 * Add a description {@code Component} to hovering text. 
	 */
	public NaUtilsItem description(MutableComponent desc)
	{
		descriptions.add(() -> desc);
		return this;
	}
	
	/**
	 * Set the item should be always foiled as if it's enchanted.
	 */
	public final NaUtilsItem alwaysFoil()
	{
		shouldBeFoil = (i) -> true;
		return this;
	}
	
	/**
	 * If input condition is true, the item will be foiled as if it's enchanted.
	 */
	public NaUtilsItem foilCondition(Predicate<ItemStack> cond)
	{
		shouldBeFoil = cond;
		return this;
	}
	
	/**
	 * Fixed here. Invoke {@code foilCondition} instead.
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
	 * Fixed here because the input {@code ItemStack} is a copy which may confuse the developers
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
			list.add(c.get());
		}
	}
	
	/**
	 * Invoked before adding description text to hovering text. No action by default.
	 */
	@OnlyIn(Dist.CLIENT)
	public void beforeAddingHoveringDescriptions(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {}
	
	/**
	 * Cast to subclasses.
	 * <p>Note: this method is for simplification, hiding an unchecked casting inside. So take care doing this to prevent {@code ClassCastException}.
	 */
	@SuppressWarnings("unchecked")
	public <T extends NaUtilsItem> T cast()
	{
		return (T)this;
	}

}
