package net.sodiumzh.nfu.entity.vanillatrade;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.container.ITable2D;
import net.sodiumzh.nfu.container.Table2D;
import net.sodiumzh.nfu.container.Tuple2;
import net.sodiumzh.nfu.math.RandomSelection;
import net.sodiumzh.nfu.util.NFUContainerStatics;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code RandomEnchantmentSelector} provides a set of enchantments for generating random offers of enchantment books.
 */
public class RandomEnchantmentSelector
{
	private final Table2D<Enchantment, Integer, Double> table = new Table2D<>();
	private RandomSelection<Tuple2<Enchantment, Integer>> selector = null;
	private Enchantment lastRegistered = null;
	private boolean built = false;
	
	protected RandomEnchantmentSelector() {};
	
	public RandomEnchantmentSelector add(@Nonnull Enchantment enc, int level, double probabilityWeight)
	{
		if (level <= 0)
			throw new IllegalArgumentException("Non-positive level");
		if (probabilityWeight < 0)
			throw new IllegalArgumentException("Negative weight");
		table.put(enc, level, probabilityWeight);
		lastRegistered = enc;
		return this;
	}
	
	/**
	 * Add with weight == 1.0d.
	 */
	public RandomEnchantmentSelector add(int level, double probabilityWeight)
	{
		if (lastRegistered == null)
			throw new IllegalStateException("enchantment not specified. Use enchantment-specified versions at least once before using omitted versions.");
		return add(lastRegistered, level, probabilityWeight);
	}

	/**
	 * Add with omitted enchantment type - using the latest registered enchantment.
	 * <p>Take care using this in BuildEvent listeners. Do not call this on the first registration operation.
	 */
	public RandomEnchantmentSelector add(int level)
	{
		return add(level, 1.0d);
	}

	public RandomEnchantmentSelector addEnchantment(Enchantment enchantment, Function<Integer, Double> levelWeightMapping) {
		for (int level = 1; level <= enchantment.getMaxLevel(); ++level) {
			this.add(enchantment, level, levelWeightMapping.apply(level));
		}
		return this;
	}

	public RandomEnchantmentSelector addEnchantment(Enchantment enchantment, double weightOverall) {
		return this.addEnchantment(enchantment, i -> weightOverall / enchantment.getMaxLevel());
	}

	public RandomEnchantmentSelector remove(Enchantment enc, int level)
	{
		this.table.remove(enc, level);
		return this;
	}
	
	/**
	 * Finally build the selector. NEVER CALL THIS IN {@code BuildEvent} LISTENERS!!!
	 */
	public RandomEnchantmentSelector build()
	{
		// Stacktrace check to prevent calling from event listener
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement e: stacktrace)
		{
			if (e.getMethodName().contains("getSelector") && e.getClassName().contains("BuildEvent"))
				throw new IllegalStateException("build error - building through event listener is not allowed.");
		}
		if (this.built)
			throw new IllegalStateException("build error - already built.");
		// Post the event, to allow external modification
		MinecraftForge.EVENT_BUS.post(new BuildEvent(this));
		
