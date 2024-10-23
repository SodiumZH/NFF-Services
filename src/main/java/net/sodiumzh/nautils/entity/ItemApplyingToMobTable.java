package net.sodiumzh.nautils.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.data.ServerSideRegistry;

/**
 * An {@code ItemApplyingToMobTable} is a collection of information about if an {@link ItemStack}
 * is usable to a {@link Mob} and its usage result, including a double "amount" value for e.g. healing,
 * a cool-down time, whether the item should be consumed, and extra actions of the mob.
 */
@ServerSideRegistry
public class ItemApplyingToMobTable
{

	public static final ItemApplyingToMobTable EMPTY = new ItemApplyingToMobTable();
	
	private HashMap<Input, OutputGetter> entries = new HashMap<>();
	
	protected ItemApplyingToMobTable()
	{
	}
	
	protected ItemApplyingToMobTable(HashMap<Input, OutputGetter> entries)
	{
		this.entries = entries;
	}

	/**
	 * Create a new builder.
	 * <p> Note: for a new builder, always call any of {@code add()} first, otherwise it will crash.
	 */
	public static ItemApplyingToMobTable.Builder builder() {
		return new ItemApplyingToMobTable.Builder();
	}
	
	/** @deprecated use {@code builder()} instead */
	@Deprecated
	public static ItemApplyingToMobTable.Builder create() {
		return new ItemApplyingToMobTable.Builder();
	}
	
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
	
