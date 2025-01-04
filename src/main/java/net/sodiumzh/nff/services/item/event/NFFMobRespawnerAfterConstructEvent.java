package net.sodiumzh.nff.services.item.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

public class NFFMobRespawnerAfterConstructEvent extends Event
{
	private Mob mob;
	private NFFMobRespawnerInstance respawner;
	private CompoundTag nbt;
	//protected ItemStack stack;

	public Mob getMob() {return mob;};
	public NFFMobRespawnerInstance getRespawner() {return respawner;};
	//public ItemStack getStack() {return stack;}
	public CompoundTag getNBT() {return nbt;}

	public NFFMobRespawnerAfterConstructEvent(Mob mob, NFFMobRespawnerInstance respawner, CompoundTag nbt)
	{
		this.mob = mob;
		this.respawner = respawner;
		this.nbt = nbt;
	}

}
