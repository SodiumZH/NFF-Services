package net.sodiumzh.nff.services.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.client.gui.screen.NFFGuiConstructorRegistry;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;

public class NFFClientGamePacketHandler
{
	
	@SuppressWarnings("resource")
	public static void handleBefriendedGuiOpen(ClientboundNFFGUIOpenPacket packet, ClientGamePacketListener listener)
	{
		Minecraft mc = Minecraft.getInstance();
		PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
		Entity entity = mc.level.getEntity(packet.getEntityId());
		INFFTamed.get(entity).ifPresent(bef -> {
			LocalPlayer localplayer = mc.player;
			NFFTamedMobInventory inv = new NFFTamedMobInventory(packet.getSize());
			NFFTamedInventoryMenu menu =
					bef.makeMenu(packet.getContainerId(), localplayer.getInventory(), inv);
			if (menu == null)
				return;
			localplayer.containerMenu = menu;
			mc.setScreen(NFFGuiConstructorRegistry.createGuiFromMenu(menu));
		});
	}
}
