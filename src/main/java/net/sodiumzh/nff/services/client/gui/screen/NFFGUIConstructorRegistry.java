package net.sodiumzh.nff.services.client.gui.screen;

import java.util.HashMap;
import java.util.function.Function;

import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;

public class NFFGUIConstructorRegistry
{
	private static final HashMap<Class<? extends NFFTamedInventoryMenu>, Function<NFFTamedInventoryMenu, NFFTamedGUI>> TABLE
		= new HashMap<Class<? extends NFFTamedInventoryMenu>, Function<NFFTamedInventoryMenu, NFFTamedGUI>>();
	
	/**
	 * Put a method for making GUI from inventory menu for a given menu class.
	 */
	public static void register(Class<? extends NFFTamedInventoryMenu> menuClass, Function<NFFTamedInventoryMenu, NFFTamedGUI> constructor)
	{
		TABLE.put(menuClass, constructor);
	}
	
	/**
	 * Make GUI from class.
	 * <p> WARNING: Ensure this function is called only on client!
	 */
	public static NFFTamedGUI createGUIFromMenu(NFFTamedInventoryMenu menu)
	{
		if (TABLE.containsKey(menu.getClass()))
		{
			return TABLE.get(menu.getClass()).apply(menu);
		}
		else throw new IllegalArgumentException("NFFGUIConstructorRegistry::make missing method. Use NFFGUIConstructorRegistry.put() to register GUI making method before calling.");
	}
	
}
