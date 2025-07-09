package net.sodiumzh.nfu.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.math.LinearColor;

import javax.swing.text.html.parser.Entity;
import java.util.*;

public class NFUEntityDataSerializers
{
	public static final DeferredRegister<DataSerializerEntry> SERIALIZERS = 
			DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, NFULibrary.MOD_ID);

	public static final RegistryObject<DataSerializerEntry> LINEAR_COLOR = SERIALIZERS.register("linear_color", () -> new DataSerializerEntry(
			new EntityDataSerializer<LinearColor>()
			{
				@Override
			    public void write(FriendlyByteBuf buf, LinearColor color) {
						buf.writeDouble(color.r);
						buf.writeDouble(color.g);
						buf.writeDouble(color.b);
			    }
				
				@Override
			    public LinearColor read(FriendlyByteBuf buf) {
						double r = buf.readDouble();
						double g = buf.readDouble();
						double b = buf.readDouble();
						return LinearColor.fromNormalized(r, g, b);
			    }
				
				@Override
			    public LinearColor copy(LinearColor color) {
			    	   return color;
			    }
			}));

	public static final RegistryObject<EntityDataSerializer<Double>> DOUBLE = SERIALIZERS.register("double", () ->
		EntityDataSerializer.simple(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble));
	public static final RegistryObject<EntityDataSerializer<Vec3>> VEC3 = SERIALIZERS.register("vec3", () ->
		EntityDataSerializer.simple((buf, val) -> {
			buf.writeDouble(val.x);
			buf.writeDouble(val.y);
			buf.writeDouble(val.z);
		}, buf -> {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			return new Vec3(x, y, z);
		}));
	public static final RegistryObject<EntityDataSerializer<LinearColor>> LINEAR_COLOR = SERIALIZERS.register("linear_color", () -> 
		EntityDataSerializer.simple((buf, color) -> {
			buf.writeDouble(color.r);
			buf.writeDouble(color.g);
			buf.writeDouble(color.b);
		}, buf -> {
			double r = buf.readDouble();
			double g = buf.readDouble();
			double b = buf.readDouble();
			return LinearColor.fromNormalized(r, g, b);
		}));

	public static <T> EntityDataSerializer<Optional<T>> optionalOf(EntityDataSerializer<T> original) {
		return EntityDataSerializer.optional(original::write, original::read);
	}

	public static <T> EntityDataSerializer<List<T>> listOf(EntityDataSerializer<T> original) {
		return EntityDataSerializer.simple((buf, list) -> {
			buf.writeInt(list.size());
			list.forEach(e -> original.write(buf, e));
		}, buf -> {
			int size = buf.readInt();
			List<T> res = new ArrayList<>(size * 2);
			for (int i = 0; i < size; ++i)
				res.add(original.read(buf));
			return res;
		});
	}

	public static <K, V> EntityDataSerializer<Map<K, V>> mapOf(EntityDataSerializer<K> keySerializer, EntityDataSerializer<V> valSerializer) {
		return EntityDataSerializer.simple((buf, map) -> {
			buf.writeInt(map.size());
			map.entrySet().forEach(entry -> {
				keySerializer.write(buf, entry.getKey());
				valSerializer.write(buf, entry.getValue());
			});
		}, buf -> {
			int size = buf.readInt();
			Map<K, V> res = new HashMap<>();
			for (int i = 0; i < size; ++i) {
				K key = keySerializer.read(buf);
				V val = valSerializer.read(buf);
				res.put(key, val);
			}
			return res;
		});
	}

}
