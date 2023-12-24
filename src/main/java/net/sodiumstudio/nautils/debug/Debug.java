package net.sodiumstudio.nautils.debug;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.nautils.InfoHelper;
import net.sodiumstudio.nautils.NaMiscUtils;
import net.sodiumstudio.nautils.exceptions.AssertionFailedException;

// All debug output should work only in development.
public class Debug {
	
	@Deprecated
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		printToScreen(text, receiver);
	}
	
	public static void printToScreen(String text, Player receiver)
	{		
		if(BefriendMobs.IS_DEBUG_MODE)			
			NaMiscUtils.printToScreen(text, receiver);
	}
	
}
