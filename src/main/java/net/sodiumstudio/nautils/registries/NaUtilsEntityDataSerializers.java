package net.sodiumstudio.nautils.registries;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumstudio.nautils.NaUtils;
import net.sodiumstudio.nautils.math.LinearColor;

public class NaUtilsEntityDataSerializers
{
	public static final DeferredRegister<DataSerializerEntry> SERIALIZERS = 
			DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, NaUtils.MOD_ID);

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
}
