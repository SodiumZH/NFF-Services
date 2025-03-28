package net.sodiumzh.nautils.entity.vanillatrade;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nautils.containers.Tuple2;
import net.sodiumzh.nautils.containers.Tuple3;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;
import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;
import net.sodiumzh.nautils.statics.NaUtilsDataStatics;
import net.sodiumzh.nautils.statics.NaUtilsDebugStatics;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.plaf.PanelUI;

/**
 * A {@link VanillaTradeRegistry} is a mapping from each {@link ResourceLocation} key and each {@link VillagerProfession} to
 * a collection of {@link VanillaTradeListingCollection}s.
 * <p>Mob instances will access the trade entry generators by this registry. Note that this trade registry is not {@link NaUtilsRegistry},
 * but is recommended to be registered into "the registry of trade registries"({@link NaUtilsRegistries#VANILLA_TRADE_REGISTRIES}.
 * <p>Note: The {@link ResourceLocation}s are arbitrary as an identifier without pre-defined meanings.
 */
public class VanillaTradeRegistry
{
	private static final RandomSource RND = RandomSource.create();
	private final SetMultimap<Tuple2<ResourceLocation, VillagerProfession>, VanillaTradeListingCollection<?>> table;

	@Nullable
	private ResourceLocation lastKey = null;
	@Nonnull
	private VillagerProfession lastProfession = VillagerProfession.NONE;

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
		return this;
	}

	public VanillaTradeRegistry put(ResourceLocation key, VanillaTradeListingCollection<?>... values) {
		return put(key, VillagerProfession.NONE, values);
	}

	public VanillaTradeRegistry put(ResourceLocation key, VillagerProfession profession, Iterable<VanillaTradeListingCollection<?>> values) {
		values.forEach(v -> table.put(new Tuple2<>(key, profession), v));
		lastKey = key;
		lastProfession = profession;
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

	public Set<VanillaTradeListingCollection<?>> get(ResourceLocation key, VillagerProfession profession) {
		return table.get(new Tuple2<>(key, profession));
	}

	public Set<VanillaTradeListingCollection<?>> get(ResourceLocation key) {
		return get(key, VillagerProfession.NONE);
	}

	public Collected collect() {
		Collected res = new Collected();
		table.keySet().stream()
				.map(k -> Tuple3.of(k, table.get(k)))
				.forEach(entry -> {
					entry.c.stream()
						.map(VanillaTradeListingCollection::allLevelsAndListings)
						.forEach(multimap -> multimap.keySet().forEach(level -> res.table.
								putAll(Tuple3.of(entry.a, entry.b, level), multimap.get(level))));
		});
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

		public Set<IVanillaTradeListing> get(ResourceLocation key, VillagerProfession profession, Integer level) {
			return table.get(Tuple3.of(key, profession, level));
		}

		public SetMultimap<Integer, IVanillaTradeListing> get(ResourceLocation key, VillagerProfession profession) {
			SetMultimap<Integer, IVanillaTradeListing> res = HashMultimap.create();
			table.keySet().stream().filter(ks -> ks.a.equals(key) && ks.b.equals(profession))
					.forEach(ks -> res.putAll(ks.c, table.get(ks)));
			return res;
		}

		public Set<IVanillaTradeListing> getDefaultProfession(ResourceLocation key, Integer level) {
			return this.get(key, VillagerProfession.NONE, level);
		}

		public SetMultimap<Integer, IVanillaTradeListing> getDefaultProfession(ResourceLocation key) {
			return this.get(key, VillagerProfession.NONE);
		}
	}

	public Set<IVanillaTradeListing> pickListings(ResourceLocation key, VillagerProfession prof, int merchantLevel, int amount, RandomSource rnd) {
		Set<IVanillaTradeListing> set = collect().get(key, prof, merchantLevel);
		return NaUtilsContainerStatics.getWeightedRandomSubset(set.stream()
				.collect(Collectors.toMap(l -> l, IVanillaTradeListing::getSelectionWeight)), amount);
	}

	public Set<IVanillaTradeListing> pickListings(ResourceLocation key, VillagerProfession prof, int merchantLevel, int amount) {
		return pickListings(key, prof, merchantLevel, amount, RND);
	}

	public SetMultimap<Integer, IVanillaTradeListing> pickListings(
			ResourceLocation key, VillagerProfession prof, RandomSource rnd, Map<Integer, Integer> levelsAndAmounts) {
		SetMultimap<Integer, IVanillaTradeListing> res = HashMultimap.create();
		levelsAndAmounts.entrySet().stream()
				.map(entry -> Tuple2.of(entry.getKey(), pickListings(key, prof, entry.getKey(), entry.getValue(), rnd)))
				.forEach(entry -> res.putAll(entry.getA(), entry.getB()));
		return res;
	}

	public SetMultimap<Integer, IVanillaTradeListing> pickListings(
			ResourceLocation key, VillagerProfession prof, Map<Integer, Integer> levelsAndAmounts) {
		return pickListings(key, prof, RND, levelsAndAmounts);
	}

	public SetMultimap<Integer, IVanillaTradeListing> pickListings(
			ResourceLocation key, VillagerProfession prof, RandomSource rnd, int... levelsAndAmounts) {
		Map<Integer, Integer> levelsAndAmountsMap = new HashMap<>();
		for (int i = 0; i < levelsAndAmounts.length - 1; i += 2) {
			levelsAndAmountsMap.put(levelsAndAmounts[i], levelsAndAmounts[i+1]);
		}
		return pickListings(key, prof, rnd, levelsAndAmountsMap);
	}

	public SetMultimap<Integer, IVanillaTradeListing> pickListings(
			ResourceLocation key, VillagerProfession prof, int... levelsAndAmounts) {
		return pickListings(key, prof, RND, levelsAndAmounts);
	}

	public VanillaTradeRegistry readData(ResourceLocation data) {
		NaUtilsDataStatics.readJsonsServerSide(data, json -> {
			try {
				json.getAsJsonArray().forEach(elem -> {
					ResourceLocation lastKey = null;
					VillagerProfession lastProf = VillagerProfession.NONE;
					ResourceLocation currentKey = null;
					try {
						ResourceLocation key =
								NaUtilsDataStatics.getOptional(elem.getAsJsonObject(), "key", JsonElement::getAsString)
								.map(ResourceLocation::new).orElse(lastKey);
						currentKey = key;
						if (key == null) return;
						VillagerProfession prof =
								NaUtilsDataStatics.getOptional(elem.getAsJsonObject(), "profession", JsonElement::getAsString)
										.map(ResourceLocation::new)
										.map(ForgeRegistries.VILLAGER_PROFESSIONS::getValue)
										.orElse(lastProf);
						Set<VanillaTradeListingCollection<?>> collections =
								NaUtilsDataStatics.getOptionalList(elem.getAsJsonObject(), "collections", JsonElement::getAsString,
                                        e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()).stream()
                                .map(k -> NaUtilsRegistries.VANILLA_TRADE_LISTING_COLLECTIONS.getValue(new ResourceLocation(k)))
                                .filter(Objects::nonNull).collect(Collectors.toSet());
						this.put(key, prof, collections);
						this.lastKey = key;
						this.lastProfession = prof;
					} catch (Exception e) {
						NaUtilsDebugStatics.errorOnce(VanillaTradeRegistry.class,
								String.format("Read data failed, entry '%s' skipped. Exception: \n%s",
										Optional.ofNullable(currentKey).map(ResourceLocation::toString)
												.orElse("(missing key)"), e.getMessage()));
					}
				});
			} catch (Exception e){
				NaUtilsDebugStatics.errorOnce(VanillaTradeRegistry.class,
						String.format("Read data failed, json '%s' skipped. Exception: \n%s", data.toString(), e.getMessage()));
			}
		});
		return this;
	}

}