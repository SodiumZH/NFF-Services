package net.sodiumstudio.nautils.nbt;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;

@Deprecated
public interface SerializableField<T>
{
	public T get();
	public Tag serialize();
	
	public static record Byte(byte val) implements SerializableField<java.lang.Byte>
	{
		@Override
		public java.lang.Byte get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return ByteTag.valueOf(val);
		}
	}
	
	public static record Short(short val) implements SerializableField<java.lang.Short>
	{
		@Override
		public java.lang.Short get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return ShortTag.valueOf(val);
		}
	}
	
	public static record Int(int val) implements SerializableField<Integer>
	{
		@Override
		public Integer get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return IntTag.valueOf(val);
		}
	}
	
	public static record Long(long val) implements SerializableField<java.lang.Long>
	{
		@Override
		public java.lang.Long get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return LongTag.valueOf(val);
		}
	}
	
	public static record Float(float val) implements SerializableField<java.lang.Float>
	{
		@Override
		public java.lang.Float get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return FloatTag.valueOf(val);
		}
	}
	
	public static record Double(double val) implements SerializableField<java.lang.Double>
	{
		@Override
		public java.lang.Double get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return DoubleTag.valueOf(val);
		}
	}
	
	public static record ByteArray(byte[] val) implements SerializableField<byte[]>
	{
		@Override
		public byte[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return new ByteArrayTag(val);
		}
	}
	
	public static record String(java.lang.String val) implements SerializableField<java.lang.String>
	{
		@Override
		public java.lang.String get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return StringTag.valueOf(val);
		}
	}
	
	public static record Compound(NBTSerializableMap val) implements SerializableField<NBTSerializableMap>
	{
		@Override
		public NBTSerializableMap get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return val.serializeNBT();
		}
	}
	
	public static record IntArray(int[] val) implements SerializableField<int[]>
	{
		@Override
		public int[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return new IntArrayTag(val);
		}
	}
	
	public static record LongArray(long[] val) implements SerializableField<long[]>
	{
		@Override
		public long[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			return new LongArrayTag(val);
		}
	}
	
	// Some arrays
	public static record FloatArray(float[] val) implements SerializableField<float[]>
	{
		@Override
		public float[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			ListTag res = new ListTag();
			for (float v: val)
				res.add(FloatTag.valueOf(v));
			return res;
		}
	}
	
	public static record DoubleArray(double[] val) implements SerializableField<double[]>
	{
		@Override
		public double[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			ListTag res = new ListTag();
			for (double v: val)
				res.add(DoubleTag.valueOf(v));
			return res;
		}
	}
	
	public static record StringArray(java.lang.String[] val) implements SerializableField<java.lang.String[]>
	{
		@Override
		public java.lang.String[] get() {
			return val;
		}
		@Override
		public Tag serialize() {
			ListTag res = new ListTag();
			for (java.lang.String v: val)
				res.add(StringTag.valueOf(v));
			return res;
		}
	}
	
	public static record ItemStack(net.minecraft.world.item.ItemStack val) implements SerializableField<net.minecraft.world.item.ItemStack>
	{
		
		@Override
		public net.minecraft.world.item.ItemStack get() {
			return val;
		}

		@Override
		public Tag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("SERIALIZABLE_FIELD_TYPE", "ITEM_STACK");
			CompoundTag valueTag = new CompoundTag();
			tag.put("SERIALIZABLE_FIELD_VALUE", valueTag);
			return tag;
		}
		
		public static boolean isItemStack(CompoundTag tag)
		{
			return tag.contains("SERIALIZABLE_FIELD_TYPE", Tag.TAG_STRING) && tag.getString("SERIALIZABLE_FIELD_TYPE").equals("ITEM_STACK")
					&& tag.contains("SERIALIZABLE_FIELD_VALUE", Tag.TAG_COMPOUND);
		}
		
		public static ItemStack deserialize(CompoundTag tag)
		{
			if (isItemStack(tag))
				return new ItemStack(net.minecraft.world.item.ItemStack.of(tag.getCompound("SERIALIZABLE_FIELD_VALUE")));
			else throw new RuntimeException();
		}
	}
	
	public static record UUID(java.util.UUID val) implements SerializableField<java.util.UUID>
	{
		
		@Override
		public java.util.UUID get() {
			return val;
		}

		@Override
		public Tag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("SERIALIZABLE_FIELD_TYPE", "UUID");
			tag.putUUID("SERIALIZABLE_FIELD_VALUE", val);
			return tag;
		}
		
		public static boolean isUUID(CompoundTag tag)
		{
			return tag.contains("SERIALIZABLE_FIELD_TYPE", Tag.TAG_STRING) && tag.getString("SERIALIZABLE_FIELD_TYPE").equals("UUID")
					&& tag.hasUUID("SERIALIZABLE_FIELD_VALUE");
		}
		
		public static UUID deserialize(CompoundTag tag)
		{
			if (isUUID(tag))
				return new UUID(tag.getUUID("SERIALIZABLE_FIELD_VALUE"));
			else throw new RuntimeException();
		}
	}
	
	/** Check if a CompoundTag should be parsed to a special type other than {@code SerializableField#Compound}.
	 * Returns a tuple containing the special type identifier as string and the value as tag. Or {@code null} if it's not special.
	 */
	@Nullable
	public static Tuple<java.lang.String, Tag> getSpecialCompoundType(CompoundTag tag)
	{
		if (tag.contains("SERIALIZABLE_FIELD_TYPE", Tag.TAG_STRING) && tag.contains("SERIALIZABLE_FIELD_VALUE"))
		{
			return new Tuple<>(tag.getString("SERIALIZABLE_FIELD_TYPE"), tag.get("SERIALIZABLE_FIELD_VALUE"));
		}
		else return null;
	}
	
}
