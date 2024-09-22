package net.sodiumzh.nff.services.event.setup;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;

public class NFFTamingMappingRegisterEvent extends Event implements IModBusEvent {
	
	public void register(@Nonnull EntityType<? extends Mob> from, @Nonnull EntityType<? extends Mob> convertTo, @Nonnull NFFTamingProcess handler, boolean override)
	{
		NFFTamingMapping.register(from, convertTo, handler, override);
	}
	
	public void register(@Nonnull EntityType<? extends Mob> fromType, @Nonnull EntityType<? extends Mob> convertToType, @Nonnull NFFTamingProcess handler)
	{
		NFFTamingMapping.register(fromType, convertToType, handler);
	}
	
}
