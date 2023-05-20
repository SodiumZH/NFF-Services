package net.sodiumstudio.befriendmobs.util.debug;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.befriendmobs.BefriendMobs;

import net.sodiumstudio.befriendmobs.util.InfoHelper;
import net.sodiumstudio.befriendmobs.util.MiscUtil;
import net.sodiumstudio.befriendmobs.util.exceptions.AssertionFailedException;

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
			MiscUtil.printToScreen(text, receiver);
	}
	
}
