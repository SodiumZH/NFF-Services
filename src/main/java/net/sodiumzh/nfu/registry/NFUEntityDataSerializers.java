package net.sodiumzh.nfu.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.math.Field3D;
import net.sodiumzh.nfu.math.Inequality3D;
import net.sodiumzh.nfu.math.LinearColor;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import net.sodiumzh.nfu.network.NFUDataSerializers;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Field;
import java.util.*;

public class NFUEntityDataSerializers
{
	public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = 
			DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, NFULibrary.MOD_ID);

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

	public static final RegistryObject<EntityDataSerializer<AABB>> BOUNDING_BOX =
		SERIALIZERS.register("bounding_box", () -> fromNFUSerializer(NFUDataSerializers.BOUNDING_BOX));
	public static final RegistryObject<EntityDataSerializer<Optional<AABB>>> OPTIONAL_BOUNDING_BOX =
		SERIALIZERS.register("optional_bounding_box", () -> optionalOf(BOUNDING_BOX.get()));
	public static final RegistryObject<EntityDataSerializer<Inequality3D>> INEQUALITY_3D =
		SERIALIZERS.register("inequality_3d", () -> fromNFUSerializer(NFUDataSerializers.INEQUALITY_3D));
	public static final RegistryObject<EntityDataSerializer<Optional<Inequality3D>>> OPTIONAL_INEQUALITY_3D =
		SERIALIZERS.register("optional_inequality_3d", () -> optionalOf(INEQUALITY_3D.get()));
	public static final RegistryObject<EntityDataSerializer<Field3D>> FIELD_3D =
		SERIALIZERS.register("field_3d", () -> fromNFUSerializer(NFUDataSerializers.FIELD_3D));
	public static final RegistryObject<EntityDataSerializer<Optional<Field3D>>> OPTIONAL_FIELD_3D =
		SERIALIZERS.register("optional_field_3d", () -> optionalOf(FIELD_3D.get()));

	public static <T> EntityDataSerializer<T> fromNFUSerializer(NFUDataSerializer<T> serializer) {
		return serializer.asEntityDataSerializer();
	}

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
