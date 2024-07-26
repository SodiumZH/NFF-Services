package net.sodiumstudio.nautils.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.google.common.base.Function;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.sodiumstudio.nautils.NaUtils;

/**
 * Defines a data type that can serialized both into nbt and FriendlyByteBuf.
 */
public interface NaUtilsDataSerializer<T>
{
	public static final Map<ResourceLocation, NaUtilsDataSerializer<?>> TYPES = new HashMap<>();
	
	public Class<T> getObjectClass();
	public ResourceLocation getKey();
	/** Write the data to buf. */
	public void write(FriendlyByteBuf buf, T obj);
	/** Get value from buf. */
	public T read(FriendlyByteBuf buf);
	public Tag toTag(T obj);
	public T fromTag(Tag tag);
	
	/**
	 * Type-unchecked version of {@code write}. Useful if the type is unknown.
	 * Take care and ensure that the object type matches the data type.
	 */
	@SuppressWarnings("unchecked")
	public static <O> void writeUnchecked(NaUtilsDataSerializer<O> type, FriendlyByteBuf buf, Object obj)
	{
		type.write(buf, (O)obj);
	}
	
	/**
	 * Type-unchecked version of {@code toTag}. Useful if the type is unknown.
	 * Take care and ensure that the object type matches the data type.
	 */
	@SuppressWarnings("unchecked")
	public static <O> Tag toTagUnchecked(NaUtilsDataSerializer<O> type, Object obj)
	{
		return type.toTag((O)obj);
	}
	
	public static NaUtilsDataSerializer<?> fromId(ResourceLocation key)
	{
		return TYPES.get(key);
	}
	
	// Construction Utils //
	
	public static <O, T extends Tag> NaUtilsDataSerializer<O> create(ResourceLocation key, Class<O> objClass, Class<T> tagClass, 
			BiConsumer<FriendlyByteBuf, O> write, Function<FriendlyByteBuf, O> read,
			Function<O, T> toTag, Function<T, O> fromTag)
	{
		NaUtilsDataSerializer<O> res = new NaUtilsDataSerializer<O>()
		{
			@Override
			public Class<O> getObjectClass()
			{
				return objClass;
			}
			@Override
			public ResourceLocation getKey()
			{
				return key;
			}
			@Override
			public void write(FriendlyByteBuf buf, O obj)
			{
				write.accept(buf, obj);
			}
			@Override
			public O read(FriendlyByteBuf buf)
			{
				return read.apply(buf);
			}
			@Override
			public Tag toTag(O obj)
			{
				return toTag.apply(obj);
			}
			@Override
			public O fromTag(Tag t)
			{
				if (tagClass.isAssignableFrom(t.getClass()))
				{
					return fromTag.apply((T)t);
				}
				else throw new RuntimeException("NaUtilsDataSerializer: serialization and deserialization NBT type error.");
			}
		};
		TYPES.put(key, res);
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static <O> NaUtilsDataSerializer<List<O>> listOf(ResourceLocation key, final NaUtilsDataSerializer<O> original)
	{
		Class<?> clazz = List.class;
		return NaUtilsDataSerializer.create(key, (Class<List<O>>)clazz, ListTag.class, 
				(b, o) -> {
					b.writeInt(o.size());
					for (int i = 0; i < o.size(); ++i)
						original.write(b, o.get(i));
				}, (b) -> {
					int size = b.readInt();
					List<O> list = new ArrayList<>();
					for (int i = 0; i < size; ++i)
						list.add(original.read(b));
					return list;
				}, (o) -> {
					ListTag t = new ListTag();
					for (int i = 0; i < o.size(); ++i)
						t.add(original.toTag(o.get(i)));
					return t;
				}, (t) -> {
					List<O> list = new ArrayList<>();
					for (int i = 0; i < t.size(); ++i)
						list.add(original.fromTag(t.get(i)));
					return list;
				});
	}
	
	public static <O> NaUtilsDataSerializer<List<O>> listOf(final NaUtilsDataSerializer<O> original)
	{
		return listOf(new ResourceLocation(original.getKey().getNamespace(), original.getKey().getPath() + "_list"), original);
	}
	
	public static final NaUtilsDataSerializer<Boolean> BOOLEAN = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "boolean"), Boolean.class, ByteTag.class,
			FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean, ByteTag::valueOf, t -> t.getAsByte() != 0);
	public static final NaUtilsDataSerializer<Integer> INT = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "int"), Integer.class, IntTag.class,
			FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt, IntTag::valueOf, IntTag::getAsInt);
	public static final NaUtilsDataSerializer<Long> LONG = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "long"), Long.class, LongTag.class,
			FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong, LongTag::valueOf, LongTag::getAsLong);
	public static final NaUtilsDataSerializer<Double> DOUBLE = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "double"), Double.class, DoubleTag.class,
			FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble, DoubleTag::valueOf, DoubleTag::getAsDouble);
	public static final NaUtilsDataSerializer<UUID> UUID = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "uuid"), UUID.class, IntArrayTag.class,
			FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID, NbtUtils::createUUID, NbtUtils::loadUUID);
	public static final NaUtilsDataSerializer<String> STRING = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "string"), String.class, StringTag.class,
			FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf, StringTag::valueOf, StringTag::getAsString);
	public static final NaUtilsDataSerializer<int[]> INT_ARRAY = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "int_array"), int[].class, IntArrayTag.class,
			(b, o) -> {
				b.writeInt(o.length);
				for (int i = 0; i < o.length; ++i)
					b.writeInt(o[i]);
			}, b -> {
				int l = b.readInt();
				int[] res = new int[l];
				for (int i = 0; i < l; ++i)
					res[i] = b.readInt();
				return res;
			}, IntArrayTag::new, IntArrayTag::getAsIntArray);
	public static final NaUtilsDataSerializer<double[]> DOUBLE_ARRAY = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "double_array"), double[].class, ListTag.class,
			(b, o) -> {
				b.writeInt(o.length);
				for (int i = 0; i < o.length; ++i)
					b.writeDouble(o[i]);
			}, b -> {
				int l = b.readInt();
				double[] res = new double[l];
				for (int i = 0; i < l; ++i)
					res[i] = b.readDouble();
				return res;
			}, o -> {
				ListTag tag = new ListTag();
				for (int i = 0; i < o.length; ++i)
					tag.add(DoubleTag.valueOf(o[i]));
				return tag;
			}, t -> {
				double[] res = new double[t.size()];
				for (int i = 0; i < t.size(); ++i)
					res[i] = t.getDouble(i);
				return res;
			});
	public static final NaUtilsDataSerializer<Vec3> VEC3 = NaUtilsDataSerializer.create(
			new ResourceLocation(NaUtils.MOD_ID_FINAL, "vec3"), Vec3.class, ListTag.class,
			(b, o) -> {b.writeDouble(o.x); b.writeDouble(o.y); b.writeDouble(o.z);},
			(b) -> new Vec3(b.readDouble(), b.readDouble(), b.readDouble()),
			(o) -> {
				ListTag listtag = new ListTag();
				listtag.add(DoubleTag.valueOf(o.x));
				listtag.add(DoubleTag.valueOf(o.y));
				listtag.add(DoubleTag.valueOf(o.z));
				return listtag;
			}, (t) -> new Vec3(t.getDouble(0), t.getDouble(1), t.getDouble(2)));
}
