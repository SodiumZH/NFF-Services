package net.sodiumzh.nff.services.event.entity;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

/**
 * This event is fired on a befriended mob dies if it's configured to spawn respawner.
 * <p> If canceled, it will not drop respawner in normal style.
 */
@Cancelable
public class NFFTamedDropRespawnerOnDyingEvent extends Event
{

	public final INFFTamed dyingMob;
	public final NFFMobRespawnerInstance respawner;
	
	public NFFTamedDropRespawnerOnDyingEvent(INFFTamed mob, NFFMobRespawnerInstance respawner)
	{
		this.dyingMob = mob;
		this.respawner = respawner;
	}
	
}
