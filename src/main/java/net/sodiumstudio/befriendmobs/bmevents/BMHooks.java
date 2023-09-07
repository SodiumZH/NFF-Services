package net.sodiumstudio.befriendmobs.bmevents;

import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.bmevents.entity.BefriendedGenerateRespawnerOnDyingEvent;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;
import net.sodiumstudio.befriendmobs.item.MobRespawnerInstance;

public class BMHooks
{

	public static class Befriended
	{
		public static boolean onBefriendedGenerateRespawnerOnDying(IBefriendedMob mob, MobRespawnerInstance respawner)
		{
			return MinecraftForge.EVENT_BUS.post(new BefriendedGenerateRespawnerOnDyingEvent(mob, respawner));
		}
	}
	
	
	// Hooks during actions of the leve
	public static class Level
	{
		public static boolean 
	}
	
}
