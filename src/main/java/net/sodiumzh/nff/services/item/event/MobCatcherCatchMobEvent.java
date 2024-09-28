package net.sodiumzh.nff.services.item.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

/**
 * Posted before a Mob Catcher starts to catch a mob.
 * <p> The {@code respawnerAfterCatching} is initialized from the mob by default. It's modifiable and the final generated respawner will apply the change.
 * <p> This event is cancelable. If canceled, the catching action will fail.
 */
@Cancelable
public class MobCatcherCatchMobEvent extends Event
{
	public final Mob mob;
	public final Player player;
	public NFFMobRespawnerInstance respawnerAfterCatching;
	
	public MobCatcherCatchMobEvent(Mob mob, Player player, NFFMobRespawnerInstance respawner)
	{
		this.mob = mob;
		this.player = player;
		this.respawnerAfterCatching = respawner;
	}
}
