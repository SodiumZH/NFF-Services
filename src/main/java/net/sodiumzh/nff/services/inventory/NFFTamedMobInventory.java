package net.sodiumzh.nff.services.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.util.NFUNBTStatics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NFFTamedMobInventory extends SimpleContainer
{

	// For BefriendedMob only, owner ref
	@Nullable
	protected INFFTamed owner = null;

	public INFFTamed getOwner()
	{
		return owner;
	}
	
	public void changeOwner(INFFTamed newOwner)
	{
		if (owner != null)
			this.removeListener(owner);
		owner = newOwner;
		this.addListener(newOwner);
		syncToOwner();
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

	public ListTag toTag() {
		ListTag tag = new ListTag();
		this.toList().forEach(item -> tag.add(item.serializeNBT()));
		return tag;
	}

	// Save this inventory into a tag.
	@Deprecated
	public CompoundTag toTagLegacy()
	{
		CompoundTag tag = new CompoundTag();
		tag.put("size", IntTag.valueOf(this.getContainerSize()));
		for (int i = 0; i < this.getContainerSize(); ++i) 
		{
			NFUNBTStatics.saveItemStack(this.getItem(i), tag, Integer.toString(i));
		}
		return tag;
	}

	public void saveToTag(CompoundTag parent, String key)
	{
		parent.put(key, this.toTag());
	}
	
	public void readFromTag(Tag tag)
	{
		if (tag instanceof ListTag listTag) {
			if (listTag.size() != this.getContainerSize())
				NFFServices.LOGGER.warn("NFFTamedMobInventory reading from NBT: size not matching: this size: "
					+ this.getContainerSize() + ", nbt size: " + listTag.size());
			if (listTag.getElementType() != Tag.TAG_COMPOUND) throw new IllegalStateException("Wrong nbt item format");
			List<ItemStack> items = listTag.stream().map(elem -> ItemStack.of((CompoundTag) elem)).toList();
			if (items.size() != this.getContainerSize()) {
				NFFServices.LOGGER.warn("NFFTamedMobInventory reading from NBT: size not matching: this size: "
					+ this.getContainerSize() + ", nbt size: " + items.size());
			}
			for (int i = 0; i < items.size(); ++i){
				if (i < this.getContainerSize())
					this.setItem(i, items.get(i));
			}
		}
		// TODO Remove. Handle 0.x.32- legacy format
		else if (tag instanceof CompoundTag tagCmpd) {
			if (!tagCmpd.contains("size"))
				throw new IllegalArgumentException("NFFTamedMobInventory: reading from illegal tag.");
			if (tagCmpd.getInt("size") != this.getContainerSize()) {
				NFFServices.LOGGER.warn("NFFTamedMobInventory reading from NBT: size not matching: this size: "
					+ this.getContainerSize() + ", nbt size: " + tagCmpd.getInt("size"));
			}

			for (int i = 0; i < getContainerSize(); ++i) {
				if (tagCmpd.contains(Integer.toString(i)))
					this.setItem(i, NFUNBTStatics.readItemStack(tagCmpd, Integer.toString(i)));
				else this.setItem(i, ItemStack.EMPTY);
			}
		}
		if (this.owner != null) this.syncToMob(this.owner.asMob());
	}
	
	public void writeBuf(FriendlyByteBuf buf) {
		buf.writeCollection(this.toList(), (buf1, itemstack) -> buf1.writeItemStack(itemstack, false));
	}

	public void readBuf(FriendlyByteBuf buf) {
		List<ItemStack> list = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
		if (this.getContainerSize() != list.size())
			NFFServices.LOGGER.warn("NFFTamedMobInventory reading from NBT: size not matching: this size: "
				+ this.getContainerSize() + ", buf size: " + list.size());
		for (int i = 0; i < list.size(); ++i) {
			if (i < this.getContainerSize())
				this.setItem(i, list.get(i));
		}
	}

	public static NFFTamedMobInventory fromBuf(FriendlyByteBuf buf) {
		List<ItemStack> list = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
		NFFTamedMobInventory inventory = new NFFTamedMobInventory(list.size());
		for (int i = 0; i < list.size(); ++i) {
			inventory.setItem(i, list.get(i));
		}
		return inventory;
	}

	public static NFFTamedMobInventory makeFromTag(Tag tag, INFFTamed owner)
	{
		NFFTamedMobInventory inv = null;
		if (tag instanceof CompoundTag cmpd)
			inv = new NFFTamedMobInventory(cmpd.getInt("size"), owner);
		else if (tag instanceof ListTag list)
			inv = new NFFTamedMobInventory(list.size(), owner);
		inv.readFromTag(tag);
		inv.syncToOwner();
		return inv;
	}
	
	// make from tag without owner
	public static NFFTamedMobInventory makeFromTag(Tag tag)
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
		syncToOwner();
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

    public final void syncToOwner() {
        if (owner != null) this.syncToMob(owner.asMob());
    }

    /**
	 * Use mob state to update this inventory. Usually used only on initialization.
	 */
	public void getFromMob(Mob mob)
	{
	}

    public final void getFromOwner() {
        if (this.owner != null) this.getFromMob(this.owner.asMob());
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

	/**
	 * Convert inventory to item stack list..
	 */
	public List<ItemStack> toList() {
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < this.getContainerSize(); ++i) {
			list.add(this.getItem(i));
		}
		return list;
	}

	/**
	 * Set items from item stack list. If list is shorter, the additional item stacks will keep unchanged.
	 * If the list is longer, the longer part will be ignored.
	 */
	public void fromList(List<ItemStack> list) {
		for (int i = 0; i < list.size(); ++i) {
			if (i < this.getContainerSize()) this.setItem(i, list.get(i));
		}
	}

	public static NFFTamedMobInventory createEmpty(@Nullable INFFTamed owner) {
		return new NFFTamedMobInventory(0, owner);
	}

}
