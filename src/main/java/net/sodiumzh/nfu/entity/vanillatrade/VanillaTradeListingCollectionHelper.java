package net.sodiumzh.nfu.entity.vanillatrade;

import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.container.Tuple3;
import net.sodiumzh.nfu.container.Tuple4;
import net.sodiumzh.nfu.registry.NFUConfigs;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.util.NFUDataStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.OpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class for constructing {@link VanillaTradeListingCollection}, specific for default 
 * listing format (i.e.{@link VanillaTradeListing}).
 * <p>It's recommended to use this helper only in the lambda supplier of {@link NFURegistries#VANILLA_TRADE_LISTING_COLLECTIONS}
 * entries.
 */
public class VanillaTradeListingCollectionHelper {

    private final VanillaTradeListingCollection<VanillaTradeListing> collection;
    private ResourceLocation key;
    private VillagerProfession profession = VillagerProfession.NONE;
    private int level = 1;
    private VanillaTradeListing lastListing = null;
    private final Optional<String> defaultDataPath = Optional.empty();
    private ItemStack currency = new ItemStack(Items.EMERALD, 1);
    private boolean randomizeUsesPoisson = false;	// If true, the randomization should use Poisson distribution.
    private double poissonFactor = 0.5d;
    @Nullable
    private ResourceLocation dataPath = null;

    private VanillaTradeListingCollectionHelper(VanillaTradeListingCollection<VanillaTradeListing> forCollection) {
        this.collection = forCollection;
        this.collection.helperCount += 1;
        if (this.collection.getHelperCreationCount() == 1)
            MinecraftForge.EVENT_BUS.post(new VanillaTradeListingCollectionHelperEvent(this, this.collection));
    }

    public static VanillaTradeListingCollectionHelper newCollection() {
        return new VanillaTradeListingCollectionHelper(new VanillaTradeListingCollection<>());
    }

    public static VanillaTradeListingCollectionHelper forCollection(VanillaTradeListingCollection<VanillaTradeListing> collection) {
        return new VanillaTradeListingCollectionHelper(collection);
    }

    /**
     * Get how many helpers have been created for the collection (including this).
     */
    public int getHelperCount() {
        return this.collection.getHelperCreationCount();
    }

    public VanillaTradeListingCollectionHelper setRequiredLevel(int level)
    {
        if (level <= 0)
            throw new IllegalArgumentException(String.format("VanillaTradeListingCollectionHelper: level starts from 1. Input: %d", level));
        this.level = level;
        return this;
    }

    public VanillaTradeListingCollection<VanillaTradeListing> get() {
        return collection;
    }

    public VanillaTradeListingCollectionHelper setCurrency(ItemStack currency)
    {
        this.currency = currency.copy();
        this.currency.setCount(1);
        return this;
    }

    public VanillaTradeListingCollectionHelper setCurrency(Item currency)
    {
        this.setCurrency(currency.getDefaultInstance());
        return this;
    }

    public VanillaTradeListingCollectionHelper setRandomizationUsesPoisson(boolean value)
    {
        this.randomizeUsesPoisson = value;
        return this;
    }

    public VanillaTradeListingCollectionHelper setPoissonFactor(double value)
    {
        assert(value >= 0 && value <= 1);
        this.poissonFactor = value;
        return this;
    }

    public VanillaTradeListingCollectionHelper setLevel(int value) {
        assert(value > 0);
        this.level = value;
        return this;
    }

    public ItemStack getCurrency()
    {
        return this.currency;
    }

    public boolean usesPoisson()
    {
        return this.randomizeUsesPoisson;
    }

    public double getPoissonFactor()
    {
        return this.poissonFactor;
    }

    public VanillaTradeListingCollectionHelper addListing(int merchantLevel, VanillaTradeListing listing)
    {
        this.collection.add(merchantLevel, listing);
        this.lastListing = listing;
        return this;
    }

