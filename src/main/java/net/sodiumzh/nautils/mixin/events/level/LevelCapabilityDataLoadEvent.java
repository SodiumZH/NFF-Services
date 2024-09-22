package net.sodiumzh.nautils.mixin.events.level;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted before loading Level capabilities from data, allowing to modify the nbt before loading.
 */
public class LevelCapabilityDataLoadEvent extends Event
{
	private final CompoundTag nbt;
	
	public LevelCapabilityDataLoadEvent(CompoundTag nbt)
	{
		this.nbt = nbt;
	}

	/**
	 * Get the whole nbt. You can modify the result and the data will also be modified.
	 */
	public CompoundTag getNbt() {
		return nbt;
	}

	/**
	 * Get capability nbt from key. Note that modifying the return tag will also modify the data.
	 */
	@Nonnull
	public CompoundTag getCapNbt(ResourceLocation key)
	{
		return nbt.getCompound(key.toString());
	}
}
