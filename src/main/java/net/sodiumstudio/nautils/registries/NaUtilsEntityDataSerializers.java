package net.sodiumstudio.nautils.registries;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumstudio.nautils.NaUtils;
import net.sodiumstudio.nautils.math.LinearColor;

public class NaUtilsEntityDataSerializers
{
	public static final DeferredRegister<EntityDataSerializer<?>> REG = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, NaUtils.MOD_ID);

	public static final RegistryObject<EntityDataSerializer<LinearColor>> LINEAR_COLOR = REG.register("linear_color", () -> 
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
