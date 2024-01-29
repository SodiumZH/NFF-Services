package net.sodiumstudio.nautils.debug;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.nautils.NaMiscUtils;
import net.sodiumstudio.nautils.NaUtils;

// All debug output should work only in development.
@Deprecated
public class Debug {
	
	// param receiver should be player
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		if (NaUtils.isDebugMode)
			NaMiscUtils.printToScreen(text, receiver);
	}

	@Deprecated
	public static void printToScreen(String text, Player receiver)
	{
		if (NaUtils.isDebugMode)
			NaMiscUtils.printToScreen(text, receiver, receiver);
	}
	
}
