package net.sodiumzh.nfu.entity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.container.Tuple2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

/**
 * A {@code MobApplicableItemTable} is a collection of information about if an {@link ItemStack}
 * is usable to a {@link Mob} and its usage result, including a double "amount" value for e.g. healing,
 * a cool-down time, whether the item should be consumed, and extra actions of the mob.
 */
public class MobApplicableItemTable
{

	public static final MobApplicableItemTable EMPTY = new MobApplicableItemTable();
	
	private HashMap<Input, OutputGetter> entries = new HashMap<>();
	
	protected MobApplicableItemTable()
	{
	}
	
	protected MobApplicableItemTable(HashMap<Input, OutputGetter> entries)
	{
		this.entries = entries;
	}

	/**
	 * Create a new builder.
	 * <p> Note: for a new builder, always call any of {@code add()} first, otherwise it will crash.
	 */
	public static MobApplicableItemTable.Builder builder() {
		return new MobApplicableItemTable.Builder();
	}
	
	/** @deprecated use {@code builder()} instead */
	@Deprecated
	public static MobApplicableItemTable.Builder create() {
		return new MobApplicableItemTable.Builder();
	}
	
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	/**
	 *
	 * @param mob
	 * @param stack
	 * @return
	 */
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

	@Nullable
	public OutputGetter getOutputGetter(Mob mob, ItemStack stack)
	{
		for (var input: entries.keySet())
		{
			if (input.test(stack))
				return entries.get(input);
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

	public Map<Input, OutputGetter> getEntriesView() {
		return Map.copyOf(this.entries);
	}

	public static class Builder
	{
		private HashMap<Input, OutputGetter> entries = new HashMap<>();
		private Input buildingActiveEntry = null;
		private DataReader reader = null;

		public MobApplicableItemTable.Builder addRaw(Input in, OutputGetter out)
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
		public MobApplicableItemTable.Builder add(Input input, double amount)
		{
			return addRaw(input, new OutputGetter(amount));
		}
		
		/**
		 * Put a new entry.
		 * @param item Input item.
		 * @param amount Result amount (fixed value).
		 */
		public MobApplicableItemTable.Builder add(Item item, double amount)
		{
			return addRaw(Input.create(item), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(Predicate<ItemStack> predicate, double amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(TagKey<Item> tag, double amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(ResourceLocation key, double amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}

		public MobApplicableItemTable.Builder add(String key, double amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));

		}
		
		public MobApplicableItemTable.Builder add(Item item, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(item), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(Predicate<ItemStack> predicate, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(TagKey<Item> tag, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(ResourceLocation key, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}
		
		public MobApplicableItemTable.Builder add(String key, Function<Mob, Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(amount));
		}

		public MobApplicableItemTable.Builder add(Item item, Supplier<Double> amount)
		{
			return addRaw(Input.create(item), new OutputGetter(mob -> amount.get()));
		}

		public MobApplicableItemTable.Builder add(Predicate<ItemStack> predicate, Supplier<Double> amount)
		{
			return addRaw(Input.create(predicate), new OutputGetter(mob -> amount.get()));
		}

		public MobApplicableItemTable.Builder add(TagKey<Item> tag, Supplier<Double> amount)
		{
			return addRaw(Input.create(tag), new OutputGetter(mob -> amount.get()));
		}

		public MobApplicableItemTable.Builder add(ResourceLocation key, Supplier<Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(mob -> amount.get()));
		}

		public MobApplicableItemTable.Builder add(String key, Supplier<Double> amount)
		{
			return addRaw(Input.create(key), new OutputGetter(mob -> amount.get()));
		}

		public MobApplicableItemTable.Builder addPredicate(@Nonnull Predicate<ItemStack> predicate)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			buildingActiveEntry.addPredicate(predicate);
			return this;
		}

		public MobApplicableItemTable.Builder cooldown(int value)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).cooldown(value);
			return this;
		}
		
		public MobApplicableItemTable.Builder cooldown(@Nonnull Function<Mob, Integer> getter)
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).cooldown(getter);
			return this;
		}
		
		public MobApplicableItemTable.Builder noConsume()
		{
			if (buildingActiveEntry == null)
				throw new UnsupportedOperationException("Illegal operation for empty table. Call add() first!");
			entries.get(buildingActiveEntry).noConsume();
			return this;
		}
		
		public MobApplicableItemTable.Builder extraAction(Consumer<Mob> action)
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
		public MobApplicableItemTable.Builder readData(@Nonnull ResourceLocation loc,
													   @Nonnull BiConsumer<JsonElement, MobApplicableItemTable.Builder> parser)
		{
			this.reader = new DataReader(this, loc, parser);
			return this;
		}

		public MobApplicableItemTable build()
		{
			if (this.reader != null) reader.read();
			MinecraftForge.EVENT_BUS.post(new MobApplicableItemTable.BuildEvent(this));
			return new MobApplicableItemTable(this.entries);
		}
	}
	
