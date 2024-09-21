package net.sodiumzh.nff.services.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.client.gui.screen.NFFGUIConstructorRegistry;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class NFFClientGamePacketHandler
{
	
	@SuppressWarnings("resource")
	public static void handleBefriendedGuiOpen(ClientboundNFFGUIOpenPacket packet, ClientGamePacketListener listener)
	{
		Minecraft mc = Minecraft.getInstance();
		PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
		Entity entity = mc.level.getEntity(packet.getEntityId());
		if (entity instanceof INFFTamed bef) {
			LocalPlayer localplayer = mc.player;
			NFFTamedMobInventory inv = new NFFTamedMobInventory(packet.getSize());
			NFFTamedInventoryMenu menu =
					bef.makeMenu(packet.getContainerId(), localplayer.getInventory(), inv);
			if (menu == null)
				return;
			localplayer.containerMenu = menu;
			mc.setScreen(NFFGUIConstructorRegistry.make(menu));
		}
	}
	
	public static void handleBefriendingInit(ClientboundTamedInitPacket packet, ClientGamePacketListener listener)
	{
		@SuppressWarnings("resource")
		Minecraft mc = Minecraft.getInstance();
		PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
		Entity entity = mc.level.getEntity(packet.entityId);
		entity.setXRot(packet.xRot);
		entity.setYRot(packet.yRot);
		if (entity instanceof Mob mob)
		{
			mob.setYBodyRot(packet.yBodyRot);
			mob.setYHeadRot(packet.yHeadRot);
		}
	}
	
	public static void handleBefriendedDataSync(CNFFTamedCommonData.ClientboundDataSyncPacket packet, ClientGamePacketListener listener)
	{
		@SuppressWarnings("resource")
		Minecraft mc = Minecraft.getInstance();
		PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
		Entity e = mc.level.getEntity(packet.entityId);
		e.getCapability(NFFCapRegistry.CAP_BEFRIENDED_MOB_DATA).ifPresent(c -> {
			for (var entry: packet.objects.entrySet())
				c.setSynchedDataClient(entry.getKey(), entry.getValue().getA(), entry.getValue().getB());
		});
	}
}