    public VanillaTradeListingCollectionHelper addListing(VanillaTradeListing listing)
    {
        return this.addListing(listing.getDefaultRequiredLevel(), listing);
    }

    public VanillaTradeListingCollectionHelper attach(VanillaTradeListingCollection<? extends VanillaTradeListing> other)
    {
        this.collection.table.attach(other.table);
        return this;
    }

    public VanillaTradeListingCollectionHelper attach(Multimap<Integer, ? extends VanillaTradeListing> other)
    {
        this.collection.table.attach(other);
        return this;
    }

    public VanillaTradeListingCollectionHelper attach(Map<Integer, ? extends Collection<? extends VanillaTradeListing>> other)
    {
        this.collection.table.attach(other);
        return this;
    }

    public VanillaTradeListingCollectionHelper addCopy(VanillaTradeListingCollection<? extends VanillaTradeListing> other)
    {
        this.collection.table.putAll(other.table);
        return this;
    }

    public VanillaTradeListingCollectionHelper addCopy(Multimap<Integer, ? extends VanillaTradeListing> other)
    {
        this.collection.table.putAll(other);
        return this;
    }

    public VanillaTradeListingCollectionHelper addCopy(Map<Integer, ? extends Collection<? extends VanillaTradeListing>> other)
    {
        this.collection.table.putAll(other);
        return this;
    }

