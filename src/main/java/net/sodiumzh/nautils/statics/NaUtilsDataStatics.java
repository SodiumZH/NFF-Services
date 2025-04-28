package net.sodiumzh.nautils.statics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.containers.Tuple2;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NaUtilsDataStatics {

    public static Optional<ResourceManager> getResourceManager(LogicalSide side) {
        try {
            switch (side) {
                case SERVER -> {return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getResourceManager());}
                case CLIENT -> {return Optional.ofNullable(Minecraft.getInstance().getResourceManager());}
                default -> throw new RuntimeException();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ResourceManager> getResourceManager() {
        return getResourceManager(EffectiveSide.get());
    }


    /**
     * Read all jsons at a given location.
     * @param location Location of the json.
     * @param reader Actions to do for each json. Note that this reader may be invoked multiple times if there are
     *               multiple data packs present. If IO or runtime exception occurs during invoking, it will not throw the
     *               exception out but print stack trace and continue reading next json.
     * @param suppressStackTrace If true, when an exception is caught, it will not print stack trace and just continue on next json.
     */
    public static void readJsons(LogicalSide side, ResourceLocation location, Consumer<JsonElement> reader, boolean suppressStackTrace)
    {
        ResourceManager mgr = getResourceManager(side).orElse(null);
        if (mgr == null) return;
        try {
            List<Resource> resources = mgr.getResources(location);
            for (Resource r : resources) {
                try (InputStream input = r.getInputStream()) {
                    Reader inputReader = new InputStreamReader(input);
                    JsonElement json = JsonParser.parseReader(inputReader);
                    reader.accept(json);
                } catch (RuntimeException e) {
                    if (!suppressStackTrace)
                        e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read all jsons at a given location.
     * @param location Location of the json.
     * @param reader Actions to do for each json. Note that this reader may be invoked multiple times if there are
     *               multiple data packs present. If IO or runtime exception occurs during invoking, it will not throw the
     *               exception out but print stack trace and continue reading next json.
     */
    public static void readJsons(LogicalSide side, ResourceLocation location, Consumer<JsonElement> reader)
    {
        readJsons(side, location, reader, false);
    }

    /**
     * Get an optional key value from a {@link JsonObject}.
     * <p>If the key is absent, the key value as element doesn't pass the filter, or
     * any exception is thrown during this method, return {@link Optional#empty()}.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @param filter Pre-check of the {@link JsonElement} got from the source and key. If the result is {@code false},
     *               return {@link Optional#empty()}. No filter by default.
     * @param errorHandler actions if any exception is thrown. No action by default.
     * @return An {@link Optional} of the accessed value.
     * @param <T> Type of final value. Usually referred from {@code getter}.
     */
    @Nonnull
    public static <T> Optional<T> getOptional(JsonObject source, String key, Function<JsonElement, T> getter,
                                              Predicate<JsonElement> filter, @Nonnull Consumer<Throwable> errorHandler) {
        if (!source.has(key)) return Optional.empty();
        JsonElement elem = source.get(key);
        try {
            if (filter.test(elem)) return Optional.ofNullable(getter.apply(elem));
            else return Optional.empty();
        } catch (Throwable t) {
            errorHandler.accept(t);
            return Optional.empty();
        }
    }

    /**
     * Get an optional key value from a {@link JsonObject}.
     * <p>If the key is absent, the key value as element doesn't pass the filter, or
     * any exception is thrown during this method, return {@link Optional#empty()}.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @param filter Pre-check of the {@link JsonElement} got from the source and key. If the result is {@code false},
     *               return {@link Optional#empty()}. No filter by default.
     * @return An {@link Optional} of the accessed value.
     * @param <T> Type of final value. Usually referred from {@code getter}.
     */
    public static <T> Optional<T> getOptional(JsonObject source, String key,
                                              Function<JsonElement, T> getter, Predicate<JsonElement> filter) {
        return getOptional(source, key, getter, filter, e -> {});
    }

    /**
     * Get an optional key value from a {@link JsonObject}.
     * <p>If the key is absent or any exception is thrown during this method,
     * return {@link Optional#empty()}.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @return An {@link Optional} of the accessed value.
     * @param <T> Type of final value. Usually referred from {@code getter}.
     */
    public static <T> Optional<T> getOptional(JsonObject source, String key, Function<JsonElement, T> getter) {
        return getOptional(source, key, getter, e -> true);
    }

    /**
     * Get an optional key value which is sometimes {@link JsonArray} and sometimes not.
     * <p>If the value is a {@link JsonArray}, collect its each element with {@code getter} as a {@link List}.
     * If the filter returns false, the getter returns null or any exception is thrown,
     * the array element will be skipped.
     * <p>If the value isn't an array, directly apply the getter on it, and return a single-element {@link List}.
     * (Null or exception = empty list).
     * <p>If the key is absent, return empty list.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @param filter Pre-check of the {@link JsonElement} got from the source and key. If the result is {@code false},
     *               the element will be skipped. No filter by default.
     * @param errorHandler actions if any exception is thrown. No action by default.
     * @return A {@link List} of the accessed values.
     * @param <T> Element type of final array. Usually referred from {@code getter}.
     */
    @Nonnull
    public static <T> List<T> getOptionalList(JsonObject source, String key, Function<JsonElement, T> getter, Predicate<JsonElement> filter, @Nonnull Consumer<Throwable> errorHandler) {
        List<T> res = new ArrayList<>();
        if (!source.has(key)) return res;
        if (source.get(key).isJsonArray()) {
            for (JsonElement elem: source.get(key).getAsJsonArray()) {
                try {
                    if (filter.test(elem)) res.add(getter.apply(elem));
                } catch (Throwable t) {
                    errorHandler.accept(t);
                }
            }
        }
        else {
            try {
                if (filter.test(source.get(key))) res.add(getter.apply(source.get(key)));
            } catch (Throwable t) {
                errorHandler.accept(t);
            }
        }
        return res;
    }

    /**
     * Get an optional key value which is sometimes {@link JsonArray} and sometimes not.
     * <p>If the value is a {@link JsonArray}, collect its each element with {@code getter} as a {@link List}.
     * If the filter returns false, the getter returns null or any exception is thrown,
     * the array element will be skipped.
     * <p>If the value isn't an array, directly apply the getter on it, and return a single-element {@link List}.
     * (Null or exception = empty list).
     * <p>If the key is absent, return empty list.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @param filter Pre-check of the {@link JsonElement} got from the source and key. If the result is {@code false},
     *               the element will be skipped. No filter by default.
     * @return A {@link List} of the accessed values.
     * @param <T> Element type of final array. Usually referred from {@code getter}.
     */
    @Nonnull
    public static <T> List<T> getOptionalList(JsonObject source, String key, Function<JsonElement, T> getter, Predicate<JsonElement> filter) {
        return getOptionalList(source, key, getter, filter, e -> {});
    }

    /**
     * Get an optional key value which is sometimes {@link JsonArray} and sometimes not.
     * <p>If the value is a {@link JsonArray}, collect its each element with {@code getter} as a {@link List}.
     * If the getter returns null or any exception is thrown,
     * the array element will be skipped.
     * <p>If the value isn't an array, directly apply the getter on it, and return a single-element {@link List}.
     * (Null or exception = empty list).
     * <p>If the key is absent, return empty list.
     * @param source {@link JsonObject} to read.
     * @param key Not necessarily present in the source {@link JsonObject}.
     * @param getter Function to get the result.
     * @return A {@link List} of the accessed values.
     * @param <T> Element type of final array. Usually referred from {@code getter}.
     */
    @Nonnull
    public static <T> List<T> getOptionalList(JsonObject source, String key, Function<JsonElement, T> getter) {
        return getOptionalList(source, key, getter, e -> true, e -> {});
    }

    public static List<Tuple2<ResourceLocation, JsonElement>> getJsonsUnderPath(LogicalSide side, String path, boolean suppressStackTrace) {
        List<Tuple2<ResourceLocation, JsonElement>> res = new ArrayList<>();
        ResourceManager mgr = getResourceManager(side).orElse(null);
        if (mgr == null) return res;
        Map<ResourceLocation, List<Resource>> resourceMap = new HashMap<>();
        mgr.listResources(path, l -> l.endsWith(".json")).stream()
                .forEach(loc -> {
                    try {
                        resourceMap.put(loc, mgr.getResources(loc));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        resourceMap.entrySet().forEach(entry -> {
            entry.getValue().stream().map(r -> {
                try (InputStream input = r.getInputStream()) {
                    Reader inputReader = new InputStreamReader(input);
                    return JsonParser.parseReader(inputReader);
                } catch (IOException | RuntimeException e) {
                    if (!suppressStackTrace)
                        e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).map(j -> Tuple2.of(entry.getKey(), j)).forEach(res::add);
        });
        return res;
    }

    public static List<Tuple2<ResourceLocation, JsonElement>> getJsonsUnderPath(LogicalSide side, String path) {
        return getJsonsUnderPath(side, path, false);
    }

    public static void readJsonsUnderPath(LogicalSide side, String path, BiConsumer<ResourceLocation,JsonElement> reader, boolean suppressStackTrace) {
        getJsonsUnderPath(side, path, suppressStackTrace).forEach(entry -> {
            try {
                reader.accept(entry.getA(), entry.getB());
            } catch (Exception e) {
                if (!suppressStackTrace)
                    e.printStackTrace();
            }
        });
    }

    public static void readJsonsUnderPath(LogicalSide side, String path, BiConsumer<ResourceLocation,JsonElement> reader) {
        readJsonsUnderPath(side, path, reader, false);
    }

}
