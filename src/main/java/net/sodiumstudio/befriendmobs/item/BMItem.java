package net.sodiumstudio.befriendmobs.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleItem;

public class BMItem extends Item
{

	protected List<MutableComponent> descriptions = new ArrayList<MutableComponent>();
	protected Predicate<ItemStack> shouldBeFoil = null;
	
	public BMItem(Properties pProperties)
	{
		super(pProperties);
	}

	public BMItem description(MutableComponent desc)
	{
		descriptions.add(desc);
		return this;
	}
	
	public BMItem foil()
	{
		shouldBeFoil = (i) -> true;
		return this;
	}
	
	public BMItem foilCondition(Predicate<ItemStack> cond)
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
	
	@SuppressWarnings("unchecked")
	public <T extends BMItem> T cast()
	{
		return (T)this;
	}
	
	
}
