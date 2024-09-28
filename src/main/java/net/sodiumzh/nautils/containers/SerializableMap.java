package net.sodiumzh.nautils.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;

/**
 * A HashMap that can be serialized into an deserialized from a CompoundTag.
 * @param <K>
 * @param <V>
 */
public class SerializableMap<K, V> extends HashMap<K, V> implements INBTSerializable<CompoundTag>
{

	private static final long serialVersionUID = -531421098551161655L;
	
	protected Function<K, String> keySerializer;
	protected Function<V, Tag> valueSerializer;
	protected Function<String, K> keyDeserializer;
	protected Function<Tag, V> valueDeserializer;
	
	public SerializableMap(Function<K, String> keySerializer, Function<V, Tag> valueSerializer,
			Function<String, K> keyDeserializer, Function<Tag, V> valueDeserializer)
	{
		super();
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.keyDeserializer = keyDeserializer;
		this.valueDeserializer = valueDeserializer;
	}
	
	public SerializableMap<K, V> copyFrom(Map<K, V> other)
	{
		this.clear();
		for(K k: other.keySet())
		{
			this.put(k, other.get(k));
		}
		return this;
	}
	
	public SerializableMap<K, V> add(K key, V value)
	{
		put(key, value);
		return this;
	}

	@Override
	public CompoundTag serializeNBT() {
		return NaUtilsNBTStatics.saveMap(this, keySerializer, valueSerializer);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		NaUtilsNBTStatics.readMap(nbt, this, keyDeserializer, valueDeserializer, true, false);
	}

}
