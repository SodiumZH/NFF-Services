package net.sodiumzh.nautils.entity.vanillatrade;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.event.CellEditorListener;

import com.google.gson.*;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.containers.Tuple3;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;

public class VanillaTradeRegistry extends AbstractVanillaTradeRegistry<VanillaTradeListing>
{
	private ItemStack currency = Items.EMERALD.getDefaultInstance();	// Used on calling buys(), sells() and transforms().
	private boolean randomizeUsesPoisson = false;	// If true, the randomization should use Poisson distribution.
	private double poissonFactor = 0.5d;
	@Nonnull private String globalDefaultDataPath = "vanilla_trade_listings";

	// Registration
	
	public Registering push(@Nonnull ResourceLocation key)
	{
		if (!this.getRaw().containsKey(key))
			this.getRaw().put(key, new HashMap<>());
		return new Registering(this, key);
	}
	
	public Registering push(@Nonnull String key)
	{
		return this.push(new ResourceLocation(key));
	}
	
	public Registering push(@Nonnull EntityType<?> key)
	{
		return this.push(ForgeRegistries.ENTITY_TYPES.getKey(key));
	}
	
	public Registering push(@Nonnull RegistryObject<EntityType<?>> key)
	{
		return this.push(key.get());
	}
	
	public VanillaTradeRegistry setCurrency(ItemStack currency)
	{
		if (currency == null || currency.isEmpty())
			throw new IllegalArgumentException("currency requires non-null & non-empty.");
		this.currency = currency;
		return this;
	}
	
	public VanillaTradeRegistry setCurrency(Item currency)
	{
		return this.setCurrency(getNonnullInstance(currency));
	}
	
	/**
	 * Set if the random count selection should use Poisson distribution. False(default) = uniform distribution.
	 * <p>If this value is set, the added listings will apply the corresponding distribution by default. If different distributions
	 * must be used in a single listing, you have to use raw {@code addListing} which requires a constructed listing input.
	 */
	public VanillaTradeRegistry setRandomizationDistribution(boolean usesPoisson)
	{
		this.randomizeUsesPoisson = usesPoisson;
		return this;
	}
	
	public VanillaTradeRegistry setPoissonFactor(double value)
	{
		this.poissonFactor = value;
		return this;
	}

	public void setGlobalDataPath(@Nonnull String globalDefaultDataPath) {
		this.globalDefaultDataPath = globalDefaultDataPath;
	}

	public static class Registering
	{
		private VanillaTradeRegistry registry;
		private ResourceLocation key;
		private VillagerProfession profession = VillagerProfession.NONE;
		private int level = 1;
		private VanillaTradeListing lastListing = null;
		private Optional<String> defaultDataPath = Optional.empty();

		private Registering(VanillaTradeRegistry registry, ResourceLocation key)
		{
			this.registry = registry;
			this.key = key;
		}
		
		public Registering setRequiredLevel(int level)
		{
			if (level <= 0)
				throw new IllegalArgumentException(String.format("AbstractVanillaTradeRegistry#Registering: level starts from 1. Input: %d", level));
			this.level = level;
			return this;
		}
		
		public Registering setProfession(@Nullable VillagerProfession profession)
		{
			if (profession == null)
				profession = VillagerProfession.NONE;
			this.profession = profession;
			return this;
		}
		
		public Registering setCurrency(ItemStack currency)
		{
			this.registry.setCurrency(currency);
			return this;
		}
		
		public Registering setCurrency(Item currency)
		{
			this.registry.setCurrency(currency);
			return this;
		}
		
		public Registering setRandomizationDistribution(boolean value)
		{
			this.registry.setRandomizationDistribution(value);
			return this;
		}
		
		public Registering setPoissonFactor(double value)
		{
			this.registry.setPoissonFactor(value);
			return this;
		}
		
		@Nonnull
		private VanillaTradeListings<VanillaTradeListing> getActiveListings()
		{
			if (!this.registry.getRaw().get(this.key).containsKey(this.profession))
				this.registry.getRaw().get(this.key).put(this.profession, new VanillaTradeListings<>());
			return this.registry.getRaw().get(this.key).get(this.profession);
		}
		