	/**
	 * A {@link MobApplicableItemTable.Input} is a <i>check</i> for given {@link ItemStack}.
	 * If the input {@link ItemStack} satisfies a specific {@code Input}, the {@code ItemApplyingToMobTable} will return corresponding {@code OutputGetter}.
	 * <p> It now accepts 4 types of checks: 
	 * <p> a) {@link Item}, to check if the given {@link ItemStack} is this type of {@link Item}.
	 * <p> b) {@link ResourceLocation} of item, browsing {@link Item} from registry and check if the {@link ItemStack} is the found {@link Item}.
	 * It could be optional and can be applied for other mods' items. If the item is not found (e.g. due to the mod not loaded), it will be ignored and won't throw exceptions.
	 * <p> c) {@link TagKey} to check if the given {@link ItemStack} has this tag.
	 * <p> d) {@link Predicate<ItemStack>} to simply check if the given {@link ItemStack} meets the condition.
	 * <p> Note: This object is static and should be embedded in the corresponding {@link MobApplicableItemTable}. When
	 * the item interaction actually happens, the item will be checked by each {@link MobApplicableItemTable.Input} to see
	 * which {@link MobApplicableItemTable.OutputGetter}(s) it should use.
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
		// For getAllItems(). When this input is using a predicate, it may take resource to
		// Iterate the whole registry to find applicable items. So cache it here to prevent repeated testing.
		private List<Item> cachedApplicableItems;

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
				try {
					retval = retval && stackCheck.test(stack);
					valid = true;
					if (!retval) return false;
				} catch (RuntimeException | NoSuchFieldError | NoSuchMethodError e)
				{
					e.printStackTrace();
					return false;
				}
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

		/**
		 * Get a list of all items that should be applicable in this input.
		 * <p>Note: when this input is using a predicate, this method can <i>NOT</i>
		 * recognize items of which the default instance is not applicable but only
		 * applicable when having some kind of NBT.
		 * @param refreshCache When this input is using a predicate, if true, it should iterate
		 *                     the whole item registry each time, which may be resource-costly. Use this only
		 *                     when you suspect the item registry itself may have changed after first calling
		 *                     this method.
		 */
		public List<Item> getAllItems(boolean refreshCache) {
			try {
				if (item != null) return List.of(item);
				else if (key != null)
					return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(key)).map(List::of).orElseGet(List::of);
				else if (tag != null)
					return Optional.ofNullable(ForgeRegistries.ITEMS.tags())
						.map(t -> t.getTag(tag).stream().toList()).orElseGet(List::of);
				else {
					// Skip and don't cache if the item registry is not yet available,
					// so it will always retry for the next time
					if (ForgeRegistries.ITEMS.getValues().isEmpty()) return List.of();
					if (this.cachedApplicableItems == null || refreshCache)
						this.cachedApplicableItems = ForgeRegistries.ITEMS.getValues()
							.stream().filter(item -> this.test(item.getDefaultInstance()))
							.toList();
					return this.cachedApplicableItems;
				}
			} catch (RuntimeException e) {
				return List.of();
			}
		}

		/**
		 * Get a list of all items that should be applicable in this input.
		 * <p>Note: when this input is using a predicate, this method can <i>NOT</i>
		 * recognize items of which the default instance is not applicable but only
		 * applicable when having some kind of NBT.
		 * <p>Note: When this input is using a predicate, this operation will need
		 * to iterate the whole Forge item registry, and it will cache the result on
		 * the first run of {@code getAllItems()} by default to save resource. If you
		 * suspect the Forge item registry itself changed after the first run, use
		 * {@code getAllItems(true)} to refresh the cache.
		 */
		public List<Item> getAllItems() {
			return this.getAllItems(false);
		}
	}

	/**
	 * A {@link MobApplicableItemTable.OutputGetter} describes what should happen if
	 * a specific {@link MobApplicableItemTable.Input} is applied to the mob, including a double "amount" value (can represent
	 * anything you want), usage cooldown ticks, whether the item should be consumed, and extra action to take.
	 * <p>This object should be static and embedded in the corresponding {@link MobApplicableItemTable}.
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

		public boolean isNoConsume() { return noConsume; }

		/**
		 * Get the raw amount source, either a fixed value or a functional getter from mob.
		 */
		public Either<Double, Function<Mob, Double>> getAmountSource() {
			return amountStatic.<Either<Double, Function<Mob, Double>>>map(Either::left)
				.orElseGet(() -> Either.right(amountGetter));
		}

		/**
		 * Get the raw cooldown source, either a fixed value or a functional getter from mob.
		 */
		public Either<Integer, Function<Mob, Integer>> getCooldownSource() {
			return cooldownStatic.<Either<Integer, Function<Mob, Integer>>>map(Either::left)
				.orElseGet(() -> Either.right(cooldownGetter));
		}

		/**
		 * Get the function to generate the cool down ticks.
		 */
		public Function<Mob, Integer> getCooldownGetter() {
			return cooldownStatic.<Function<Mob, Integer>>map(val -> ((Mob mob) -> val))
					.orElse(cooldownGetter);
		}

		/**
		 * Get the function to generate the amount.
		 */
		public Function<Mob, Double> getAmountGetter() {
			return amountStatic.<Function<Mob, Double>>map(val -> ((Mob mob) -> val))
					.orElse(amountGetter);
		}
	}
	
	/**
	 * A {@link MobApplicableItemTable.Output} represents a result when the item is <i>actually</i> applied to the mob.
	 *
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
		public final MobApplicableItemTable.Builder builder;
		
		public BuildEvent(MobApplicableItemTable.Builder builder)
		{
			this.builder = builder;
		}
	}

	private static class DataReader
	{
		private ResourceLocation location;
		private MobApplicableItemTable.Builder builder;
		private BiConsumer<JsonElement, Builder> parser;

		public DataReader(MobApplicableItemTable.Builder builder, ResourceLocation location, BiConsumer<JsonElement, Builder> parser)
		{
			this.location = location;
			this.builder = builder;
			this.parser = parser;
		}

		public void read()
		{
			if (builder == null || location == null) return;
			MinecraftServer server = NFULibrary.getServer();
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
