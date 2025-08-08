package net.sodiumzh.nfu.entity.vanillatrade;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.container.Tuple2;
import net.sodiumzh.nfu.container.Tuple3;
import net.sodiumzh.nfu.math.ThreadSafeRandomSource;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.util.NFUDataStatics;
import net.sodiumzh.nfu.util.NFUDebugStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A {@link VanillaTradeRegistry} is a mapping from each {@link ResourceLocation} key and each {@link VillagerProfession} to
 * a collection of {@link VanillaTradeListingCollection}s.
 * <p>Mob instances will access the trade entry generators by this registry. Note that this trade registry is not {@link NFURegistry},
 * but is recommended to be registered into "the registry of trade registries"({@link NFURegistries#VANILLA_TRADE_REGISTRIES}.
 * <p>Note: The {@link ResourceLocation}s are arbitrary as an identifier without pre-defined meanings.
 * <p>Tip: Use {@link VanillaTradeRegistry#collect()} to get the mapping to the merged listing sets from all related collections.
 */
public class VanillaTradeRegistry
{
	private static final RandomSource RND = new ThreadSafeRandomSource();
	private final SetMultimap<Tuple2<ResourceLocation, VillagerProfession>, VanillaTradeListingCollection<?>> table;

	@Nullable
	private ResourceLocation lastKey = null;
	@Nonnull
	private VillagerProfession lastProfession = VillagerProfession.NONE;
	private VanillaTradeRegistry.Collected collectedCache = null;

	public VanillaTradeRegistry() {
		this.table = HashMultimap.create();
		MinecraftForge.EVENT_BUS.post(new VanillaTradeRegistryEvent(this));
		// Event operations may modify these values. Prevent them from impacting further registrations
		this.lastKey = null;
		this.lastProfession = VillagerProfession.NONE;
	}

	public VanillaTradeRegistry put(ResourceLocation key, VillagerProfession profession, VanillaTradeListingCollection<?>... values) {
		for (VanillaTradeListingCollection<?> value: values) {
			table.put(new Tuple2<>(key, profession), value);
		}
		lastKey = key;
		lastProfession = profession;
		this.collectedCache = null;
		return this;
	}

	public VanillaTradeRegistry put(ResourceLocation key, VanillaTradeListingCollection<?>... values) {
		return put(key, VillagerProfession.NONE, values);
	}

	public VanillaTradeRegistry put(ResourceLocation key, VillagerProfession profession, Iterable<VanillaTradeListingCollection<?>> values) {
		values.forEach(v -> table.put(new Tuple2<>(key, profession), v));
		lastKey = key;
		lastProfession = profession;
		this.collectedCache = null;
		return this;
	}

	public VanillaTradeRegistry put(ResourceLocation key, Iterable<VanillaTradeListingCollection<?>> values) {
		return put(key, VillagerProfession.NONE, values);
	}

	public VanillaTradeRegistry putLast(VillagerProfession profession, VanillaTradeListingCollection<?>... values) {
		if (lastKey == null) {
			throw new IllegalStateException("VanillaTradeRegistry#putLast: missing last key. Specify a key by calling any " +
					"key-specific version of put() before calling any key-omitted versions.");
		}
		return put(lastKey, profession, values);
	}

	public VanillaTradeRegistry putLast(VanillaTradeListingCollection<?>... values) {
		if (lastKey == null) {
			throw new IllegalStateException("VanillaTradeRegistry#putLast: missing last key. Specify a key by calling any " +
					"key-specific version of put() before calling any key-omitted versions.");
		}
		return put(lastKey, lastProfession, values);
	}

	public Set<VanillaTradeListingCollection<?>> getCollections(ResourceLocation key, VillagerProfession profession) {
		return table.get(new Tuple2<>(key, profession));
	}

	public Set<VanillaTradeListingCollection<?>> getCollectionsForDefaultProfession(ResourceLocation key) {
		return getCollections(key, VillagerProfession.NONE);
	}

	/**
	 * Collect all elements and provide a 3-dimensional mapping from (key, profession, merchant level) to
	 * united listings from all listing collections.
	 */
	public Collected collect() {
		if (collectedCache != null) return collectedCache;
		Collected res = new Collected();
		table.keySet().stream()
				.map(k -> Tuple3.of(k, table.get(k)))
				.forEach(entry -> {
					entry.c.stream()
						.map(VanillaTradeListingCollection::allLevelsAndListings)
						.forEach(multimap -> multimap.keySet().forEach(level -> res.table.
								putAll(Tuple3.of(entry.a, entry.b, level), multimap.get(level))));
		});
		collectedCache = res;
		return res;
	}

	/**
	 * A pre-processed copy of a {@link VanillaTradeRegistry}, allowing to directly access the combined listing collections.
	 * Created only from {@link VanillaTradeRegistry#collect()}.
	 */
	public static class Collected {
		private final SetMultimap<Tuple3<ResourceLocation, VillagerProfession, Integer>, IVanillaTradeListing> table
				= HashMultimap.create();

		private Collected() {}

		/**
		 * Get a set of listings for given key, profession and merchant level.
		 */
		public Set<IVanillaTradeListing> get(ResourceLocation key, VillagerProfession profession, Integer level) {
			return table.get(Tuple3.of(key, profession, level));
		}

		/**
		 * Get a level-listings mapping for specified key and profession.
		 * @return A listing-collection-like mapping, unmodifiable but supporting most queries
		 * of {@link VanillaTradeListingCollection}.
		 */
		public IVanillaTradeListingCollection<IVanillaTradeListing> get(ResourceLocation key, VillagerProfession profession) {
			SetMultimap<Integer, IVanillaTradeListing> res = HashMultimap.create();
			table.keySet().stream().filter(ks -> ks.a.equals(key) && ks.b.equals(profession))
					.forEach(ks -> res.putAll(ks.c, table.get(ks)));
			return new UnmodifiableVanillaTradeListingCollection<>(res);
		}

		/**
		 * Get a set of listings for given key and merchant level for {@link VillagerProfession#NONE}.
		 */
		public Set<IVanillaTradeListing> getForDefaultProfession(ResourceLocation key, Integer level) {
			return this.get(key, VillagerProfession.NONE, level);
		}

		/**
		 * Get a level-listings mapping for specified key for {@link VillagerProfession#NONE}.
		 * @return A listing-collection-like mapping, unmodifiable but supporting most queries
		 * of {@link VanillaTradeListingCollection}.
		 */
		public IVanillaTradeListingCollection<IVanillaTradeListing> getForDefaultProfession(ResourceLocation key) {
			return this.get(key, VillagerProfession.NONE);
		}
	}

	public VanillaTradeRegistry readData(ResourceLocation data) {
		ResourceLocation actualDataKey = data.toString().endsWith(".json")?
				data : new ResourceLocation(data.getNamespace(), data.getPath() + ".json");
		NFUDataStatics.readJsons(LogicalSide.SERVER, actualDataKey, json -> {
			try {
				json.getAsJsonArray().forEach(elem -> {
					ResourceLocation lastKey = null;
					VillagerProfession lastProf = VillagerProfession.NONE;
					ResourceLocation currentKey = null;
					try {
						ResourceLocation key =
								NFUDataStatics.getOptional(elem.getAsJsonObject(), "key", JsonElement::getAsString)
								.map(ResourceLocation::new).orElse(lastKey);
						currentKey = key;
						if (key == null) return;
						VillagerProfession prof =
								NFUDataStatics.getOptional(elem.getAsJsonObject(), "profession", JsonElement::getAsString)
										.map(ResourceLocation::new)
										.map(ForgeRegistries.VILLAGER_PROFESSIONS::getValue)
										.orElse(lastProf);
						Set<VanillaTradeListingCollection<?>> collections =
								NFUDataStatics.getOptionalList(elem.getAsJsonObject(), "collections", JsonElement::getAsString,
                                        e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()).stream()
                                .map(k -> NFURegistries.VANILLA_TRADE_LISTING_COLLECTIONS.getValue(new ResourceLocation(k)))
                                .filter(Objects::nonNull).collect(Collectors.toSet());
						this.put(key, prof, collections);
						this.lastKey = key;
						this.lastProfession = prof;
					} catch (Exception e) {
						NFUDebugStatics.errorOnce(VanillaTradeRegistry.class,
								String.format("Read data failed, entry '%s' skipped. Exception: \n%s",
										Optional.ofNullable(currentKey).map(ResourceLocation::toString)
												.orElse("(missing key)"), e.getMessage()));
					}
				});
			} catch (Exception e){
				NFUDebugStatics.errorOnce(VanillaTradeRegistry.class,
						String.format("Read data failed, json '%s' skipped. Exception: \n%s", data.toString(), e.getMessage()));
			}
		});
		return this;
	}

	// Utilities

	public boolean hasAnyListing(ResourceLocation key, VillagerProfession profession) {
		return !this.collect().get(key, profession).isEmpty();
	}

	public List<ResourceLocation> getAllCollectionKeys(ResourceLocation key, VillagerProfession profession) {
		return this.getCollections(key, profession).stream().map(VanillaTradeListingCollection::getRegistryKey).map(o -> o.orElse(new ResourceLocation("not:registered"))).collect(Collectors.toList());
	}

}