	@Nullable
	public Output getOutput(Mob mob, ItemStack stack)
	{
		for (var input: entries.keySet())
		{
			if (input.test(stack))
				return Output.getOutput(mob, entries.get(input));
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
	public HashMap<String, Double> toDebugMap(Mob mob)
	{
		HashMap<String, Double> map = new HashMap<>();
		int i = 0;
		for (var input: entries.keySet())
		{
			if (input.item != null)
				map.put(input.item.toString(), Output.getOutput(mob, entries.get(input)).amount);
			else if (input.tag != null)
				map.put(input.tag.location().toString(), Output.getOutput(mob, entries.get(input)).amount);
			else if (input.stackCheck != null)
			{
				map.put("{Predicate_" + Integer.toString(i) + "}", Output.getOutput(mob, entries.get(input)).amount);
				++i;
			}
			else if (input.key != null)
			{
				Item item = ForgeRegistries.ITEMS.getValue(input.key);
				if (item != null)
					map.put(item.toString(), Output.getOutput(mob, entries.get(input)).amount);
				else map.put("{Missing item: " + input.key.toString() + "}", Output.getOutput(mob, entries.get(input)).amount);
			}
		}
		return map;
	}
	
	public static class Builder
	{
		private HashMap<Input, OutputGetter> entries = new HashMap<>();
		private Input buildingActiveEntry = null;
		private DataReader reader = null;

		public ItemApplyingToMobTable.Builder addRaw(Input in, OutputGetter out)
		{
			entries.put(in, out);
			buildingActiveEntry = in;
			return this;
		}
		/**
		 * Put a new entry.
		 * @param input Raw input object.
		 * @param amount Result amount (fixed value).
		 */
		public ItemApplyingToMobTable.Builder add(Input input, double amount)
		{
			return addRaw(input, new OutputGetter(amount));
		}
		
		/**
		 * Put a new entry.
		 * @param item Input item.
		 * @param amount Result amount (fixed value).
		 */
		public ItemApplyingToMobTable.Builder add(Item item, double amount)
		{
			return addRaw(Input.create(item), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(Predicate<ItemStack> predicate, double amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(TagKey<Item> tag, double amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(ResourceLocation key, double amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}

		public ItemApplyingToMobTable.Builder add(String key, double amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));

		}
		
		public ItemApplyingToMobTable.Builder add(Item item, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(item), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(Predicate<ItemStack> predicate, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(TagKey<Item> tag, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(ResourceLocation key, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}
		
		public ItemApplyingToMobTable.Builder add(String key, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}

		public ItemApplyingToMobTable.Builder add(Item item, Supplier<Double> amount)
		{
			return addRaw(Input.create(item), new OutputGetter(mob -> amount.get()));
		}

		public ItemApplyingToMobTable.Builder add(Predicate<ItemStack> predicate, Supplier<Double> amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(mob -> amount.get()));
		}

		public ItemApplyingToMobTable.Builder add(TagKey<Item> tag, Supplier<Double> amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(mob -> amount.get()));
		}

		public ItemApplyingToMobTable.Builder add(ResourceLocation key, Supplier<Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(mob -> amount.get()));
		}

		public ItemApplyingToMobTable.Builder add(String key, Supplier<Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(mob -> amount.get()));
		}

		public ItemApplyingToMobTable.Builder addPredicate(@Nonnull Predicate<ItemStack> predicate)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			buildingActiveEntry.addPredicate(predicate);
			return this;
		}

		public ItemApplyingToMobTable.Builder cooldown(int value)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).cooldown(value);
			return this;
		}
		
		public ItemApplyingToMobTable.Builder cooldown(@Nonnull Function<Mob, Integer> getter)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).cooldown(getter);
			return this;
		}
		
		public ItemApplyingToMobTable.Builder noConsume()
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).noConsume();
			return this;
		}
		
		public ItemApplyingToMobTable.Builder extraAction(Consumer<Mob> action)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).extraAction(action);
			return this;
		}

		/**
		 * Define a resource location and a method (parser) to read data and merge into the table on building.
		 * @Param loc
		 */
		public ItemApplyingToMobTable.Builder readData(@Nonnull ResourceLocation loc,
				@Nonnull BiConsumer<JsonElement, ItemApplyingToMobTable.Builder> parser)
		{
			this.reader = new DataReader(this, loc, parser);
			return this;
		}

		public ItemApplyingToMobTable build()
		{
			if (this.reader != null) reader.read();
			MinecraftForge.EVENT_BUS.post(new ItemApplyingToMobTable.BuildEvent(this));
			return new ItemApplyingToMobTable(this.entries);
		}
	}
	
	/**
	 * An {@code Input} is a check for given {@link ItemStack}. 
	 * If the input {@link ItemStack} satisfies a specific {@code Input}, the {@code ItemApplyingToMobTable} will return corresponding {@code OutputGetter}.
	 * <p> It now accepts 4 types of checks: 
	 * <p> a) {@link Item}, to check if the given {@link ItemStack} is this type of {@link Item}.
	 * <p> b) {@link ResourceLocation} of item, browsing {@link Item} from registry and check if the {@link ItemStack} is the found {@link Item}.
	 * It could be optional and can be applied for other mods' items. If the item is not found (e.g. due to the mod not loaded), it will be ignored and won't throw exceptions.
	 * <p> c) {@link TagKey} to check if the given {@link ItemStack} has this tag.
	 * <p> d) {@link Predicate<ItemStack>} to simply check if the given {@link ItemStack} satisfies the condition.
	 */
	public static class Input implements Predicate<ItemStack>
	{
		// By checking if it's a specific type of item
		private final Item item;
		// By running predicate
		private Predicate<ItemStack> stackCheck;
		// By checking if it has a tag
		private final TagKey<Item> tag;
		// By checking if it's an item found in registry.
		private final ResourceLocation key;
		
		private Input(Item item, Predicate<ItemStack> stackCheck, TagKey<Item> tag, ResourceLocation key)
		{
			this.item = item;
			this.stackCheck = stackCheck;
			this.tag = tag;
			this.key = key;
		}
		
		/** Create a raw {@code Input}. Not recommended unless you know what you're doing. */
		public static Input createRaw(Item item, Predicate<ItemStack> stackCheck, TagKey<Item> tag, ResourceLocation key)
		{
			return new Input(item, stackCheck, tag, key);
		}
		
		/** Create from {@link Item}. */
		public static Input create(Item in)
		{
			return new Input(in, null, null, null);
		}
		
		/** Create from {@link Predicate}. */
		public static Input create(Predicate<ItemStack> in)
		{
			return new Input(null, in, null, null);
		}
		
		/** Create from {@link TagKey}. */
		public static Input create(TagKey<Item> in)
		{
			return new Input(null, null, in, null);
		}
		
		/** Create from {@link ResourceLocation} as registry key. */
		public static Input create(ResourceLocation in)
		{
			return new Input(null, null, null, in);
		}
		
		/** Create from {@link ResourceLocation} as registry key. The input string should be formatted like "modid:name_key" to create a {@link ResourceLocation} in-situ. */
		public static Input create(String in)
		{
			return new Input(null, null, null, new ResourceLocation(in));
		}
		
		public void addPredicate(@Nonnull Predicate<ItemStack> predicate)
		{
			if (this.stackCheck == null)
				this.stackCheck = predicate;
			else this.stackCheck = this.stackCheck.and(predicate);
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
			String out = "Input {";
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
	
	/**
	 * An {@code OutputGetter} is a collection of information how it should respond to a specific {@code Input}.
	 */
	public static class OutputGetter
	{
		protected Optional<Double> amountStatic = Optional.of(5d);
		protected Function<Mob, Double> amountGetter = null;
		protected Optional<Integer> cooldownStatic = Optional.of(40);
		protected Function<Mob, Integer> cooldownGetter = null;
		protected boolean noConsume = false;
		protected Consumer<Mob> extraAction = mob -> {};
		
		public OutputGetter(double amount)
		{
			amountStatic = Optional.of(amount);
		}
		
		public OutputGetter(@Nonnull Function<Mob, Double> amountGetter)
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
	
	/**
	 * An {@code Output} is a collections of results applying an {@code OutputGetter} to a {@link Mob}.
	 */
	public static record Output(Double amount, int cooldown, boolean noConsume, Consumer<Mob> action)
	{
		
		public static Output getOutput(Mob mob, OutputGetter getter)
		{
			return new Output(
					getter.amountStatic.isPresent() ? getter.amountStatic.get() : getter.amountGetter.apply(mob),
					getter.cooldownStatic.isPresent() ? getter.cooldownStatic.get() : getter.cooldownGetter.apply(mob),
					getter.noConsume,
					getter.extraAction);
		}
	}
	
	public static class BuildEvent extends Event
	{
		public final ItemApplyingToMobTable.Builder builder;
		
		public BuildEvent(ItemApplyingToMobTable.Builder builder)
		{
			this.builder = builder;
		}
	}

	private static class DataReader
	{
		private ResourceLocation location;
		private ItemApplyingToMobTable.Builder builder;
		private BiConsumer<JsonElement, Builder> parser;

		public DataReader(ItemApplyingToMobTable.Builder builder, ResourceLocation location, BiConsumer<JsonElement, Builder> parser)
		{
			this.location = location;
			this.builder = builder;
			this.parser = parser;
		}

		public void read()
		{
			if (builder == null || location == null) return;
			MinecraftServer server = NaUtils.getServer();
			if (server == null) return;
			ResourceManager mgr = server.getResourceManager();
			List<Resource> resources = mgr.getResourceStack(location);
			for (Resource r: resources)
			{
				try {
					InputStream input = r.open();
					Reader reader = new InputStreamReader(input);
					JsonElement json = JsonParser.parseReader(reader);
					parser.accept(json, builder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
