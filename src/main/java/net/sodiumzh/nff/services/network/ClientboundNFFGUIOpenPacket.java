package net.sodiumzh.nff.services.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundNFFGUIOpenPacket implements Packet<ClientGamePacketListener> {

	protected final int containerId;
	protected final int size;
	protected final int entityId;

	public ClientboundNFFGUIOpenPacket(int containerId, int size, int entityId) {
		this.containerId = containerId;
		this.size = size;
		this.entityId = entityId;
	}

	public ClientboundNFFGUIOpenPacket(FriendlyByteBuf pBuffer) {
		this.containerId = pBuffer.readUnsignedByte();
		this.size = pBuffer.readVarInt();
		this.entityId = pBuffer.readInt();
	}


	@Override
	public void write(FriendlyByteBuf pBuffer) {
		pBuffer.writeByte(this.containerId);
		pBuffer.writeVarInt(this.size);
		pBuffer.writeInt(this.entityId);
	}

	@Override
	public void handle(ClientGamePacketListener handler) {
		NFFClientGamePacketHandler.handleBefriendedGuiOpen(this, handler);
	}

	public int getContainerId() {
		return this.containerId;
	}

	public int getSize() {
		return this.size;
	}

	public int getEntityId() {
		return this.entityId;
	}
}
