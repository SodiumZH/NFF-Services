package net.sodiumstudio.nautils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NbtHelper {

	public static final int TAG_BYTE_ID = 1;
	public static final int TAG_SHORT_ID = 2;
	public static final int TAG_INT_ID = 3;
	public static final int TAG_LONG_ID = 4;
	public static final int TAG_FLOAT_ID = 5;
	public static final int TAG_DOUBLE_ID = 6;
	public static final int TAG_BYTE_ARRAY_ID = 7;
	public static final int TAG_STRING_ID = 8;
	public static final int TAG_LIST_ID = 9;
	public static final int TAG_COMPOUND_ID = 10;
	public static final int TAG_INT_ARRAY_ID = 11;
	public static final int TAG_LONG_ARRAY_ID = 12;
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

	// Check if a compound tag contains a compound subtag with player's string uuid as key.
	public static boolean containsPlayer(CompoundTag inTag, Player player)
	{
		// Fix unknown crash on player die
		if (player == null) return false;
		return inTag.contains(player.getStringUUID()) 
				&& (inTag.get(player.getStringUUID()) instanceof CompoundTag);
	}
	
	// Check if a compound tag contains a player's string uuid as subtag, and a tag with given key under it
	public static boolean containsPlayerData(CompoundTag inTag, Player player, String key)
	{
		return containsPlayer(inTag, player) && inTag.getCompound(player.getStringUUID()).contains(key);
	}
	
	public static HashSet<Player> getAllValidPlayersContaining(CompoundTag inTag, Entity levelContext)
	{
		HashSet<Player> res = new HashSet<Player>();
		for (Player player: levelContext.level().players())
		{
			if (containsPlayer(inTag, player))
				res.add(player);
		}
		return res;
	}
	
	// Get tag if a compound tag has a player-uuid-named subtag which contains subtag with given key
	// Return null if not present
	public static Tag getPlayerData(CompoundTag inTag, Player player, String key)
	{
		return containsPlayerData(inTag, player, key) ? inTag.getCompound(player.getStringUUID()).get(key) : null;
	}
	
	public static void putPlayerData(Tag inTag, CompoundTag putTo, Player player, String key)
	{
		// If missing player data, add
		if (!containsPlayer(putTo, player))
			putTo.put(player.getStringUUID(), new CompoundTag());
		/**
		 * If missing player data string label, put it
		 * This value is ONLY used on iteration of the parent cmpd tag to find potential player data without player in level
		 */
		if (!putTo.getCompound(player.getStringUUID()).contains("player_uuid_string", TAG_STRING_ID))
			putTo.getCompound(player.getStringUUID()).putString("player_uuid_string", player.getStringUUID());
		// Finally put
		putTo.getCompound(player.getStringUUID()).put(key, inTag);
	}
	
	public static void removePlayerData(CompoundTag removeFrom, Player player, String key)
	{
		if (containsPlayerData(removeFrom, player, key))
		{
			removeFrom.getCompound(player.getStringUUID()).remove(key);
		}
		// When the data is the last one, remove the whole player data tag
		if (removeFrom.getCompound(player.getStringUUID()).isEmpty())
		{
			removeFrom.remove(player.getStringUUID());
		}
	}
	
	public static CompoundTag saveItemStack(@Nullable ItemStack stack, @Nonnull CompoundTag saveTo, String key) {
		CompoundTag newTag = new CompoundTag();
		if (stack == null || stack.isEmpty())
			ItemStack.EMPTY.save(newTag);
		else
			stack.save(newTag);
		saveTo.put(key, newTag);
		return newTag;
	}
	
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
	
	public static void saveEquipment(CompoundTag toTag, Mob inMob)
	{
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.HEAD), toTag, "nbt_helper_equipment_item_head");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.CHEST), toTag, "nbt_helper_equipment_item_chest");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.LEGS), toTag, "nbt_helper_equipment_item_legs");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.FEET), toTag, "nbt_helper_equipment_item_feet");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.MAINHAND), toTag, "nbt_helper_equipment_item_main_hand");
		saveItemStack(inMob.getItemBySlot(EquipmentSlot.OFFHAND), toTag, "nbt_helper_equipment_item_off_hand");
	}
	
	public static void readEquipment(Mob toMob, CompoundTag inTag)
	{
		toMob.setItemSlot(EquipmentSlot.HEAD, readItemStack(inTag, "nbt_helper_equipment_item_head"));
		toMob.setItemSlot(EquipmentSlot.CHEST, readItemStack(inTag, "nbt_helper_equipment_item_chest"));
		toMob.setItemSlot(EquipmentSlot.LEGS, readItemStack(inTag, "nbt_helper_equipment_item_legs"));
		toMob.setItemSlot(EquipmentSlot.FEET, readItemStack(inTag, "nbt_helper_equipment_item_feet"));
		toMob.setItemSlot(EquipmentSlot.MAINHAND, readItemStack(inTag, "nbt_helper_equipment_item_main_hand"));
		toMob.setItemSlot(EquipmentSlot.OFFHAND, readItemStack(inTag, "nbt_helper_equipment_item_off_hand"));
	}
	
	@Deprecated // Use NbtHelper.TAG_XXX_ID constants instead
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
	
	public static Player getPlayerFromKey(CompoundTag fromTag, String key, Level level)
	{
		if (level == null)
			return null;
		else if (!fromTag.contains(key, 11))
			return null;
		return level.getPlayerByUUID(fromTag.getUUID(key));
	}
	
	public static void putPlayerToKey(Player player, CompoundTag toTag, String key)
	{
		if (player == null)
			return;
		toTag.putUUID(key, player.getUUID());
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

}