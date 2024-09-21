package net.sodiumzh.nff.services.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Mob;

/**
 * Packet sending to client right after befriending a mob. 
 * Not implemented yet because client cannot find the mob (null when getting entity from id)
 */
public class ClientboundTamedInitPacket implements Packet<ClientGamePacketListener>
{
	public final int entityId;
	public final float xRot;
	public final float yRot;
	public final float yBodyRot;
	public final float yHeadRot;
	
	/** On server construction only
	 * @param target The mob BEFORE befriending.
	 */
	@SuppressWarnings("resource")
	public ClientboundTamedInitPacket(Mob target)
	{
		if (!target.level().isClientSide)
		{
			entityId = target.getId();
			xRot = target.getXRot();
			yRot = target.getYRot();
			yBodyRot = target.yBodyRot;
			yHeadRot = target.yHeadRot;
		}
		else
		{
			throw new IllegalStateException("ClientboundTamedInitPacket can construct from mob only on server.");
		}
	}

	public ClientboundTamedInitPacket(FriendlyByteBuf buffer)
	{
		entityId = buffer.readInt();
		xRot = buffer.readFloat();
		yRot = buffer.readFloat();
		yBodyRot = buffer.readFloat();
		yHeadRot = buffer.readFloat();
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeFloat(xRot);
		buffer.writeFloat(yRot);
		buffer.writeFloat(yBodyRot);
		buffer.writeFloat(yHeadRot);
	}

	@Override
	public void handle(ClientGamePacketListener pHandler) {
		NFFClientGamePacketHandler.handleBefriendingInit(this, pHandler);		
	}
	
}
