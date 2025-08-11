package net.sodiumzh.nfu.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sodiumzh.nfu.network.packet.ClientboundEntityMotionUpdatePacket;

@OnlyIn(Dist.CLIENT)
public class NFUClientboundPacketHandlers {

    public static void handleEntityMotionUpdate(ClientboundEntityMotionUpdatePacket packet, ClientGamePacketListener listener) {
        Minecraft mc = Minecraft.getInstance();
        PacketUtils.ensureRunningOnSameThread(packet, listener, mc);
        if (mc.level == null) return;
        Entity e = mc.level.getEntity(packet.getId());
        if (e == null) return;
        e.setPos(e.position().add(packet.getDeltaPos()));
        e.setDeltaMovement(e.getDeltaMovement().add(packet.getDeltaVelocity()));
    }

}
