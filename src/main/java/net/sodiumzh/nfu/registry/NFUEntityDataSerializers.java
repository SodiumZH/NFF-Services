package net.sodiumzh.nfu.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.registries.DataSerializerEntry;
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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class NFUEntityDataSerializers
{
	public static final EntityDataSerializer<Double> DOUBLE =
		create(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
	public static final EntityDataSerializer<Vec3> VEC3 =
		fromNFUSerializer(NFUDataSerializers.VEC3);
	public static final EntityDataSerializer<LinearColor> LINEAR_COLOR =
		fromNFUSerializer(NFUDataSerializers.LINEAR_COLOR);
	public static final EntityDataSerializer<AABB> BOUNDING_BOX =
		fromNFUSerializer(NFUDataSerializers.BOUNDING_BOX);
	public static final EntityDataSerializer<Optional<AABB>> OPTIONAL_BOUNDING_BOX =
		optionalOf(BOUNDING_BOX);
	public static final EntityDataSerializer<Inequality3D> INEQUALITY_3D =
		fromNFUSerializer(NFUDataSerializers.INEQUALITY_3D);
	public static final EntityDataSerializer<Optional<Inequality3D>> OPTIONAL_INEQUALITY_3D =
		optionalOf(INEQUALITY_3D);
	public static final EntityDataSerializer<Field3D> FIELD_3D =
		fromNFUSerializer(NFUDataSerializers.FIELD_3D);
	public static final EntityDataSerializer<Optional<Field3D>> OPTIONAL_FIELD_3D =
		optionalOf(FIELD_3D);

	public static <T> EntityDataSerializer<T> fromNFUSerializer(NFUDataSerializer<T> serializer) {
		return serializer.asEntityDataSerializer();
	}

	public static <T> EntityDataSerializer<Optional<T>> optionalOf(EntityDataSerializer<T> original) {
		return create((buf, opt) -> {
			buf.writeBoolean(opt.isPresent());
            opt.ifPresent(t -> original.write(buf, t));
		},  buf -> {
			boolean isPresent = buf.readBoolean();
			if (!isPresent) return Optional.empty();
			return Optional.of(original.read(buf));
		});
	}

	public static <T> EntityDataSerializer<List<T>> listOf(EntityDataSerializer<T> original) {
		return create((buf, list) -> {
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
		return create((buf, map) -> {
			buf.writeInt(map.size());
			map.forEach((key, value) -> {
                keySerializer.write(buf, key);
                valSerializer.write(buf, value);
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

	public static <T> EntityDataSerializer<T> create(
		BiConsumer<FriendlyByteBuf, T> write, Function<FriendlyByteBuf, T> read, UnaryOperator<T> copy) {
		return new EntityDataSerializer<T>() {
			@Override
			public void write(FriendlyByteBuf pBuffer, T pValue) {
				write.accept(pBuffer, pValue);
			}

			@Override
			public T read(FriendlyByteBuf pBuffer) {
				return read.apply(pBuffer);
			}

			@Override
			public T copy(T pValue) {
				return copy.apply(pValue);
			}
		};
	}

	public static <T> EntityDataSerializer<T> create(
		BiConsumer<FriendlyByteBuf, T> write, Function<FriendlyByteBuf, T> read) {
		return create(write, read, t -> t);
	}

	public static class Register {

		public static final DeferredRegister<DataSerializerEntry> SERIALIZERS =
			DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, NFULibrary.MOD_ID);

		public static final RegistryObject<DataSerializerEntry> DOUBLE =
			SERIALIZERS.register("double", () -> new DataSerializerEntry(NFUEntityDataSerializers.DOUBLE));
		public static final RegistryObject<DataSerializerEntry> VEC3 =
			SERIALIZERS.register("vec3", () -> new DataSerializerEntry(NFUEntityDataSerializers.VEC3));
		public static final RegistryObject<DataSerializerEntry> LINEAR_COLOR =
			SERIALIZERS.register("linear_color", () -> new DataSerializerEntry(NFUEntityDataSerializers.LINEAR_COLOR));
		public static final RegistryObject<DataSerializerEntry> BOUNDING_BOX =
			SERIALIZERS.register("bounding_box", () -> new DataSerializerEntry(NFUEntityDataSerializers.BOUNDING_BOX));
		public static final RegistryObject<DataSerializerEntry> OPTIONAL_BOUNDING_BOX =
			SERIALIZERS.register("optional_bounding_box", () -> new DataSerializerEntry(NFUEntityDataSerializers.OPTIONAL_BOUNDING_BOX));
		public static final RegistryObject<DataSerializerEntry> INEQUALITY_3D =
			SERIALIZERS.register("inequality_3d", () -> new DataSerializerEntry(NFUEntityDataSerializers.INEQUALITY_3D));
		public static final RegistryObject<DataSerializerEntry> OPTIONAL_INEQUALITY_3D =
			SERIALIZERS.register("optional_inequality_3d", () -> new DataSerializerEntry(NFUEntityDataSerializers.OPTIONAL_INEQUALITY_3D));
		public static final RegistryObject<DataSerializerEntry> FIELD_3D =
			SERIALIZERS.register("field_3d", () -> new DataSerializerEntry(NFUEntityDataSerializers.FIELD_3D));
		public static final RegistryObject<DataSerializerEntry> OPTIONAL_FIELD_3D =
			SERIALIZERS.register("optional_field_3d", () -> new DataSerializerEntry(NFUEntityDataSerializers.OPTIONAL_FIELD_3D));
	}

}
