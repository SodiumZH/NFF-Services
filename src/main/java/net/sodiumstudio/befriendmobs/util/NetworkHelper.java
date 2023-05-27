package net.sodiumstudio.befriendmobs.util;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.network.ClientboundBefriendedGuiOpenPacket;

public class NetworkHelper
{
	
	public static SimpleChannel newChannel(String modId, String key, String version)
	{
		return NetworkRegistry.newSimpleChannel(
                new ResourceLocation(modId, key),
                () -> {
                    return version;
                },
                (v) -> {
                    return v.equals(version);
                },
                (v) -> {
                    return v.equals(version);
                });
	}
	
	public static SimpleChannel newChannel(String modId, String key)
	{
		return newChannel(modId, key, "1.0");
	}
	
	@SuppressWarnings("resource")
	public static <T extends Packet<ClientGamePacketListener>>
			void registerDefaultPacket(int id, SimpleChannel channel, Class<T> packetClass)
	{
		channel.registerMessage(
                id,
                packetClass,
                (pack, buffer) -> {
                    pack.write(buffer);
                },
                (buffer) -> {
					try
					{
						return packetClass.getConstructor(FriendlyByteBuf.class).newInstance(buffer);
					}
					catch (Exception e)
					{
						throw new IllegalArgumentException("NetworkHelper::registerDefaultPacket packet class missing constructor.");
					}
                },
                (pack, ctx) -> {
                	ctx.get().enqueueWork(() ->
                	{ 
                		pack.handle(Minecraft.getInstance().getConnection());
                	});
                	ctx.get().setPacketHandled(true);
                }
        );
	}
	
}
