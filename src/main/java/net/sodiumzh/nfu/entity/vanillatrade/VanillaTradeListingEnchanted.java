package net.sodiumzh.nfu.entity.vanillatrade;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.sodiumzh.nfu.container.Tuple2;
import net.sodiumzh.nfu.math.RangedRandomInt;

import java.util.List;

import java.util.Random;

/**
 * A {@code VanillaTradeListingEnchanted} is a {@code VanillaTradeListing} of which the result is enchanted. 
 * The enchantment source can be either (Enchantment+level) or {@code RandomEnchantmentSelector}.
 */
public class VanillaTradeListingEnchanted extends VanillaTradeListing
{
	private RandomEnchantmentSelector selector;
	
	public VanillaTradeListingEnchanted(Enchantment e, int level) 
	{
		super();
		this.selector = new RandomEnchantmentSelector();
		this.selector.add(e, level, 1d);
		this.selector.build();
	}
	
	public VanillaTradeListingEnchanted(RandomEnchantmentSelector sel)
	{
		super();
		this.selector = sel;
	}

	/**
	 * Set this entry should be a book enchantment trade (like vanilla librarian). Book slot is B, and you still
	 * need to manually set cost A.
	 */
	public VanillaTradeListingEnchanted setEnchantsBook() {
		this.hasB = true;
		this.costB.clear();
		this.costB.add(new ItemStack(Items.BOOK, 1));
		this.bCount = RangedRandomInt.fixed(1);
		this.result.clear();
		this.result.add(new ItemStack(Items.ENCHANTED_BOOK, 1));
		this.resCount = RangedRandomInt.fixed(1);
		return this;
	}

	/**
	 * Set this entry should be a book enchantment trade (like vanilla librarian). Book slot is B, and you still
	 * need to manually set cost A.
	 */
	public VanillaTradeListingEnchanted setEnchants() {
		this.hasB = true;
		this.costB.clear();
		this.costB.add(new ItemStack(Items.BOOK, 1));
		this.result.clear();
		this.result.add(new ItemStack(Items.ENCHANTED_BOOK, 1));
		return this;
	}

	@Override
	public MerchantOffer getOffer(Entity trader, Random rnd)
	{
		MerchantOffer offer = super.getOffer(trader, rnd);
		Tuple<Enchantment, Integer> e = selector.select();
		offer.getResult().enchant(e.getA(), e.getB());
		return offer;
	}

	public RandomEnchantmentSelector getEnchantmentSelector() {
		return this.selector;
	}

	public List<Tuple2<Enchantment, Integer>> getAllPossibleEnchantments() {
		return selector.getTableSnapshot().entryStream()
			.filter(entry -> entry.value() > 0)
			.map(entry -> new Tuple2<>(entry.rowKey(), entry.columnKey()))
			.toList();
	}
}
