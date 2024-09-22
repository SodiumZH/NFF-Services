package net.sodiumzh.nff.services.event.client;

import java.util.function.Function;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumzh.nff.services.client.gui.screen.NFFGuiConstructorRegistry;
import net.sodiumzh.nff.services.client.gui.screen.NFFTamedGui;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;

public class NFFGuiScreenRegisterEvent extends Event implements IModBusEvent
{
	public NFFGuiScreenRegisterEvent() {}
	
	/**
	 * Register using simple mapping from menu to screen. 
	 * Also for GUI classes with constructor with one parameter {@code NFFTamedInventoryMenu}.
	 * @param menuClass
	 * @param prvd
	 */
	public void register(Class<? extends NFFTamedInventoryMenu> menuClass, Function<NFFTamedInventoryMenu, NFFTamedGui> guiFunction)
	{
		NFFGuiConstructorRegistry.register(menuClass, guiFunction);
	}
	
	/**
	 * Register using constructor with parameters {@code NFFTamedInventoryMenu, Inventory, INFFTamed}.
	 * This is a simplification for GUI classes with such constructor above and you'll be able to use {@code YourClass::new}.
	 * <p> Note: If registered in this style, it will invoke {@code new YourClass(menu, menu.playerInventory, menu.mob)} for construction.
	 * If you don't want this, use {@code register} instead and manually input the parameters.
	 * @param menuClass
	 * @param prvd
	 */
	public void registerDefault(Class<? extends NFFTamedInventoryMenu> menuClass, Provider prvd)
	{
		this.register(menuClass, (menu) -> prvd.create(menu, menu.playerInventory, menu.mob));
	}
	
	@FunctionalInterface
	public static interface Provider
	{
		NFFTamedGui create(NFFTamedInventoryMenu menu, Inventory inv, INFFTamed mob);
	}
	
}
