package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaubleItem extends Item
{

	protected List<MutableComponent> descriptions = Lists.newArrayList();
	protected Predicate<ItemStack> shouldBeFoil = null;
	
	public BaubleItem(Properties pProperties)
	{
		super(pProperties.stacksTo(1));
	}
	
	public BaubleItem description(MutableComponent desc)
	{
		descriptions.add(desc);
		return this;
	}
	
	public BaubleItem foil()
	{
		shouldBeFoil = (i) -> true;
		return this;
	}
	
	public BaubleItem foilCondition(Predicate<ItemStack> cond)
	{
		shouldBeFoil = cond;
		return this;
	}
	
	@Override
	public final boolean isFoil(ItemStack stack)
	{
		if (shouldBeFoil == null)
			return false;
		return shouldBeFoil.test(stack);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, level, list, tooltipFlag);
		for (MutableComponent c: descriptions)
		{
			list.add(c);
		}
	}
}
