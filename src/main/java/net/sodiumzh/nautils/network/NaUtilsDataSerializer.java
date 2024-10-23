package net.sodiumzh.nautils.network;

import com.google.common.base.Function;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.registries.NaUtilsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
/**
 * Defines a data type that can serialized both into nbt and FriendlyByteBuf.
 */

public interface NaUtilsDataSerializer<T>
{
	public static NaUtilsRegistry<NaUtilsDataSerializer<?>> getRegistry()
	{
		return NaUtilsRegistries.DATA_SERIALIZERS;
	}

	public static NaUtilsDataSerializer<?> getFromRegistry(ResourceLocation key)
	{
		return NaUtilsRegistries.DATA_SERIALIZERS.getValue(key);
	}

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
		try {
			type.write(buf, (O)obj);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(String.format("NaUtilsDataSerializer#writeUnchecked: cast failed. Attempting to cast \"%s\" to \"%s\".", 
					obj.getClass().getSimpleName(), type.getClass().getSimpleName()), e);
		}
	}
	
	/**
	 * Type-unchecked version of {@code toTag}. Useful if the type is unknown.
	 * Take care and ensure that the object type matches the data type.
	 */
	@SuppressWarnings("unchecked")
	public static <O> Tag toTagUnchecked(NaUtilsDataSerializer<O> type, Object obj)
	{
		try {
			return type.toTag((O)obj);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(String.format("NaUtilsDataSerializer#toTagUnchecked: cast failed. Attempting to cast \"%s\" to \"%s\".", 
					obj.getClass().getSimpleName(), type.getClass().getSimpleName()), e);
		}
	}
	
	public static NaUtilsDataSerializer<?> fromId(ResourceLocation key)
	{
		NaUtilsDataSerializer<?> res = getFromRegistry(key);
		if (res == null)
			throw new IllegalStateException(String.format("NaUtilsDataSerializer: missing serializer \"%s\". If you are using custom data serializers, ensure the related classes are loaded on mod initialization!", 
					key.toString()));
		return res;
	}
	
	// Construction Utils //
	
	/**
	 * Create an instance with serialization/deserialization methods.
	 * <p>Note: {@code NaUtilsDataSerializer} is auto-registered on create. <u>Ensure the class where the
	 * serializers are defined is loaded on mod initialization!</u> 
	 * (E.g. by calling the class owning the instances somehow in the mod main class constructor)
	 */
	public static <O> NaUtilsDataSerializer<O> create(Class<O> objClass,
			BiConsumer<FriendlyByteBuf, O> write, Function<FriendlyByteBuf, O> read,
			Function<O, Tag> toTag, Function<Tag, O> fromTag)
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
				return getRegistry().getKey(this);
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
				return fromTag.apply(t);
			}
			@Override
			public String toString()
			{
				return String.format("NaUtilsDataSerializer<%s>", this.getKey().toString());
			}
		};
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static <O, T extends Tag> NaUtilsDataSerializer<O> create(Class<O> objClass, Class<T> tagClass,
			BiConsumer<FriendlyByteBuf, O> write, Function<FriendlyByteBuf, O> read,
			Function<O, T> toTag, Function<T, O> fromTag)
	{
		return create(objClass, write, read, o -> toTag.apply(o), t -> fromTag.apply((T)t));
	}
	
	/**
	 * Create a list serializer from element serializer.
	 * <p>Note: {@code NaUtilsDataSerializer} is auto-registered on create. <u>Ensure the class where the
	 * serializers are defined is loaded on mod initialization!</u> 
	 * (E.g. by calling the class owning the instances somehow in the mod main class constructor)
	 */
	@SuppressWarnings("unchecked")
	public static <O> NaUtilsDataSerializer<List<O>> listOf(final NaUtilsDataSerializer<O> original)
	{
		Class<?> clazz = List.class;
		return NaUtilsDataSerializer.create((Class<List<O>>)clazz, ListTag.class,
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
	
	public static <A, B> NaUtilsDataSerializer<B> castTo(Class<B> clazzB,
			NaUtilsDataSerializer<A> original, Function<A, B> aToB, Function<B, A> bToA)
	{
		return NaUtilsDataSerializer.create(clazzB,
				(buf, b) -> original.write(buf, bToA.apply(b)),
				(buf) -> aToB.apply(original.read(buf)),
				b -> original.toTag(bToA.apply(b)),
				t -> aToB.apply(original.fromTag(t)));
	}

}
