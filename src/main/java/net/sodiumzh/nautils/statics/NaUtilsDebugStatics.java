package net.sodiumzh.nautils.statics;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;

public class NaUtilsDebugStatics
{

	public static void debugPrintToScreen(Component text, Player receiver) 
	{
		if (NaUtilsConfigs.CACHED_DEBUG_MODE)
		{
			NaUtilsMiscStatics.printToScreen(text, receiver);
		}
	}
	
	public static void debugPrintToScreen(String text, Player receiver) 
	{
		if (NaUtilsConfigs.CACHED_DEBUG_MODE)
		{
			NaUtilsMiscStatics.printToScreen(text, receiver);
		}
	}
	
}
