package net.sodiumzh.nautils.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.sodiumzh.nautils.math.GuiPos;

public abstract class PlayerInvolvedContainerMenu extends AbstractContainerMenu
{
	protected final Player player;
	protected final Inventory playerInventory;
	protected final Container extraContainer;
	
	protected PlayerInvolvedContainerMenu(MenuType<?> pMenuType, int pContainerId, Player player,
			Inventory playerInventory, Container extraContainer, GuiPos playerInventoryPos)
	{
		super(pMenuType, pContainerId);
		this.player = player;
		this.playerInventory = playerInventory;
		this.extraContainer = extraContainer;
		this.addPlayerInventorySlots(playerInventoryPos);
	}
	
	private void addPlayerInventorySlots(GuiPos startPos) 
	{
		for (int i = 0; i < 9; ++i) 
		{
			this.addSlot(new Slot(this.playerInventory, i, startPos.x + i * 18, startPos.y + 58));
		}
		for (int i = 0; i < 3; ++i) 
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, startPos.x + j * 18,
						startPos.y + i * 18));
			}
		}
	}
	
	protected boolean isInPlayerInventory(int slotIndex)
	{
		if (slotIndex < 0)
			throw new IllegalArgumentException("Negative slot index.");
		return slotIndex < 36;
	}
	
	/**
	 * Add slots other than the preset player inventory. Needs to be manually called in the constructor.
	 */
	protected abstract void addExtraSlots();
}