		private ItemStack getCurrency()
		{
			return this.registry.currency;
		}
		
		private boolean usesPoisson()
		{
			return this.registry.randomizeUsesPoisson;
		}
		
		private double getPoissonFactor()
		{
			return this.registry.poissonFactor;
		}
		
		public Registering addListing(VanillaTradeListing listing)
		{
			this.getActiveListings().add(listing.setRequiredLevel(this.level));
			this.lastListing = listing;
			return this;
		}
		
		public Registering linkListings(@Nonnull ResourceLocation key, @Nullable VillagerProfession profession)
		{
			if (profession == null) profession = VillagerProfession.NONE;
			if (this.registry.hasListings(key, profession))
				this.getActiveListings().linkExternal(this.registry.getListings(key, profession));
			else throw new IllegalArgumentException(String.format("AbstractVanillaTradeRegistry#Registering#linkListing: input key doesn't exist. Input: key = %s, profession = %s", key.toString(), profession.toString()));
			return this;
		}
		
		public Registering linkListings(@Nonnull String key, @Nullable VillagerProfession profession)
		{
			return this.linkListings(new ResourceLocation(key), profession);
		}
		
		public Registering linkListings(@Nonnull EntityType<?> type, @Nullable VillagerProfession profession)
		{
			return this.linkListings(ForgeRegistries.ENTITY_TYPES.getKey(type), profession);
		}
		
		public Registering linkListings(@Nonnull ResourceLocation key)
		{
			return this.linkListings(key, null);
		}
		
		public Registering linkListings(@Nonnull String key)
		{
			return this.linkListings(key, null);
		}
		
		public Registering linkListings(@Nonnull EntityType<?> type)
		{
			return this.linkListings(type, null);
		}
		
		public Registering mergeListings(@Nonnull ResourceLocation key, @Nullable VillagerProfession profession)
		{
			if (profession == null) profession = VillagerProfession.NONE;
			if (this.registry.hasListings(key, profession))
				this.getActiveListings().addAll(this.registry.getListings(key, profession).getValidSet());
			else throw new IllegalArgumentException(String.format("AbstractVanillaTradeRegistry#Registering#mergeListing: input key doesn't exist. Input: key = %s, profession = %s", key.toString(), profession.toString()));
			return this;
		}
		
		public Registering mergeListings(@Nonnull String key, @Nullable VillagerProfession profession)
		{
			return this.mergeListings(new ResourceLocation(key), profession);
		}
		
		public Registering mergeListings(@Nonnull EntityType<?> type, @Nullable VillagerProfession profession)
		{
			return this.mergeListings(ForgeRegistries.ENTITY_TYPES.getKey(type), profession);
		}
	
		public Registering mergeListings(@Nonnull ResourceLocation key)
		{
			return this.mergeListings(key, null);
		}
		
		public Registering mergeListings(@Nonnull String key)
		{
			return this.mergeListings(key, null);
		}
		
		public Registering mergeListings(@Nonnull EntityType<?> type)
		{
			return this.mergeListings(type, null);
		}
		
		/**
		 * Add an generic exchanging listing.
		 */
		public Registering addExchanges(ItemStack costA, int costAMin, int costAMax, @Nullable ItemStack costB, int costBMin, int costBMax,
				ItemStack result, int resultMin, int resultMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.create(costA, result).setACountRange(costAMin, costAMax).addB(costB)
					.setBCountRange(costBMin, costBMax).setResultCountRange(resultMin, resultMax).setMaxUses(maxUses);
			if (this.usesPoisson())
				l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}
		
		/**
		 * Add an generic exchanging listing.
		 */
		public Registering addExchanges(Item costA, int costAMin, int costAMax, @Nullable Item costB, int costBMin, int costBMax,
				Item result, int resultMin, int resultMax, int maxUses)
		{
			return this.addExchanges(getNonnullInstance(costA), costAMin, costAMax, costB == null ? ItemStack.EMPTY : getNonnullInstance(costB),
					costBMin, costBMax, getNonnullInstance(result), resultMin, resultMax, maxUses);
		}
		
