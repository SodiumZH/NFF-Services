package net.sodiumstudio.befriendmobs.bmevents.client;

import java.util.function.Function;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumstudio.befriendmobs.client.gui.screens.BefriendedGuiScreen;
import net.sodiumstudio.befriendmobs.client.gui.screens.BefriendedGuiScreenMaker;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;

public class RegisterGuiScreenEvent extends Event implements IModBusEvent
{
	public RegisterGuiScreenEvent() {}
	
	public void register(Class<? extends BefriendedInventoryMenu> menuClass, Function<BefriendedInventoryMenu, BefriendedGuiScreen> guiFunction)
	{
		BefriendedGuiScreenMaker.put(menuClass, guiFunction);
	}
}
