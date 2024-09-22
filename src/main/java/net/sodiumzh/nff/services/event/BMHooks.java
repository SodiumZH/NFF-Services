package net.sodiumzh.nff.services.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.event.entity.NFFMobTamedEvent;
import net.sodiumzh.nff.services.event.entity.NFFTamedDropRespawnerOnDyingEvent;
import net.sodiumzh.nff.services.event.level.BMLevelModuleTickEndEvent;
import net.sodiumzh.nff.services.event.level.BMLevelModuleTickStartEvent;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

public class BMHooks
{

	/**
	 * Hooks related to befriending processes
	 */
	public static class Befriending
	{
		public static void onMobBefriended(Mob mobBefore, INFFTamed befriended)
		{
			MinecraftForge.EVENT_BUS.post(new NFFMobTamedEvent(mobBefore, befriended));
		}
	}
	
	public static class Befriended
	{
		public static boolean onBefriendedGenerateRespawnerOnDying(INFFTamed mob, NFFMobRespawnerInstance respawner)
		{
			return MinecraftForge.EVENT_BUS.post(new NFFTamedDropRespawnerOnDyingEvent(mob, respawner));
		}
	}
	
	
	// Hooks during actions of the level
	public static class Level
	{
		public static void onModuleTickStart(ServerLevel level)
		{
			MinecraftForge.EVENT_BUS.post(new BMLevelModuleTickStartEvent(level));
		}
		
		public static void onModuleTickEnd(ServerLevel level)
		{
			MinecraftForge.EVENT_BUS.post(new BMLevelModuleTickEndEvent(level));
		}
	}
	
}
