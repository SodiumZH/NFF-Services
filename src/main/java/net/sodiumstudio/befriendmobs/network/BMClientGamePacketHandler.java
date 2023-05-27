package net.sodiumstudio.befriendmobs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.sodiumstudio.befriendmobs.client.gui.screens.BefriendedGuiScreenMaker;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;

public class BMClientGamePacketHandler
{
	
	@SuppressWarnings("resource")
	public static void handleBefriendedGuiOpen(ClientboundBefriendedGuiOpenPacket packet, ClientGamePacketListener listener)
	{
		Minecraft mc = Minecraft.getInstance();
		PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
		Entity entity = mc.level.getEntity(packet.getEntityId());
		if (entity instanceof IBefriendedMob bef) {
			LocalPlayer localplayer = mc.player;
			BefriendedInventory inv = new BefriendedInventory(packet.getSize());
			BefriendedInventoryMenu menu =
					bef.makeMenu(packet.getContainerId(), localplayer.getInventory(), inv);
			if (menu == null)
				return;
			localplayer.containerMenu = menu;
			mc.setScreen(BefriendedGuiScreenMaker.make(menu));
		}
	}
	
}
