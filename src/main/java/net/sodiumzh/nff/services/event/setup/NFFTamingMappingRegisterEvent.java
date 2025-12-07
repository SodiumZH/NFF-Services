package net.sodiumzh.nff.services.event.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class NFFTamingMappingRegisterEvent extends Event implements IModBusEvent {
	
	public void register(@Nonnull ResourceLocation from, @Nonnull ResourceLocation convertTo,
						 @Nonnull Supplier<NFFTamingProcess> processSupplier, boolean override)
	{
		NFFTamingMapping.register(from, convertTo, processSupplier, override);
	}
	
	public void register(@Nonnull ResourceLocation fromType, @Nonnull ResourceLocation convertToType,
						 @Nonnull Supplier<NFFTamingProcess> processSupplier)
	{
		NFFTamingMapping.register(fromType, convertToType, processSupplier);
	}
	
}
