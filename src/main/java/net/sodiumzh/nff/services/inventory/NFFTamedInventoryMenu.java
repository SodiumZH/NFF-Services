package net.sodiumzh.nff.services.inventory;

import java.util.function.Predicate;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.math.GuiPos;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.subsystems.baublesystem.BaubleSystem;

public abstract class NFFTamedInventoryMenu extends AbstractContainerMenu {

	public final Container container;
	public final INFFTamed mob;
	public final Inventory playerInventory;
	// Overall offset that will be applied to all slots
	
	protected NFFTamedInventoryMenu(int containerId, Inventory playerInventory, Container container,
			INFFTamed mob) {
		super(null, containerId);
		this.mob = mob;
		this.container = container;
		if (!(this.container instanceof NFFTamedMobInventory))
			throw new UnsupportedOperationException("NFFTamedInventoryMenu only receives NFFTamedMobInventory.");
		((NFFTamedMobInventory)(this.container)).addListener(mob);
		this.playerInventory = playerInventory;
		addMenuSlots();
		if (doAddPlayerInventory())
			addPlayerInventorySlots(playerInventory, getPlayerInventoryPosition().x, getPlayerInventoryPosition().y);
		container.startOpen(playerInventory.player);
	}

	protected abstract void addMenuSlots();

	protected void addBaubleSlot(int slot, GuiPos pos, String key, Predicate<ItemStack> additionalCondition)
	{
		addSlot(new Slot(container, slot, pos.x, pos.y) {			
			@Override
			public boolean mayPlace(ItemStack stack) {
				return BaubleSystem.canEquipOn(stack, mob.asMob(), key) && additionalCondition.test(stack);
			}			
			@Override
			public int getMaxStackSize() {
	            return 1;
	        }			
		});
	}
	
	protected void addBaubleSlot(int slot, GuiPos pos, String key)
	{
		addBaubleSlot(slot, pos, key, stack -> true);
	}
	
	protected boolean doAddPlayerInventory()
	{
		return true;
	}
	
	protected abstract GuiPos getPlayerInventoryPosition();
	
	private void addPlayerInventorySlots(Inventory playerInventory, int startX, int startY) 
	{

		for (int i1 = 0; i1 < 3; ++i1) 
		{
			for (int k1 = 0; k1 < 9; ++k1)
			{
				this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, startX + k1 * 18,
						startY + i1 * 18));
			}
		}
		for (int j1 = 0; j1 < 9; ++j1) 
		{
			this.addSlot(new Slot(playerInventory, j1, startX + j1 * 18, startY + 58));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return mob.getOwnerInDimension() == player && ((LivingEntity) mob).distanceTo(player) < 16.0;
	}

	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.container.stopOpen(pPlayer);
	}

}
