package net.sodiumzh.nautils.containers;

import java.util.UUID;

import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PlayerIntMap extends SerializableMap<Player, Integer>
{

	private static final long serialVersionUID = 3110870180301725700L;

	public PlayerIntMap(Entity levelContext)
	{
		super((Player player) -> player.getStringUUID(), 
				i -> IntTag.valueOf(i), 
				str -> levelContext.level.getPlayerByUUID(UUID.fromString(str)),
				tag -> ((IntTag)tag).getAsInt());
	}
}
