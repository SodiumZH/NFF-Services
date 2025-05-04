package net.sodiumzh.nfu.util;

import net.minecraft.nbt.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class NFUNBTStatics {

	@Deprecated
	public static final int TAG_BYTE_ID = 1;
	@Deprecated
	public static final int TAG_SHORT_ID = 2;
	@Deprecated
	public static final int TAG_INT_ID = 3;
	@Deprecated
	public static final int TAG_LONG_ID = 4;
	@Deprecated
	public static final int TAG_FLOAT_ID = 5;
	@Deprecated
	public static final int TAG_DOUBLE_ID = 6;
	@Deprecated
	public static final int TAG_BYTE_ARRAY_ID = 7;
	@Deprecated
	public static final int TAG_STRING_ID = 8;
	@Deprecated
	public static final int TAG_LIST_ID = 9;
	@Deprecated
	public static final int TAG_COMPOUND_ID = 10;
	@Deprecated
	public static final int TAG_INT_ARRAY_ID = 11;
	@Deprecated
	public static final int TAG_LONG_ARRAY_ID = 12;
	@Deprecated
	public static final int TAG_ANY_NUMERIC_ID = 99;
	
	
	// Generate a unique key from the base key in a compound tag
	public static String getUniqueKey(String baseKey, CompoundTag cpd)
	{
		int i = 0;
		while (cpd.contains(baseKey + "_" + Integer.toString(i)))
		{
			i += 1;
		}
		return baseKey + "_" + Integer.toString(i);
	}
	
	// Serialize a UUID array from vector into the given compound tag with given key.
	// Return the ListTag containing the UUIDs.
	@Deprecated
	public static ListTag serializeUUIDSet(CompoundTag tag, HashSet<UUID> set, String key)
	{
		tag.remove(key);
		ListTag list = new ListTag();
		for (UUID id : set)
		{
			list.add(NbtUtils.createUUID(id));
		}
		tag.put(key, list);
		return list;
	}
	
	// Deserialize a UUID array into set from a compound tag with given key.
	// Return a new vector containing the UUIDs.
	@Deprecated
	public static HashSet<UUID> deserializeUUIDSet(CompoundTag inTag, String key)
	{
		ListTag uuidSetTag = inTag.getList(key, Tag.TAG_INT_ARRAY);
		HashSet<UUID> out = new HashSet<UUID>();
		for(Tag tag : uuidSetTag)
		{
			out.add(NbtUtils.loadUUID(tag));
		}
		return out;
	}

	@Deprecated
	public static CompoundTag saveItemStack(@Nullable ItemStack stack, @Nonnull CompoundTag saveTo, String key) {
		CompoundTag newTag = new CompoundTag();
		if (stack == null || stack.isEmpty())
			ItemStack.EMPTY.save(newTag);
		else
			stack.save(newTag);
		saveTo.put(key, newTag);
		return newTag;
	}

	@Deprecated
	public static ItemStack readItemStack(CompoundTag nbt, String key)
	{
		if (nbt.contains(key, 10))
		{
			ItemStack stack = ItemStack.of(nbt.getCompound(key));
			if (stack != null && !stack.isEmpty())
				return stack;
			else return ItemStack.EMPTY;
		}
		else return ItemStack.EMPTY;
	}

	@Deprecated
	public static void saveEquipment(CompoundTag toTag, Mob inMob)
	{
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.HEAD), toTag, "nbt_helper_equipment_item_head");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.CHEST), toTag, "nbt_helper_equipment_item_chest");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.LEGS), toTag, "nbt_helper_equipment_item_legs");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.FEET), toTag, "nbt_helper_equipment_item_feet");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.MAINHAND), toTag, "nbt_helper_equipment_item_main_hand");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.OFFHAND), toTag, "nbt_helper_equipment_item_off_hand");
	}

	@Deprecated
	public static void readEquipment(Mob toMob, CompoundTag inTag)
	{
		toMob.setItemSlot(EquipmentSlot.HEAD, readItemStack(inTag, "nbt_helper_equipment_item_head"));
		toMob.setItemSlot(EquipmentSlot.CHEST, readItemStack(inTag, "nbt_helper_equipment_item_chest"));
		toMob.setItemSlot(EquipmentSlot.LEGS, readItemStack(inTag, "nbt_helper_equipment_item_legs"));
		toMob.setItemSlot(EquipmentSlot.FEET, readItemStack(inTag, "nbt_helper_equipment_item_feet"));
		toMob.setItemSlot(EquipmentSlot.MAINHAND, readItemStack(inTag, "nbt_helper_equipment_item_main_hand"));
		toMob.setItemSlot(EquipmentSlot.OFFHAND, readItemStack(inTag, "nbt_helper_equipment_item_off_hand"));
	}

	@Deprecated // Use NFUNBTStatics.TAG_XXX_ID constants instead
	public static enum TagType
	{
		   TAG_BYTE(1),
		   TAG_SHORT(2),
		   TAG_INT(3),
		   TAG_LONG(4),
		   TAG_FLOAT(5),
		   TAG_DOUBLE(6),
		   TAG_BYTE_ARRAY(7),
		   TAG_STRING(8),
		   TAG_LIST(9),
		   TAG_COMPOUND(10),
		   TAG_INT_ARRAY(11),
		   TAG_LONG_ARRAY(12),
		   TAG_ANY_NUMERIC(99);
		
		protected int id;
		
		private TagType(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
		
	}
	
	public static void putVec3(CompoundTag toTag, String key, Vec3 val)
	{
		ListTag listtag = new ListTag();

		listtag.add(DoubleTag.valueOf(val.x));
		listtag.add(DoubleTag.valueOf(val.y));
		listtag.add(DoubleTag.valueOf(val.z));

		toTag.put(key, listtag);
	}
	
	public static Vec3 getVec3(CompoundTag fromTag, String key)
	{
		ListTag listtag = fromTag.getList(key, 6);
		return new Vec3(listtag.getDouble(0), listtag.getDouble(1), listtag.getDouble(2));
	}
	
	// Shift a tag from old key to new key inside a compound tag.
	// For save data shifting after tag key change.
	// This is not to be removed, but just add a warning to every position calling this.
	@Deprecated
	public static void shiftNbtTag(CompoundTag nbt, String oldKey, String newKey)
	{
		if (nbt.contains(oldKey))
		{
			nbt.put(newKey, nbt.get(oldKey));
			nbt.remove(oldKey);
		}
	}
	
	/**
	 * Save a map into a compound tag.
	 * The map key must be serialized into string.
	 * @param <K> Map key type.
	 * @param <V> Map value type.
	 * @param keySerializer Function casting keys to string.
	 * @param valueSerializer Function casting values to tag.
	 * @return Result compound tag.
	 */
	public static <K, V> CompoundTag saveMap(Map<K, V> map, Function<K, String> keySerializer, Function<V, Tag> valueSerializer)
	{
		CompoundTag out = new CompoundTag();
		for (K k: map.keySet())
		{
			out.put(keySerializer.apply(k), valueSerializer.apply(map.get(k)));
		}
		return out;
	}
	
	/**
	 * Read a map from a compound tag in which the keys are sub-tag keys and values are sub-tags.
	 * @param <K> Map key type.
	 * @param <V> Map value type.
	 * @param readInto Map that the values will be read into. It will be cleared before reading.
	 * @param keyDeserializer Function casting sub-tag key string to map key object.
	 * @param valueDeserializer Function casting sub-tag to map value object.
	 * @param keyNonnull If true, the entry will be ignored if the casted map key is null.
	 * @param valueNonnull If true, the entry will be ignored if the casted map value is null.
	 * @return Result map. (HashMap)
	 */
	public static <K, V> void readMap(CompoundTag tag, Map<K, V> readInto, Function<String, K> keyDeserializer, Function<Tag, V> valueDeserializer, boolean keyNonnull, boolean valueNonnull)
	{
		readInto.clear();
		for (String str: tag.getAllKeys())
		{
			K key = keyDeserializer.apply(str);
			V value = valueDeserializer.apply(tag.get(str));
			if ((!keyNonnull || key != null) && (!valueNonnull || value != null))
				readInto.put(key, value);
		}
	}

	/**
	 * Read a map from a compound tag in which the keys are sub-tag keys and values are sub-tags.
	 * @param <K> Map key type.
	 * @param <V> Map value type.
	 * @param keyDeserializer Function casting sub-tag key string to map key object.
	 * @param valueDeserializer Function casting sub-tag to map value object.
	 * @param keyNonnull If true, the entry will be ignored if the casted map key is null.
	 * @param valueNonnull If true, the entry will be ignored if the casted map value is null.
	 * @return Result map. (HashMap)
	 */
	public static <K, V> Map<K, V> readMap(CompoundTag tag, Function<String, K> keyDeserializer, Function<Tag, V> valueDeserializer, boolean keyNonnull, boolean valueNonnull)
	{
		Map<K, V> map = new HashMap<K, V>();
		readMap(tag, map, keyDeserializer, valueDeserializer, keyNonnull, valueNonnull);
		return map;
	}
	
	/**
	 * Read a map from a compound tag in which the keys are sub-tag keys and values are sub-tags.
	 * If casted map key is null, the entry will be ignored. Values don't perform null check.
	 * @param <K> Map key type.
	 * @param <V> Map value type.
	 * @param keyDeserializer Function casting sub-tag key string to map key object.
	 * @param valueDeserializer Function casting sub-tag to map value object.
	 * @return Result map. (HashMap)
	 */
	public static <K, V> Map<K, V> readMap(CompoundTag tag, Function<String, K> keyDeserializer, Function<Tag, V> valueDeserializer)
	{
		return readMap(tag, keyDeserializer, valueDeserializer, true, false);
	}

	/**
	 * Reset the key of a subtag in a CompoundTag. If the old key isn't present, it will not do anything.
	 * @param inTag {@code CompoundTag} to be operated.
	 * @param allowsOverwrite If true, when the new key is present, it will be overwritten by the subtag under old key. Otherwise
	 * it will not do anything and the old key will <i>NOT</i> be removed.
	 */
	public static void resetKey(CompoundTag inTag, String oldKey, String newKey, boolean allowsOverwrite)
	{
		if (!inTag.contains(oldKey))
			return;
		if (inTag.contains(newKey) && !allowsOverwrite)
			return;
		Tag subtag = inTag.get(oldKey);
		inTag.put(newKey, subtag);
		inTag.remove(oldKey);
	}

	/**
	 * Reset the key of a subtag in a CompoundTag. If the old key isn't present or the new key is already occupied, it will not do anything.
	 * @param inTag {@code CompoundTag} to be operated.
	 */
	public static void resetKey(CompoundTag inTag, String oldKey, String newKey)
	{
		resetKey(inTag, oldKey, newKey, false);
	}

	public static <T> ListTag listTagFromIterable(Iterable<T> it, Function<T, Tag> saver)
	{
		ListTag res = new ListTag();
		for (T elem: it) {
			res.add(saver.apply(elem));
		}
		return res;
	}

	public static <T> Set<T> setFromListTag(ListTag listTag, Function<Tag, T> loader) {
		Set<T> res = new HashSet<>();
        for (Tag tag : listTag) {
            res.add(loader.apply(tag));
        }
		return res;
	}

	/**
	 * Use {@code list.stream().map(loader).collect(Collector.toList())} instead
	 */
	@Deprecated
	public static <T> List<T> listFromListTag(ListTag listTag, Function<Tag, T> loader) {
		List<T> res = new ArrayList<>();
		for (Tag tag : listTag) {
			res.add(loader.apply(tag));
		}
		return res;
	}

	public static <T> ListTag listTagFromList(List<T> list, Function<T, Tag> saver) {
		ListTag tag = new ListTag();
		for (T t: list) tag.add(saver.apply(t));
		return tag;
	}

	public static <K, V> Map<K, V> mapFromCompoundTag(CompoundTag nbt, Function<String, K> keyLoader, Function<Tag, V> valLoader) {
		Map<K, V> res = new HashMap<>();
		for (String key: nbt.getAllKeys()) {
			res.put(keyLoader.apply(key), valLoader.apply(nbt.get(key)));
		}
		return res;
	}

	public static <V> Map<String, V> stringMapFromCompoundTag(CompoundTag nbt, Function<Tag, V> valLoader) {
		return mapFromCompoundTag(nbt, s -> s, valLoader);
	}

	public static <K, V> CompoundTag compoundTagFromMap(Map<K, V> map, Function<K, String> keySaver, Function<V, Tag> valSaver) {
		CompoundTag nbt = new CompoundTag();
		for (var entry: map.entrySet()) {
			nbt.put(keySaver.apply(entry.getKey()), valSaver.apply(entry.getValue()));
		}
		return nbt;
	}

	public static <V> CompoundTag compoundTagFromStringMap(Map<String, V> map, Function<V, Tag> valSaver) {
		return compoundTagFromMap(map, s -> s, valSaver);
	}

	public static <T> CompoundTag compoundTagFromIterable(Iterable<T> iterable, Function<T, String> toKey, Function<T, Tag> toValue) {
		CompoundTag nbt = new CompoundTag();
		for (T t: iterable) {
			nbt.put(toKey.apply(t), toValue.apply(t));
		}
		return nbt;
	}

}