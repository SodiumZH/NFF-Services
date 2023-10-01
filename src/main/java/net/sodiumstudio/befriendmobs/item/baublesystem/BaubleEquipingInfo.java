package net.sodiumstudio.befriendmobs.item.baublesystem;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public record BaubleEquipingInfo(@Nonnull Mob mob, @Nonnull String slotKey, @Nonnull ItemStack baubleItemStack)
{
	
}
