package net.sodiumzh.nfu.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.network.NFUClientboundPacketHandlers;
import net.sodiumzh.nfu.network.NFUDataSerializers;

public class ClientboundEntityMotionUpdatePacket implements Packet<ClientGamePacketListener> {

    private final int id;
    private final Vec3 deltaPos;
    private final Vec3 deltaVelocity;

    public ClientboundEntityMotionUpdatePacket(int id, Vec3 deltaPos, Vec3 deltaVelocity) {
        this.id = id;
        this.deltaPos = deltaPos;
        this.deltaVelocity = deltaVelocity;
    }

    public ClientboundEntityMotionUpdatePacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.deltaPos = NFUDataSerializers.VEC3.read(buf);
        this.deltaVelocity = NFUDataSerializers.VEC3.read(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(id);
        NFUDataSerializers.VEC3.write(buf, deltaPos);
        NFUDataSerializers.VEC3.write(buf, deltaVelocity);
    }

    @Override
    public void handle(ClientGamePacketListener handler) {
        NFUClientboundPacketHandlers.handleEntityMotionUpdate(this, handler);
    }

    public int getId() {
        return id;
    }

    public Vec3 getDeltaPos() {
        return deltaPos;
    }

    public Vec3 getDeltaVelocity() {
        return deltaVelocity;
    }

}
