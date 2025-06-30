package net.sodiumzh.nfu.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.math.LinearColor;

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
}