		/**
		 * Add an generic exchanging listing without costB.
		 */
		public Registering addExchanges(ItemStack costA, int costAMin, int costAMax, ItemStack result, int resultMin, int resultMax, int maxUses)
		{
			return this.addExchanges(costA, costAMin, costAMax, null, 1, 1, result, resultMin, resultMax, maxUses);
		}
		
		/**
		 * Add an generic exchanging listing without costB.
		 */
		public Registering addExchanges(Item costA, int costAMin, int costAMax, Item result, int resultMin, int resultMax, int maxUses)
		{
			return this.addExchanges(costA, costAMin, costAMax, null, 1, 1, result, resultMin, resultMax, maxUses);
		}
		
		/**
		 * Add a listing that the mob buys item from player with currency.
		 */
		public Registering addBuys(ItemStack buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
		{
			return this.addExchanges(buys, buysMin, buysMax, this.getCurrency(), priceMin, priceMax, maxUses);
		}
		
		/**
		 * Add a listing that the mob buys item from player with currency.
		 */
		public Registering addBuys(Item buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
		{
			return this.addExchanges(getNonnullInstance(buys), buysMin, buysMax, this.getCurrency(), priceMin, priceMax, maxUses);
		}
		
		/**
		 * Add a listing that the mob buys one random item from the buys list from player with currency.
		 */
		public Registering addBuys(ItemStack[] buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(buysMin, buysMax, priceMin, priceMax).addA(buys).addResult(this.getCurrency()).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}
		
		/**
		 * Add a listing that the mob buys one random item from the buys list from player with currency.
		 */
		public Registering addBuys(Item[] buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(buysMin, buysMax, priceMin, priceMax).addA(buys).addResult(this.getCurrency()).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}
		
		/**
		 * Add a listing that the mob sells item to player with currency.
		 */
		public Registering addSells(int priceMin, int priceMax, ItemStack sells, int sellsMin, int sellsMax, int maxUses)
		{
			return this.addExchanges(this.getCurrency(), priceMin, priceMax, sells, sellsMin, sellsMax, maxUses);
		}
		
		/**
		 * Add a listing that the mob sells item to player with currency.
		 */
		public Registering addSells(int priceMin, int priceMax, Item sells, int sellsMin, int sellsMax, int maxUses)
		{
			return this.addExchanges(this.getCurrency(), priceMin, priceMax, getNonnullInstance(sells), sellsMin, sellsMax, maxUses);
		}
		
		/**
		 * Add a listing that the mob sells one random item from the sells list to player with currency.
		 */
		public Registering addSells(int priceMin, int priceMax, ItemStack[] sells, int sellsMin, int sellsMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(priceMin, priceMax, sellsMin, sellsMax)
					.addA(this.getCurrency()).addResult(sells).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}

		/**
		 * Add a listing that the mob sells one random item from the sells list to player with currency.
		 */
		public Registering addSells(int priceMin, int priceMax, Item[] sells, int sellsMin, int sellsMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(priceMin, priceMax, sellsMin, sellsMax)
					.addA(this.getCurrency()).addResult(sells).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}
		
		public Registering addSellsEnchantmentBook(int priceMin, int priceMax, Enchantment enc, int lvl, int maxUses)
		{
			ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
			VanillaTradeListing l = new VanillaTradeListingEnchanted(enc, lvl).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
					.addA(this.getCurrency()).addB(Items.BOOK).addResult(book).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}

		@Deprecated
		public Registering addSellsEnchantmentBook(int priceMin, int priceMax, RandomEnchantmentSelector enchantmentSelector, int maxUses)
		{
			ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
			VanillaTradeListing l = new VanillaTradeListingEnchanted(enchantmentSelector).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
					.addA(this.getCurrency()).addResult(book).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}

		public Registering addEnchantsBook(int priceMin, int priceMax, Enchantment enc, int lvl, int maxUses)
		{
			ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
			VanillaTradeListing l = new VanillaTradeListingEnchanted(enc, lvl).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
					.addA(this.getCurrency()).addB(Items.BOOK).addResult(book).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}

		public Registering addEnchantsBook(int priceMin, int priceMax, RandomEnchantmentSelector enchantmentSelector, int maxUses)
		{
			ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
			VanillaTradeListing l = new VanillaTradeListingEnchanted(enchantmentSelector).addB(Items.BOOK.getDefaultInstance())
					.setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
					.addA(this.getCurrency()).addResult(book).setMaxUses(maxUses);
			if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}

		/**
		 * Add a listing that the mob receives some cost ({@code extraCost}) and converts some amount of an item to another.
		 * (e.g. vanilla paid cooking)
		 */
		public Registering addConverts(ItemStack extraCost, int costMin, int costMax, ItemStack from, ItemStack to, int convertsMin, int convertsMax, int maxUses)
		{
			VanillaTradeListing l = VanillaTradeListing.converts(extraCost, costMin, costMax, from, to, convertsMin, convertsMax).setMaxUses(maxUses);
			if (this.usesPoisson())
				l.setAllPoisson(this.getPoissonFactor());
			this.addListing(l);
			return this;
		}
		
		/**
		 * Add a listing that mobs receives some cost ({@code extraCost}) and converts some amount of an item to another.
		 * (e.g. vanilla paid cooking)
		 */
		public Registering addConverts(Item extraCost, int costMin, int costMax, Item from, Item to, int convertsMin, int convertsMax, int maxUses)
		{
			return this.addConverts(getNonnullInstance(extraCost), costMin, costMax, 
					getNonnullInstance(from), getNonnullInstance(to), convertsMin, convertsMax, maxUses);
		}
		
		/**
		 * Add a listing that mobs receives currency and converts some amount of an item to another.
		 * (e.g. vanilla paid cooking)
		 */
		public Registering addConverts(int costMin, int costMax, ItemStack from, ItemStack to, int convertsMin, int convertsMax, int maxUses)
		{
			return this.addConverts(this.getCurrency(), costMin, costMax, from, to, convertsMin, convertsMax, maxUses);
		}
		
		/**
		 * Add a listing that mobs receives currency and converts some amount of an item to another.
		 * (e.g. vanilla paid cooking)
		 */
		public Registering addConverts(int costMin, int costMax, Item from, Item to, int convertsMin, int convertsMax, int maxUses)
		{
			return this.addConverts(costMin, costMax, getNonnullInstance(from), getNonnullInstance(to), convertsMin, convertsMax, maxUses);
		}
		
		/**
		 * Set selection weight of the last added listing.
		 */
		public Registering weight(double value)
		{
			if (this.lastListing != null)
				this.lastListing.setSelectionWeight(value);
			else LogUtils.getLogger().error("VanillaTradeRegistry#Registering#weight: no listing registered. Skipped.");
			return this;
		}

		/**
		 * Set selection weight of the last added listing.
		 */
		public Registering maxUses(int value)
		{
			if (this.lastListing != null)
				this.lastListing.setMaxUses(value);
			else LogUtils.getLogger().error("VanillaTradeRegistry#Registering#maxUses: no listing registered. Skipped.");
			return this;
		}

		/**
		 * Set data reading default path for this registry key.
		 * <p>Note: This method only sets the data path of this registry entry i.e. works only before next call of {@code pop()}.
		 * To set global default data path for the whole registry, use {@code globalDataPath()}.
		 * <p>Note: If this value is valid, it will prioritize this value. If the default path for this entry is null,
		 * uses global one.
		 * @param path New path. If this path input is {@code "path"}, registering resource location is {@code "some_mod:some_entity_type"},
		 *             then the data reading path will be: {@code "some_mod:path/some_entity_type.json"}. Default value is {@code "vanilla_trade_listings"}.
		 *             {@code null} means using global default path.
		 *
		 */
		public Registering dataPath(@Nullable String path)
		{
			this.defaultDataPath = Optional.ofNullable(path);
			return this;
		}

		/**
		 * Set data reading default path for the whole registry.
		 * <p>Note: If the default path is valid for this registry entry (i.e. {@code dataPath} is ever called with non-null input),
		 * this global path will be overriden. To apply this value, call {@code dataPath(null)}.
		 * @param path New path. If this path input is {@code "path"}, registering resource location is {@code "some_mod:some_entity_type"},
		 *             then the data reading path will be: {@code "some_mod:path/some_entity_type.json"}. Default value is {@code "vanilla_trade_listings"}.
		 *             {@code null} means using global default path.
		 */
		public Registering globalDataPath(@Nonnull String path)
		{
			this.defaultDataPath = Optional.ofNullable(path);
			return this;
		}

		private ResourceLocation getDefaultDataPath()
		{
			String path = this.defaultDataPath.orElse(this.registry.globalDefaultDataPath);
			return new ResourceLocation(this.key.getNamespace(), String.format("%s/%s.json", path, this.key.getPath()));
		}

		/**
		 * End registering for this key and return to the registry. No data reading.
		 * <p>If you need data reading, use a version of {@code readAndPop} to end instead, or call
		 * a version of {@code readData} before {@code pop()}.
		 */
		public VanillaTradeRegistry pop()
		{
			return this.registry;
		}

		/**
		 * End registering for this key, <b>read data of default path</b> and return to the registry.
		 * See {@code dataPath(String)}.
		 */
		public VanillaTradeRegistry readAndPop()
		{
			this.readData();
			return this.pop();
		}

		/**
		 * End registering for this key, <b>read data of given path</b> and return to the registry.
 		 * Note: if input is {@code "path"}, current registering key is {@code "some_mod:some_key"},
		 * then it will read data {@code "some_mod:path/some_key.json"}.
		 * <p>If you need to specify the full data path, use {@code readData(yourPath).pop()} to finalize.
		 */
		public VanillaTradeRegistry readAndPop(String dataPath)
		{
			Optional<String> oldPath = this.defaultDataPath;
			this.dataPath(dataPath);
			this.readData();
			this.dataPath(oldPath.orElse(null));
			return this.pop();
		}

		public Registering readData(ResourceLocation location)
		{
			MinecraftServer server = NaUtils.getServer();
			if (server == null) return this;
			ResourceManager mgr = server.getResourceManager();
			List<Resource> resources = mgr.getResourceStack(location);
			for (Resource r: resources)
			{
				try {
					InputStream input = r.open();
					Reader reader = new InputStreamReader(input);
					JsonElement json = JsonParser.parseReader(reader);

					// Don't make settings change after reading json
					// Record settings
					boolean usesPoisson = this.registry.randomizeUsesPoisson;
					double poissonFactor = this.registry.poissonFactor;
					ItemStack currency = this.registry.currency;
					int level = this.level;
					VillagerProfession prof = this.profession;

					// Read. Settings may be randomly changed during reading.
					this.setRequiredLevel(1);	// In json it's 1 by default
					this.readSingleJson(json, new Tuple3<>(usesPoisson, poissonFactor, currency), NaUtilsConfigs.CACHED_DEBUG_MODE);

					// Set back, don't let json reading change the settings
					this.setRandomizationDistribution(usesPoisson);
					this.setPoissonFactor(poissonFactor);
					this.setCurrency(currency);
					this.setRequiredLevel(level);
					this.profession = prof;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return this;
		}

		/**
		 * Read data under default directory. If the current registering
		 * entity type is {@code "some_mod:some_entity"}, then the data path will be
		 * {@code "some_mod:{default_path}/some_entity.json"}.
		 * <p> The default path can be set by calling {@code dataPath()}. Default is {"vanilla_trade_listings"}.
		 */
		public Registering readData()
		{
			return this.readData(this.getDefaultDataPath());
		}

		/**
		 * A wrapped data reading option that should read an extra optional {@code "currency"} field and temporarily set currency.
		 */
		private Consumer<JsonObject> withCurrencyOption(Consumer<JsonObject> action) {
			return (JsonObject jo) ->
			{
				boolean withCurrency = jo.has("currency");
				ItemStack oldCurrency = this.getCurrency();
				if (withCurrency) {
					ItemStack[] currency = readItem(jo.get("currency"), false);
					if (currency.length != 1 || currency[0] == null || currency[0].isEmpty()) {
						LogUtils.getLogger().error("VanillaTradeRegistry#readData set currency failed: missing or duplicate item. Set to Emerald.");
						currency[0] = Items.EMERALD.getDefaultInstance();
					}
					this.setCurrency(currency[0]);
				}
				action.accept(jo);
				if (withCurrency)
					this.setCurrency(oldCurrency);
			};
		}

		private void readSingleJson(JsonElement json, Tuple3<Boolean, Double, ItemStack> defaultSettings, boolean debug)
		{
			try {
				for (JsonElement element: json.getAsJsonArray())
				{
					try {
						// "action" field defines what this entry stands for
						JsonObject jo = element.getAsJsonObject();
						String action = jo.get("action").getAsString();
						switch (action)
						{
							// Setting modifications
							case "settings" : {	// Set currency
								if (jo.has("currency")) {
									ItemStack[] item = readItem(jo.get("currency"), false);
									if (item.length != 1 || item[0] == null || item[0].isEmpty()) {
										LogUtils.getLogger().error("VanillaTradeRegistry#readData set currency failed: missing or duplicate item. Set to Emerald.");
										item[0] = Items.EMERALD.getDefaultInstance();
									}
									this.setCurrency(item[0]);
								}
								if (jo.has("poisson"))
									this.setRandomizationDistribution(jo.get("poisson").getAsBoolean());
								if (jo.has("p"))
									this.setPoissonFactor(jo.get("p").getAsDouble());
								if (jo.has("level"))
									this.setRequiredLevel(jo.get("level").getAsInt());
								if (jo.has("profession"))
									this.setProfession(ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("profession")));
								break;
							}
							case "reset" : {
								this.setCurrency(defaultSettings.c);
								this.setRandomizationDistribution(defaultSettings.a);
								this.setPoissonFactor(defaultSettings.b);
								break;
							}
							case "link": {
								String target = jo.get("target").getAsString();
								if (jo.has("profession"))
									this.linkListings(new ResourceLocation(target),
											ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(jo.get("profession").getAsString())));
								else this.linkListings(new ResourceLocation(target));
								break;
							}
							// Trade entry definitions
							default : {
								this.withCurrencyOption(jsonObject -> {
									switch (action) {
										case "buy": {
											ItemStack[] buys = readItem(jsonObject.get("item"), true);
											int[] price = readAmountRange(jsonObject.get("price"));
											int[] amount = readAmountRange(jsonObject.get("amount"));
											int maxUses = jsonObject.has("maxUses") ? jsonObject.get("maxUses").getAsInt() : 12;
											this.addBuys(buys, amount[0], amount[1], price[0], price[1], maxUses);
											if (jsonObject.has("weight")) this.weight(jsonObject.get("weight").getAsDouble());
											break;
										}
										case "sell": {
											ItemStack[] sells = readItem(jsonObject.get("item"), true);
											int[] price = readAmountRange(jsonObject.get("price"));
											int[] amount = readAmountRange(jsonObject.get("amount"));
											int maxUses = jsonObject.has("maxUses") ? jsonObject.get("maxUses").getAsInt() : 12;
											this.addSells(price[0], price[1], sells, amount[0], amount[1], maxUses);
											if (jsonObject.has("weight")) this.weight(jsonObject.get("weight").getAsDouble());
											break;
										}
										case "convert": {
											ItemStack[] from = readItem(jsonObject.get("item"), true);
											ItemStack[] to = readItem(jsonObject.get("result"), true);
											if (from.length != 1 || to.length != 1)
												throw new UnsupportedOperationException("VanillaTradeRegistry converting doesn't support multi-item.");
											int[] price = readAmountRange(jsonObject.get("price"));
											int[] amount = readAmountRange(jsonObject.get("amount"));
											int maxUses = jsonObject.has("maxUses") ? jsonObject.get("maxUses").getAsInt() : 12;
											this.addConverts(price[0], price[1], from[0], to[0], amount[0], amount[1], maxUses);
											if (jsonObject.has("weight")) this.weight(jsonObject.get("weight").getAsDouble());
											break;
										}
										case "enchantmentBook": {
											int[] price = readAmountRange(jsonObject.get("price"));
											var enchantment = readEnchantment(jsonObject.get("enchantment"));
											int maxUses = jsonObject.has("maxUses") ? jsonObject.get("maxUses").getAsInt() : 12;
											if (enchantment != null) {
												this.addEnchantsBook(price[0], price[1], enchantment.getA(), enchantment.getB(), maxUses);
												if (jsonObject.has("weight")) this.weight(jsonObject.get("weight").getAsDouble());
											}
											break;
										}
										default: break;
									}
								}).accept(jo);
								break;
							}

						}
					} catch (Exception | NoSuchMethodError | NoSuchFieldError e) {
						if (debug) e.printStackTrace();
					}
				}
			} catch (Exception e){
				if (debug) e.printStackTrace();
			}
		}

		/**
		 * Read ItemStack info from a JsonElement. Supports 3 formats: Primitive - {@link Item} key;
		 * JsonObject - {@link ItemStack}; JsonArray - multiple {@link ItemStack}s.
		 * <p>It will never return zero-length output. If empty, it will return {@code new ItemStack[1] {ItemStack.EMPTY}}.
		 * @param element JsonElement.
		 * @param allowsArray Whether allows array format. If false, it will always output {@code ItemStack[1]}.
		 * @return
		 */
		private static ItemStack[] readItem(JsonElement element, boolean allowsArray) {
			try {
				// Case of a single item type
				if (element.isJsonPrimitive()) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(element.getAsString()));
					return new ItemStack[]{(item == null || item == Items.AIR) ? ItemStack.EMPTY : item.getDefaultInstance()};
				}
				// Case of an ItemStack representation of Forge format
				else if (element.isJsonObject()) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(element.getAsJsonObject().get("item").getAsString()));
					return new ItemStack[]{(item == null || item == Items.AIR) ?
						ItemStack.EMPTY :
						CraftingHelper.getItemStack(element.getAsJsonObject(), true, true)};
				}
				// Case of an array, recursively read each
				else if (element.isJsonArray()) {
					if (allowsArray) {
						int size = element.getAsJsonArray().size();
						if (size == 0) return new ItemStack[]{ItemStack.EMPTY};    // Always prevent 0-length output
						ItemStack[] res = new ItemStack[size];
						for (int i = 0; i < size; ++i) {
							JsonElement e = element.getAsJsonArray().get(i);
							try {
								if (e.isJsonArray())
									throw new JsonParseException("VanillaTradeRegistry#readItem doesn't allow nested arrays.");
								res[i] = readItem(e, false)[0];
							} catch (Exception exception) {
								exception.printStackTrace();
								res[i] = ItemStack.EMPTY;
							}
						}
						return res;
					} else
						throw new JsonParseException("VanillaTradeRegistry#readItem: JsonArray detected, but not allowed.");
				} else throw new JsonParseException("Read item failed.");
			} catch (Exception e) {
				e.printStackTrace();
				return new ItemStack[] {ItemStack.EMPTY};
			}
        }

		private static int[] readAmountRange(JsonElement element) {
			if (element == null)
				return new int[]{1, 1};

			if (element.isJsonPrimitive())
				return new int[] {element.getAsInt(), element.getAsInt()};
			else if (element.isJsonArray())
			{
				JsonArray array = element.getAsJsonArray();
				switch (array.size()) {
					case 1:
						return new int[]{array.get(0).getAsInt(), array.get(0).getAsInt()};
					case 2:
						return new int[]{array.get(0).getAsInt(), array.get(1).getAsInt()};
				}
			}
			throw new JsonParseException("invalid amount range");
		}

		@Nullable
		private static Tuple<Enchantment, Integer> readEnchantment(JsonElement element)
		{
			if (element == null) return null;
			if (element.isJsonObject())
			{
				Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(element.getAsJsonObject().get("key").getAsString()));
				if (e == null) return null;
				int lv = element.getAsJsonObject().has("level") ?
						element.getAsJsonObject().get("level").getAsInt() : e.getMaxLevel();
				return new Tuple<>(e, lv);
			}
			else throw new JsonParseException("invalid enchantment");
		}
	}
	
	// Utilities

	@Nonnull
	private static ItemStack getNonnullInstance(@Nullable Item item)
	{
		return item != null ? item.getDefaultInstance() : ItemStack.EMPTY;
	}


}
