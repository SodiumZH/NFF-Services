package net.sodiumstudio.befriendmobs.entity.capability;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class HealingItemTable
{

	public static final HealingItemTable EMPTY = create();
	
	private HashMap<ItemInput, ItemOutputGetter> entries = new HashMap<>();
	private ItemInput buildingActiveEntry = null;
	
	public static HealingItemTable create() {
		return new HealingItemTable();
	}
	
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
	
	public HealingItemTable add(Item item, float amount)
	{
		ItemInput input = ItemInput.create(item);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(Predicate<ItemStack> predicate, float amount)
	{
		ItemInput input = ItemInput.create(predicate);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(TagKey<Item> tag, float amount)
	{
		ItemInput input = ItemInput.create(tag);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(ResourceLocation key, float amount)
	{
		ItemInput input = ItemInput.create(key);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}

	public HealingItemTable add(String key, float amount)
	{
		ItemInput input = ItemInput.create(key);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(Item item, Function<Mob, Float> amount)
	{
		ItemInput input = ItemInput.create(item);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(Predicate<ItemStack> predicate, Function<Mob, Float> amount)
	{
		ItemInput input = ItemInput.create(predicate);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(TagKey<Item> tag, Function<Mob, Float> amount)
	{
		ItemInput input = ItemInput.create(tag);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(ResourceLocation key, Function<Mob, Float> amount)
	{
		ItemInput input = ItemInput.create(key);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable add(String key, Function<Mob, Float> amount)
	{
		ItemInput input = ItemInput.create(key);
		entries.put(input, new ItemOutputGetter(amount));
		buildingActiveEntry = input;
		return this;
	}
	
	public HealingItemTable cooldown(int value)
	{
		if (buildingActiveEntry == null)
			throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
		entries.get(buildingActiveEntry).cooldown(value);
		return this;
	}
	
	public HealingItemTable cooldown(@Nonnull Function<Mob, Integer> getter)
	{
		if (buildingActiveEntry == null)
			throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
		entries.get(buildingActiveEntry).cooldown(getter);
		return this;
	}
	
	public HealingItemTable noConsume()
	{
		if (buildingActiveEntry == null)
			throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
		entries.get(buildingActiveEntry).noConsume();
		return this;
	}
	
	public HealingItemTable extraAction(Consumer<Mob> action)
	{
		if (buildingActiveEntry == null)
			throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
		entries.get(buildingActiveEntry).extraAction(action);
		return this;
	}
	
	@Nullable
	public HealingOutput getOutput(Mob mob, ItemStack stack)
	{
		for (var input: entries.keySet())
		{
			if (input.test(stack))
				return HealingOutput.getOutput(mob, entries.get(input));
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return entries.toString();
	}
	
	/**
	 * Only for debug mode, transform it to a visualized map.
	 */
	public HashMap<String, Float> toDebugMap(Mob mob)
	{
		HashMap<String, Float> map = new HashMap<>();
		int i = 0;
		for (var input: entries.keySet())
		{
			if (input.item != null)
				map.put(input.item.toString(), HealingOutput.getOutput(mob, entries.get(input)).amount);
			else if (input.tag != null)
				map.put(input.tag.location().toString(), HealingOutput.getOutput(mob, entries.get(input)).amount);
			else if (input.stackCheck != null)
			{
				map.put("{Predicate_" + Integer.toString(i) + "}", HealingOutput.getOutput(mob, entries.get(input)).amount);
				++i;
			}
			else if (input.key != null)
			{
				Item item = ForgeRegistries.ITEMS.getValue(input.key);
				if (item != null)
					map.put(item.toString(), HealingOutput.getOutput(mob, entries.get(input)).amount);
				else map.put("{Missing item: " + input.key.toString() + "}", HealingOutput.getOutput(mob, entries.get(input)).amount);
			}
		}
		return map;
	}
	
	
	
	
	public static class ItemInput implements Predicate<ItemStack>
	{
		// By checking if it's a specific type of item
		private final Item item;
		// By running predicate
		private final Predicate<ItemStack> stackCheck;
		// By checking if it has a tag
		private final TagKey<Item> tag;
		// By checking if it's an item found in registry.
		private final ResourceLocation key;
		
		private ItemInput(Item item, Predicate<ItemStack> stackCheck, TagKey<Item> tag, ResourceLocation key)
		{
			this.item = item;
			this.stackCheck = stackCheck;
			this.tag = tag;
			this.key = key;
		}
		
		public static ItemInput create(Item item, Predicate<ItemStack> stackCheck, TagKey<Item> tag, ResourceLocation key)
		{
			return new ItemInput(item, stackCheck, tag, key);
		}
		
		public static ItemInput create(Item in)
		{
			return new ItemInput(in, null, null, null);
		}
		
		public static ItemInput create(Predicate<ItemStack> in)
		{
			return new ItemInput(null, in, null, null);
		}
		
		public static ItemInput create(TagKey<Item> in)
		{
			return new ItemInput(null, null, in, null);
		}
		
		public static ItemInput create(ResourceLocation in)
		{
			return new ItemInput(null, null, null, in);
		}
		
		public static ItemInput create(String in)
		{
			return new ItemInput(null, null, null, new ResourceLocation(in));
		}
		
		@Override
		public boolean test(ItemStack stack)
		{
			boolean retval = true;
			boolean valid = false;
			if (item != null)
			{
				retval = retval && stack.is(item);
				valid = true;
				if (!retval) return false;
			}
			if (stackCheck != null)
			{
				retval = retval && stackCheck.test(stack);
				valid = true;
				if (!retval) return false;
			}
			if (tag != null)
			{
				retval = retval && stack.is(tag);
				valid = true;
				if (!retval) return false;
			}
			if (key != null)
			{
				Item item = ForgeRegistries.ITEMS.getValue(key);
				retval = retval && item != null && stack.is(item);
				valid = true;
				if (!retval) return false;
			}
			return retval && valid;
		}
		
		@Override
		public String toString()
		{
			String out = "ItemInput {";
			if (item != null)
				out = out + "Item (" + ForgeRegistries.ITEMS.getKey(item).toString() + "), ";
			if (stackCheck != null)
				out = out + "{Predicate}, ";
			if (tag != null)
				out = out + "Tag (" + tag.location().toString() + "), ";
			if (key != null)
				out = out + "Key (" + key.toString() + "), ";
			if (out.substring(out.length() - 2, out.length()) == ", ")
				out = out.substring(0, out.length() - 2);
			out = out + "}";
			return out;
		}
	}
	
	public static class ItemOutputGetter
	{
		protected Optional<Float> amountStatic = Optional.of(5f);
		protected Function<Mob, Float> amountGetter = null;
		protected Optional<Integer> cooldownStatic = Optional.of(40);
		protected Function<Mob, Integer> cooldownGetter = null;
		protected boolean noConsume = false;
		protected Consumer<Mob> extraAction = mob -> {};
		
		public ItemOutputGetter(float amount)
		{
			amountStatic = Optional.of(amount);
		}
		
		public ItemOutputGetter(@Nonnull Function<Mob, Float> amountGetter)
		{
			amountStatic = Optional.empty();
			this.amountGetter = amountGetter;
		}
		
		public void cooldown(int value)
		{
			cooldownStatic = Optional.of(value);
			cooldownGetter = null;
		}
		
		public void cooldown(@Nonnull Function<Mob, Integer> getter)
		{
			cooldownStatic = Optional.empty();
			cooldownGetter = getter;
		}
		
		public void noConsume()
		{
			noConsume = true;
		}
		
		public void extraAction(@Nullable Consumer<Mob> action)
		{
			this.extraAction = action;
		}
	}
	
	public static record HealingOutput(float amount, int cooldown, boolean noConsume, Consumer<Mob> action)
	{
		
		public static HealingOutput getOutput(Mob mob, ItemOutputGetter getter)
		{
			return new HealingOutput(
					getter.amountStatic.isPresent() ? getter.amountStatic.get() : getter.amountGetter.apply(mob),
					getter.cooldownStatic.isPresent() ? getter.cooldownStatic.get() : getter.cooldownGetter.apply(mob),
					getter.noConsume,
					getter.extraAction);
		}
	}
	
}
