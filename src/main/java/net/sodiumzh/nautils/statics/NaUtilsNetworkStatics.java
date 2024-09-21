package net.sodiumzh.nautils.statics;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NaUtilsNetworkStatics
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
			void registerDefaultClientGamePacket(int id, SimpleChannel channel, Class<T> packetClass)
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
						throw new IllegalArgumentException("NaUtilsNetworkStatics::registerDefaultPacket packet class missing constructor.", e);
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
	
	/**
	 * Send a packet from server to a given player.
	 */
	public static void sendToPlayer(SimpleChannel channel, Packet<?> msg, Player target)
	{
		if (target.level().isClientSide)
			return;
		if (target instanceof ServerPlayer sp)
		{
			channel.send(PacketDistributor.PLAYER.with(() -> sp), msg);
		}
	}
	
	/**
	 * Send packet from server to all players in a level.
	 */
	public static void sendToAllPlayers(Level level, SimpleChannel channel, Packet<?> message)
	{
		if (level.isClientSide)
			return;
		for (Player player: level.players())
		{
			if (player instanceof ServerPlayer sp)
			{
				channel.send(PacketDistributor.PLAYER.with(() -> sp), message);
			}
		}
	}
}
