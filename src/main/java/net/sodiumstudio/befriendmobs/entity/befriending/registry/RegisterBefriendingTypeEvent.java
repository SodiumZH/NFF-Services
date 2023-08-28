package net.sodiumstudio.befriendmobs.entity.befriending.registry;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumstudio.befriendmobs.entity.befriending.BefriendingHandler;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterBefriendingTypeEvent extends Event implements IModBusEvent
{

	public RegisterBefriendingTypeEvent register(@Nonnull EntityType<? extends Mob> from, @Nonnull EntityType<? extends Mob> convertTo, @Nonnull BefriendingHandler handler, boolean override)
	{
		BefriendingTypeRegistry.register(from, convertTo, handler, override);
		return this;
	}
	
	public RegisterBefriendingTypeEvent register(@Nonnull EntityType<? extends Mob> fromType, @Nonnull EntityType<? extends Mob> convertToType, @Nonnull BefriendingHandler handler)
	{
		BefriendingTypeRegistry.register(fromType, convertToType, handler);
		return this;
	}
	
	@SubscribeEvent
	public static void onRegisterBefriendingType(FMLCommonSetupEvent event)
	{
		ModLoader.get().postEvent(new RegisterBefriendingTypeEvent());
	}
}
