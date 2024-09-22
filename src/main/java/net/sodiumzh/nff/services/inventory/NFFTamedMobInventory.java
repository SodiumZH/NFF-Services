package net.sodiumzh.nff.services.inventory;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFTamedMobInventory extends SimpleContainer
{

	// For BefriendedMob only, owner ref
	@Nullable
	protected INFFTamed owner = null;
	
	protected void updateOwner()
	{
		if (owner != null && owner.hasInit())
			owner.updateFromInventory();
	}
	
	public INFFTamed getOwner()
	{
		return owner;
	}
	
	public void changeOwner(INFFTamed newOwner)
	{
		this.removeListener(owner);
		owner = newOwner;
		this.addListener(newOwner);
		updateOwner();
	}
	
	public NFFTamedMobInventory(int size)
	{
		super(size);
		if (size < 0)
			throw new NegativeArraySizeException();
	}
	
	public NFFTamedMobInventory(int size, INFFTamed owner)
	{
		super(size);
		this.owner = owner;
		if (owner != null)
			this.addListener(owner);
	}

	@Override
	public void setItem(int index, ItemStack stack)
	{
		if (index < 0 || index >= getContainerSize())
			throw new IndexOutOfBoundsException();
		super.setItem(index, stack);
	}

	@Override
	public ItemStack getItem(int index)
	{
		if (index < 0 || index >= getContainerSize())
			throw new IndexOutOfBoundsException();
		return super.getItem(index);
	}
	
	@Override
	public int getContainerSize()
	{
		return super.getContainerSize();
	}
	
	// Save this inventory into a tag.
	public CompoundTag toTag() 
	{
		CompoundTag tag = new CompoundTag();
		tag.put("size", IntTag.valueOf(this.getContainerSize()));
		for (int i = 0; i < this.getContainerSize(); ++i) 
		{
			NaUtilsNBTStatics.saveItemStack(this.getItem(i), tag, Integer.toString(i));
		}
		return tag;
	}

	
	public void saveToTag(CompoundTag parent, String key)
	{
		parent.put(key, this.toTag());
	}
	
	public void readFromTag(CompoundTag tag)
	{
		if (!tag.contains("size"))
			throw new IllegalArgumentException("NFFTamedMobInventory: reading from illegal tag.");
		if (tag.getInt("size") != this.getContainerSize())
		{
			NFFServices.LOGGER.warn("NFFTamedMobInventory reading from NBT: size not matching: this size: " 
					+ this.getContainerSize() + ", nbt size: " + tag.getInt("size"));
		}
		
		for (int i = 0; i < getContainerSize(); ++i)
		{
			if (tag.contains(Integer.toString(i)))
				this.setItem(i, NaUtilsNBTStatics.readItemStack(tag, Integer.toString(i)));
			else this.setItem(i, ItemStack.EMPTY);
		}
		updateOwner();
	}
	
	
	public static NFFTamedMobInventory makeFromTag(CompoundTag tag, INFFTamed owner)
	{
		NFFTamedMobInventory inv = new NFFTamedMobInventory(tag.getInt("size"), owner);
		inv.readFromTag(tag);
		inv.updateOwner();
		return inv;
	}
	
	// make from tag without owner
	public static NFFTamedMobInventory makeFromTag(CompoundTag tag) 
	{
		return makeFromTag(tag, null);
	}
	
	// Get a copy of this inventory
	public NFFTamedMobInventory getCopy()
	{
		NFFTamedMobInventory cpy;
		cpy = new NFFTamedMobInventory(this.getContainerSize(), owner);
		for (int i = 0; i < this.getContainerSize(); ++i)
		{
			cpy.setItem(i, this.getItem(i));
		}
		return cpy;
	}
	
	// Copy the input inventory into this
	public void copyFrom(NFFTamedMobInventory from)
	{
		if (from.getContainerSize() != this.getContainerSize())
			throw new IllegalStateException("NFFTamedMobInventory reading from other inventory: size not matching.");
		changeOwner(from.owner);
		for (int i = 0; i < getContainerSize(); ++i)
		{
			this.setItem(i, from.getItem(i));
		}
		updateOwner();
	}
	
	@Deprecated
	public NFFTamedMobInventory toContainer() {
		return this;
	}
	
	// Make a new InventoryTag from container.
	public static NFFTamedMobInventory makeFromContainer(SimpleContainer container)
	{
		NFFTamedMobInventory inv = new NFFTamedMobInventory(container.getContainerSize());
		for (int i = 0; i < inv.getContainerSize(); ++i)
		{
			inv.setItem(i, container.getItem(i));
		}
		return inv;	
	}
	
	// Set the tag content from a container.
	// Note: the tag size and container size must match.
	@Deprecated // Directly refer to this instead
	public void setFromContainer(SimpleContainer container)
	{
		if (getContainerSize() != container.getContainerSize())
			throw new IllegalArgumentException("Size not matching.");
		for (int i = 0; i < getContainerSize(); ++i)
		{
			setItem(i, container.getItem(i));
		}
	}
	
	public void swapItem(int position_1, int position_2)
	{
		ItemStack stack1 = this.getItem(position_1);
		this.setItem(position_1, this.getItem(position_2));
		this.setItem(position_2, stack1);
	}
	
	public boolean consumeItem(int position)
	{
		ItemStack stack = this.getItem(position);
		if (stack.isEmpty())
			return false;
		else
		{
			if (stack.getCount() == 1)
				this.setItem(position, ItemStack.EMPTY);
			else
			{
				stack.setCount(stack.getCount() - 1);
				this.setItem(position, stack);
			}
			return true;			
		}		
	}
	
	@Override
	public void clearContent()
	{
		for (int i = 0; i < this.getContainerSize(); ++i)
		{
			this.setItem(i, ItemStack.EMPTY);
		}
		this.setChanged();
	}
	
	/**
	 * Use information in this inventory to update the mob state (hand items, armor, etc.)
	 */
	public void syncToMob(Mob mob)
	{
	}
	
	/**
	 * Use mob state to update this inventory. Usually used only on initialization.
	 */
	public void getFromMob(Mob mob)
	{
	}
	
	/**
	 * Cast this to given subclass. 
	 * <p>WARNING: This method wraps an unchecked cast. Make sure the class matches.
	 */
	@SuppressWarnings("unchecked")
	public <T extends NFFTamedMobInventory> T cast(Class<T> clazz)
	{
		return (T) this;
	}
}
