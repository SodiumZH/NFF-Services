package net.sodiumstudio.nautils.debug;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.nautils.InfoHelper;
import net.sodiumstudio.nautils.NaMiscUtils;
import net.sodiumstudio.nautils.exceptions.AssertionFailedException;

// All debug output should work only in development.
public class Debug {
	
	// param receiver should be player
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		if(BefriendMobs.IS_DEBUG_MODE)			
			receiver.sendMessage(InfoHelper.createText(text), sender.getUUID());
	}

	@Deprecated
	public static void printToScreen(String text, Player receiver)
	{
		printToScreen(text, receiver, receiver);
	}
	
}