		if (table.isEmpty())
			throw new IllegalArgumentException("build error - no valid entry available.");
		double weightSum = table.entryStream().map(ITable2D.Entry::value).reduce(0d, Double::sum);
		if (weightSum == 0)
			throw new IllegalArgumentException("build error - no valid entry available.");
		this.selector = new RandomSelection<>(null);
		table.entryStream().forEach(entry -> this.selector.add(Tuple2.of(entry.rowKey(), entry.columnKey()), entry.value() / weightSum));
		built = true;
		return this;
	}
	
	public Tuple2<Enchantment, Integer> select()
	{
		if (!this.built || this.selector == null)
			throw new IllegalStateException("getValue error - not built.");
		for (int i = 0; i < 100; ++i)
		{
			Tuple2<Enchantment, Integer> res = selector.select();
			if (res != null)
				return res;
		}
		throw new RuntimeException("getValue error - failed to get a valid result after 100 tests. Usually failure shouldn't happen and this "
				+ "indicates an internal error.");
	}
	
	public static class BuildEvent extends Event
	{
		private final RandomEnchantmentSelector selector;
		public BuildEvent(RandomEnchantmentSelector s)
		{
			this.selector = s;
		}
		public RandomEnchantmentSelector getSelector()
		{
			return selector;
		}
	}

	private RandomEnchantmentSelector readData(ResourceLocation location)
	{
		MinecraftServer server = NFULibrary.getServer();
		if (server == null) return this;
		ResourceManager mgr = server.getResourceManager();
		List<Resource> resources = mgr.getResourceStack(location);
		for (Resource r: resources)
		{
			try {
				InputStream input = r.open();
				Reader reader = new InputStreamReader(input);
				JsonElement json = JsonParser.parseReader(reader);
				this.readSingleJson(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	private void readSingleJson(JsonElement json) {
		for (JsonElement element : json.getAsJsonArray()) {
			try {
				JsonObject jo = element.getAsJsonObject();
				Enchantment enc = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(jo.get("enchantment").getAsString()));
				if (enc == null) continue;

				int[] levels;
				JsonElement levelJson = jo.get("level");
				if (levelJson == null) levels = NFUContainerStatics.intRangeArray(enc.getMinLevel(), enc.getMaxLevel(), 1);
				else if (levelJson.isJsonPrimitive()) levels = new int[] { levelJson.getAsInt() };
				else if (levelJson.isJsonArray())
				{
					levels = new int[levelJson.getAsJsonArray().size()];
					for (int i = 0; i < levels.length; ++i) {
						levels[i] = levelJson.getAsJsonArray().get(i).getAsInt();
					}
				}
				else throw new JsonParseException("invalid level");

				double[] weights = new double[levels.length];
				JsonElement weightsJson = jo.get("level");
				if (weightsJson == null) Arrays.fill(weights, 1d);
				else if (weightsJson.isJsonPrimitive())  Arrays.fill(weights, levelJson.getAsInt());
				else if (weightsJson.isJsonArray() && weightsJson.getAsJsonArray().size() == levels.length)
				{
					for (int i = 0; i < weights.length; ++i) {
						weights[i] = weightsJson.getAsJsonArray().get(i).getAsInt();
					}
				}
				else throw new JsonParseException("invalid weights or weight array length");

				for (int i = 0; i < levels.length; ++i)
				{
					this.add(enc, levels[i], weights[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static RandomEnchantmentSelector allEnchantments(
		boolean includeTreasure,
		boolean includeNonTreasure,
		boolean includeCurse,
		boolean includeNonCurse,
		double treasureWeightScale,
		Function<Integer, Double> levelWeightDistribution
	) {
		if (!includeTreasure && !includeNonTreasure)
			throw new IllegalArgumentException("RandomEnchantmentSelector#allEnchantments: must include either treasure or non-treasure.");
		if (!includeCurse && !includeNonCurse)
			throw new IllegalArgumentException("RandomEnchantmentSelector#allEnchantments: must include either curse or non-curse.");
		RandomEnchantmentSelector res = new RandomEnchantmentSelector();
		Stream<Enchantment> allEnc = ForgeRegistries.ENCHANTMENTS.getValues().stream();
		if (!includeTreasure)
			allEnc = allEnc.filter(enc -> !enc.isTreasureOnly());
		if (!includeNonTreasure)
			allEnc = allEnc.filter(Enchantment::isTreasureOnly);
		if (!includeCurse)
			allEnc = allEnc.filter(enc -> !enc.isCurse());
		if (!includeNonCurse)
			allEnc = allEnc.filter(Enchantment::isCurse);
		allEnc.forEach(enc -> {
			double[] weights = IntStream.range(1, enc.getMaxLevel() + 1)
				.mapToDouble(levelWeightDistribution::apply).toArray();
			double weightSum = DoubleStream.of(weights).sum();
			for (int i = 0; i < weights.length; ++i) {
				weights[i] /= weightSum;
				if (enc.isTreasureOnly())
					weights[i] *= treasureWeightScale;
				res.add(enc, i + 1, weights[i]);
			}
		});
		res.build();
		return res;
	}

	public ITable2D<Enchantment, Integer, Double> getTableSnapshot() {
		return ITable2D.snapshotOf(this.table);
	}

}