    /**
     * Add an generic exchanging listing.
     */
    public VanillaTradeListingCollectionHelper addExchanges(
            ItemStack costA, int costAMin, int costAMax, @Nullable ItemStack costB, int costBMin, int costBMax,
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
    public VanillaTradeListingCollectionHelper addExchanges(
            Item costA, int costAMin, int costAMax, @Nullable Item costB, int costBMin, int costBMax,
            Item result, int resultMin, int resultMax, int maxUses)
    {
        return this.addExchanges(getNonnullInstance(costA), costAMin, costAMax, costB == null ? ItemStack.EMPTY : getNonnullInstance(costB),
                costBMin, costBMax, getNonnullInstance(result), resultMin, resultMax, maxUses);
    }

    /**
     * Add an generic exchanging listing without costB.
     */
    public VanillaTradeListingCollectionHelper addExchanges(ItemStack costA, int costAMin, int costAMax, ItemStack result, int resultMin, int resultMax, int maxUses)
    {
        return this.addExchanges(costA, costAMin, costAMax, null, 1, 1, result, resultMin, resultMax, maxUses);
    }

    /**
     * Add an generic exchanging listing without costB.
     */
    public VanillaTradeListingCollectionHelper addExchanges(Item costA, int costAMin, int costAMax, Item result, int resultMin, int resultMax, int maxUses)
    {
        return this.addExchanges(costA, costAMin, costAMax, null, 1, 1, result, resultMin, resultMax, maxUses);
    }

    /**
     * Add a listing that the mob buys item from player with currency.
     */
    public VanillaTradeListingCollectionHelper addBuys(ItemStack buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
    {
        return this.addExchanges(buys, buysMin, buysMax, this.getCurrency(), priceMin, priceMax, maxUses);
    }

    /**
     * Add a listing that the mob buys item from player with currency.
     */
    public VanillaTradeListingCollectionHelper addBuys(Item buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
    {
        return this.addExchanges(getNonnullInstance(buys), buysMin, buysMax, this.getCurrency(), priceMin, priceMax, maxUses);
    }

    /**
     * Add a listing that the mob buys one random item from the buys list from player with currency.
     */
    public VanillaTradeListingCollectionHelper addBuys(ItemStack[] buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
    {
        VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(buysMin, buysMax, priceMin, priceMax).addA(buys).addResult(this.getCurrency()).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    /**
     * Add a listing that the mob buys one random item from the buys list from player with currency.
     */
    public VanillaTradeListingCollectionHelper addBuys(Item[] buys, int buysMin, int buysMax, int priceMin, int priceMax, int maxUses)
    {
        VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(buysMin, buysMax, priceMin, priceMax).addA(buys).addResult(this.getCurrency()).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    /**
     * Add a listing that the mob sells item to player with currency.
     */
    public VanillaTradeListingCollectionHelper addSells(int priceMin, int priceMax, ItemStack sells, int sellsMin, int sellsMax, int maxUses)
    {
        return this.addExchanges(this.getCurrency(), priceMin, priceMax, sells, sellsMin, sellsMax, maxUses);
    }

    /**
     * Add a listing that the mob sells item to player with currency.
     */
    public VanillaTradeListingCollectionHelper addSells(int priceMin, int priceMax, Item sells, int sellsMin, int sellsMax, int maxUses)
    {
        return this.addExchanges(this.getCurrency(), priceMin, priceMax, getNonnullInstance(sells), sellsMin, sellsMax, maxUses);
    }

    /**
     * Add a listing that the mob sells one random item from the sells list to player with currency.
     */
    public VanillaTradeListingCollectionHelper addSells(int priceMin, int priceMax, ItemStack[] sells, int sellsMin, int sellsMax, int maxUses)
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
    public VanillaTradeListingCollectionHelper addSells(int priceMin, int priceMax, Item[] sells, int sellsMin, int sellsMax, int maxUses)
    {
        VanillaTradeListing l = VanillaTradeListing.invalidWithAmounts(priceMin, priceMax, sellsMin, sellsMax)
                .addA(this.getCurrency()).addResult(sells).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    public VanillaTradeListingCollectionHelper addSellsEnchantmentBook(int priceMin, int priceMax, Enchantment enc, int lvl, int maxUses)
    {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        VanillaTradeListing l = new VanillaTradeListingEnchanted(enc, lvl).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
                .addA(this.getCurrency()).addB(Items.BOOK).addResult(book).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    @Deprecated
    public VanillaTradeListingCollectionHelper addSellsEnchantmentBook(int priceMin, int priceMax, RandomEnchantmentSelector enchantmentSelector, int maxUses)
    {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        VanillaTradeListing l = new VanillaTradeListingEnchanted(enchantmentSelector).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
                .addA(this.getCurrency()).addResult(book).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    public VanillaTradeListingCollectionHelper addEnchantsBook(int priceMin, int priceMax, Enchantment enc, int lvl, int maxUses)
    {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        VanillaTradeListing l = new VanillaTradeListingEnchanted(enc, lvl).setACountRange(priceMin, priceMax).setResultCountRange(1, 1)
                .addA(this.getCurrency()).addB(Items.BOOK).addResult(book).setMaxUses(maxUses);
        if (this.usesPoisson()) l.setAllPoisson(this.getPoissonFactor());
        this.addListing(l);
        return this;
    }

    public VanillaTradeListingCollectionHelper addEnchantsBook(int priceMin, int priceMax, RandomEnchantmentSelector enchantmentSelector, int maxUses)
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
    public VanillaTradeListingCollectionHelper addConverts(ItemStack extraCost, int costMin, int costMax, ItemStack from, ItemStack to, int convertsMin, int convertsMax, int maxUses)
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
    public VanillaTradeListingCollectionHelper addConverts(Item extraCost, int costMin, int costMax, Item from, Item to, int convertsMin, int convertsMax, int maxUses)
    {
        return this.addConverts(getNonnullInstance(extraCost), costMin, costMax,
                getNonnullInstance(from), getNonnullInstance(to), convertsMin, convertsMax, maxUses);
    }

    /**
     * Add a listing that mobs receives currency and converts some amount of an item to another.
     * (e.g. vanilla paid cooking)
     */
    public VanillaTradeListingCollectionHelper addConverts(int costMin, int costMax, ItemStack from, ItemStack to, int convertsMin, int convertsMax, int maxUses)
    {
        return this.addConverts(this.getCurrency(), costMin, costMax, from, to, convertsMin, convertsMax, maxUses);
    }

    /**
     * Add a listing that mobs receives currency and converts some amount of an item to another.
     * (e.g. vanilla paid cooking)
     */
    public VanillaTradeListingCollectionHelper addConverts(int costMin, int costMax, Item from, Item to, int convertsMin, int convertsMax, int maxUses)
    {
        return this.addConverts(costMin, costMax, getNonnullInstance(from), getNonnullInstance(to), convertsMin, convertsMax, maxUses);
    }

    /**
     * Set selection weight of the last added listing.
     */
    public VanillaTradeListingCollectionHelper weight(double value)
    {
        if (this.lastListing != null)
            this.lastListing.setSelectionWeight(value);
        else LogUtils.getLogger().error("VanillaTradeRegistry#Registering#weight: no listing registered. Skipped.");
        return this;
    }

    /**
     * Set selection weight of the last added listing.
     */
    public VanillaTradeListingCollectionHelper maxUses(int value)
    {
        if (this.lastListing != null)
            this.lastListing.setMaxUses(value);
        else LogUtils.getLogger().error("VanillaTradeRegistry#Registering#maxUses: no listing registered. Skipped.");
        return this;
    }

    public VanillaTradeListingCollectionHelper readData(ResourceLocation location)
    {
        MinecraftServer server = NFULibrary.getServer();
        if (server == null) return this;
        ResourceManager mgr = server.getResourceManager();
        // Auto-fix if the coder forgets to add ".json"
        ResourceLocation actualLocation = location.toString().endsWith(".json")?
                location : new ResourceLocation(location.toString() + ".json");
        List<Resource> resources;
        try { resources = mgr.getResources(actualLocation);
        } catch (IOException e) {throw new RuntimeException(e);}
        for (Resource r: resources)
        {
            try {
                InputStream input = r.getInputStream();
                Reader reader = new InputStreamReader(input);
                JsonElement json = JsonParser.parseReader(reader);

                // Don't make settings change after reading json
                // Record settings
                boolean usesPoisson = this.randomizeUsesPoisson;
                double poissonFactor = this.poissonFactor;
                ItemStack currency = this.currency;
                int level = this.level;
                VillagerProfession prof = this.profession;

                // Read. Settings may be randomly changed during reading.
                this.setRequiredLevel(1);	// In json it's 1 by default
                this.readSingleJson(json, new Tuple3<>(usesPoisson, poissonFactor, currency), NFUConfigs.CACHED_DEBUG_MODE);

                // Set back, don't let json reading change the settings
                this.setRandomizationUsesPoisson(usesPoisson);
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

    /*
     * A wrapped data reading option that should read an extra optional {@code "currency"} field and temporarily set currency.
     */
    /*private Consumer<JsonObject> withCurrencyAndLevelOption(Consumer<JsonObject> action) {
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

            boolean withLevel = jo.has("level") && jo.isJsonPrimitive();
            int oldLevel = this.level;
            if (withLevel)
                this.setRequiredLevel(jo.get("level").getAsInt());

            action.accept(jo);

            if (withCurrency)
                this.setCurrency(oldCurrency);
            if (withLevel)
                this.setRequiredLevel(oldLevel);
        };
    }*/

    /**
     * Read and add entries from a single json file.
     */
    private void readSingleJson(JsonElement json, Tuple3<Boolean, Double, ItemStack> defaultSettings, boolean debug) {
        try {
            for (JsonElement element : json.getAsJsonArray()) {
                try {
                    // "type" field defines what this entry stands for.
                    // It was previously called "action", be compatible with the old format
                    if (!element.isJsonObject()) continue;
                    JsonObject jo = element.getAsJsonObject();
                    String type = NFUDataStatics.getOptional(jo, "type", JsonElement::getAsString)
                        .orElseGet(() -> NFUDataStatics.getOptional(jo, "action", JsonElement::getAsString)
                            .orElse("settings"));

                    switch (type) {

                        // ===== Case default configurations ===== //
                        case "settings": {
                            if (jo.has("currency")) {
                                ItemStack[] item = readItem(jo.get("currency"), false);
                                if (item.length != 1 || item[0] == null || item[0].isEmpty()) {
                                    LogUtils.getLogger().error("VanillaTradeRegistry#readData set currency failed: missing or duplicate item. Set to Emerald.");
                                    item[0] = Items.EMERALD.getDefaultInstance();
                                }
                                this.setCurrency(item[0]);
                            }
                            if (jo.has("poisson"))
                                this.setRandomizationUsesPoisson(jo.get("poisson").getAsBoolean());
                            if (jo.has("p"))
                                this.setPoissonFactor(jo.get("p").getAsDouble());
                            if (jo.has("level"))
                                this.setRequiredLevel(jo.get("level").getAsInt());
                            break;
                        }
                        case "reset": {
                            this.setCurrency(defaultSettings.c);
                            this.setRandomizationUsesPoisson(defaultSettings.a);
                            this.setPoissonFactor(defaultSettings.b);
                            break;
                        }
                        case "link":
                        case "attach": {
                            String target = jo.get("target").getAsString();
                            try {
                                this.attach((VanillaTradeListingCollection<VanillaTradeListing>) NFURegistries
                                    .VANILLA_TRADE_LISTING_COLLECTIONS.getValue(new ResourceLocation(target)));
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        // Trade entry definitions
                        case "registered": case "registry": case "entry": {    // Add an entry from predefined listing in registry
                            String key = jo.get("key").getAsString();
                            Optional<Integer> level = NFUDataStatics.getOptionalInt(jo, "level");
                            Optional<Double> weight = NFUDataStatics.getOptionalDouble(jo, "weight");
                            if (NFURegistries.VANILLA_TRADE_LISTINGS.containsKey(new ResourceLocation(key))) {
                                level.ifPresentOrElse(
                                    lvl -> this.addListing(lvl, NFURegistries.VANILLA_TRADE_LISTINGS.getValue(new ResourceLocation(key))),
                                    () -> this.addListing(NFURegistries.VANILLA_TRADE_LISTINGS.getValue(new ResourceLocation(key)))
                                );
                                weight.ifPresent(this::weight);
                            }
                            break;
                        }
                        // ===== Case entry reading ===== //
                        default: {
                            readListing(jo, this.getCurrency(), this.usesPoisson(), this.getPoissonFactor(), this.level)
                                .ifPresent(this::addListing);
                        }
                    }
                } catch(Exception | NoSuchMethodError | NoSuchFieldError e){
                    if (debug) e.printStackTrace();
                }
            }
        } catch(Exception e){
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
    public static ItemStack[] readItem(JsonElement element, boolean allowsArray) {
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
                                throw new JsonParseException("VanillaTradeListingCollectionHelper#readItem doesn't allow nested arrays.");
                            res[i] = readItem(e, false)[0];
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            res[i] = ItemStack.EMPTY;
                        }
                    }
                    return res;
                } else
                    throw new JsonParseException("VanillaTradeListingCollectionHelper#readItem: JsonArray detected, but not allowed.");
            } else throw new JsonParseException("Read item failed.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[] {ItemStack.EMPTY};
        }
    }

    public static int[] readAmountRange(JsonElement element) {
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
    public static Tuple<Enchantment, Integer> readEnchantment(JsonElement element)
    {
        if (element == null) return null;
        if (element.isJsonObject())
        {
            Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(
                NFUDataStatics.getOptionalString(element.getAsJsonObject(), "id").orElseGet(() ->
                    NFUDataStatics.getOptionalString(element.getAsJsonObject(), "key").orElse("minecraft:none"))));
            if (e == null) return null;
            int lv = element.getAsJsonObject().has("level") ?
                    element.getAsJsonObject().get("level").getAsInt() : e.getMaxLevel();
            return new Tuple<>(e, lv);
        } else if (element.isJsonPrimitive()) {
            Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(element.getAsString()));
            if (e == null) return null;
            return new Tuple<>(e, e.getMaxLevel());
        }
        else throw new JsonParseException("invalid enchantment");
    }

    /**
     * Utility. Static reading a single listing without context parameters.
     */
    public static Optional<VanillaTradeListing> readListing(
        JsonObject from,
        @Nullable ItemStack defaultCurrency,
        boolean defaultUsesPoisson,
        double defaultPoissonP,
        int defaultRequiredLevel)
    {
        ItemStack currency = (defaultCurrency != null && !defaultCurrency.isEmpty()) ?
            defaultCurrency : Items.EMERALD.getDefaultInstance();
        try {
            ItemStack[] currencyArray = NFUDataStatics.getOptional(from, "currency", e -> readItem(e, false))
                .orElse(new ItemStack[]{currency});
            if (currencyArray.length != 1 || currencyArray[0] == null || currencyArray[0].isEmpty()) {
                LogUtils.getLogger().error("VanillaTradeRegistry#readData set currency failed: missing or duplicate item. Set to Emerald.");
            } else {
                currency = currencyArray[0];
            }
        } catch (RuntimeException ignore) {
        }
        String type = NFUDataStatics.getOptionalString(from, "type").orElseGet(() ->
            NFUDataStatics.getOptionalString(from, "action").orElse(null));
        if (type == null) return Optional.empty();
        boolean poisson = NFUDataStatics.getOptional(from, "poisson", JsonElement::getAsBoolean).orElse(defaultUsesPoisson);
        double p = NFUDataStatics.getOptionalDouble(from, "p").orElse(defaultPoissonP);
        int level = NFUDataStatics.getOptionalInt(from, "level").orElse(defaultRequiredLevel);
        int maxUses = NFUDataStatics.getOptionalInt(from, "maxUses").orElse(12);
        double weight = NFUDataStatics.getOptionalDouble(from, "weight").orElse(1d);
        VanillaTradeListing res = null;
        switch (type) {
            case "buy": {
                ItemStack[] buys = readItem(from.get("item"), true);
                int[] price = readAmountRange(from.get("price"));
                int[] amount = readAmountRange(from.get("amount"));
                res = VanillaTradeListing.invalidWithAmounts(amount[0], amount[1], price[0], price[1])
                    .addA(buys)
                    .addResult(currency);
                break;
            }
            case "sell": {
                ItemStack[] sells = readItem(from.get("item"), true);
                int[] price = readAmountRange(from.get("price"));
                int[] amount = readAmountRange(from.get("amount"));
                res = VanillaTradeListing.invalidWithAmounts(price[0], price[1], amount[0], amount[1])
                    .addA(currency)
                    .addResult(sells);
                break;
            }
            case "convert": {
                ItemStack[] convFrom = readItem(from.get("item"), false);
                ItemStack[] to = readItem(from.get("result"), false);
                int[] price = readAmountRange(from.get("price"));
                int[] amount = readAmountRange(from.get("amount"));
                res = VanillaTradeListing.converts(currency, price[0], price[1], convFrom[0], to[0],
                    amount[0], amount[1]);
                break;
            }
            case "enchantmentBook": {
                int[] price = readAmountRange(from.get("price"));
                var enchantment = readEnchantment(from.get("enchantment"));
                if (enchantment != null) {
                    res = new VanillaTradeListingEnchanted(enchantment.getA(), enchantment.getB())
                        .setEnchantsBook()
                        .addA(currency)
                        .setACountRange(price[0], price[1]);
                }
                break;
            }
            default:
                break;
        }
        // Finalize and reset the helper params
        if (res == null) return Optional.empty();
        res.setMaxUses(maxUses);
        res.setSelectionWeight(weight);
        res.setDefaultRequiredLevel(level);
        if (poisson)
            res.setAllPoisson(p);
        return Optional.of(res);
    }


    @Nonnull
    private static ItemStack getNonnullInstance(@Nullable Item item)
    {
        return item != null ? item.getDefaultInstance() : ItemStack.EMPTY;
    }


}

