package net.sodiumzh.nff.services.client.gui.screen;

import java.util.HashMap;
import java.util.function.Function;

import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;

public class NFFGuiConstructorRegistry
{
	private static final HashMap<Class<? extends NFFTamedInventoryMenu>, Function<NFFTamedInventoryMenu, NFFTamedGui>> TABLE
		= new HashMap<Class<? extends NFFTamedInventoryMenu>, Function<NFFTamedInventoryMenu, NFFTamedGui>>();
	
	/**
	 * Put a method for creating GUI from inventory menu for a given menu class.
	 */
	public static void register(Class<? extends NFFTamedInventoryMenu> menuClass, Function<NFFTamedInventoryMenu, NFFTamedGui> constructor)
	{
		TABLE.put(menuClass, constructor);
	}
	
	/**
	 * Make GUI from class.
	 * <p> WARNING: Ensure this function is called only on client!
	 */
	public static NFFTamedGui createGuiFromMenu(NFFTamedInventoryMenu menu)
	{
		if (TABLE.containsKey(menu.getClass()))
		{
			return TABLE.get(menu.getClass()).apply(menu);
		}
		else throw new IllegalArgumentException("NFFGuiConstructorRegistry::createGuiFromMenu missing method. Use NFFGuiConstructorRegistry.put() to register GUI construction method before calling.");
	}
	
}
