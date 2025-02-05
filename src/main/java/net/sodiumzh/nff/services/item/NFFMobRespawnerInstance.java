package net.sodiumzh.nff.services.item;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;
import net.sodiumzh.nff.services.entity.NFFMobRespawnInfo;
import net.sodiumzh.nff.services.item.event.NFFMobRespawnerAfterConstructEvent;
import net.sodiumzh.nff.services.item.event.NFFMobRespawnerBeforeConstructEvent;

/**
 * Wrapper of item stack of mob respawner
 */
public class NFFMobRespawnerInstance extends NFFMobRespawnInfo
{
	protected ItemStack stack;
	protected static final String ITEM_STACK_NBT_PATH = "mob_respawner";
	protected boolean noExpire = true;
	protected boolean invulnerable = true;
	protected boolean recoverInVoid = true;

	protected NFFMobRespawnerInstance(@Nonnull ItemStack stack)
	{
		if (stack.isEmpty())
			throw new IllegalArgumentException("NFFMobRespawnerInstance: Input ItemStack is empty.");
		this.stack = stack;
		// Initialize fields first
		if (this.getNBTIfPresent() != null)
			this.deserializeNBT(this.getNBTIfPresent());
		// Then fix ItemStack NBT
		this.writeNBT(this.getOrCreateNBT());
	}
	
	public void set(ItemStack stack, boolean clearOldNBT)
	{
		if (stack.isEmpty())
			throw new IllegalArgumentException("NFFMobRespawnerInstance: Input ItemStack is empty.");
		if (clearOldNBT && this.stack.hasTag())
			this.stack.getTag().remove(ITEM_STACK_NBT_PATH);
		this.stack = stack;
		// Initialize fields first
		this.deserializeNBT(getOrCreateNBT());
		// Then fix ItemStack NBT
		this.writeNBT(getOrCreateNBT());
	}

	public ItemStack get()
	{
		return stack;
	}

	/**
	 * Create {@code NFFMobRespawnerInstance} if the input ItemStack is already in a valid respawner format
	 * (not necessarily being able to respawn a mob). Returns {@code null} if not.
	 */
	@Nullable
	public static NFFMobRespawnerInstance createIfValid(ItemStack stack)
	{
		return isValidRespawner(stack) ? new NFFMobRespawnerInstance(stack) : null;
	}

	/**
	 * Create {@code NFFMobRespawnerInstance} and initialize the NBT of ItemStack to be a valid spawner format
	 * (not necessarily being able to respawn a mob).
	 */
	@Nonnull
	public static NFFMobRespawnerInstance createAndInitItem(ItemStack stack) {
		return new NFFMobRespawnerInstance(stack);
	}

	/**
	 * Get the full NBT stored in the item stack. Create NBT if the path is absent.
	 * The NBT will be stored into item stack when calling {@code writeNBT()}.
	 */
	@Nonnull
	public CompoundTag getOrCreateNBT() {
		return this.get().getOrCreateTag().getCompound(ITEM_STACK_NBT_PATH);
	}

	@Nullable
	public CompoundTag getNBTIfPresent() {
		if (!this.get().hasTag()) return null;
		if (!this.get().getTag().contains(ITEM_STACK_NBT_PATH, Tag.TAG_COMPOUND)) return null;
		return this.get().getTag().getCompound(ITEM_STACK_NBT_PATH);
	}

	public boolean isNoExpire() {
		return noExpire;
	}

	public void setNoExpire(boolean val) {

		this.noExpire = val;
		this.writeNBT(getOrCreateNBT());
	}

	public boolean recoverInVoid() {
		return this.recoverInVoid;
	}

	public void setRecoverInVoid(boolean val) {
		this.recoverInVoid = val;
		this.writeNBT(getOrCreateNBT());
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean val) {
		this.invulnerable = val;
		this.writeNBT(getOrCreateNBT());
	}

	public CompoundTag getMobNbt() {
		return getOrCreateNBT().getCompound(MOB_NBT_KEY);
	}

	@SuppressWarnings("unchecked")
	public EntityType<? extends Mob> getType() {
		return (EntityType<? extends Mob>) ForgeRegistries.ENTITIES
				.getValue(new ResourceLocation(getOrCreateNBT().getString(MOB_TYPE_KEY)));
	}

	public CompoundTag makeMobData(Player player, BlockPos pos, Direction direction) {
		CompoundTag nbt = getOrCreateNBT().getCompound(MOB_NBT_KEY);
		// Update position first, otherwise the generated mob will perform teleporting
		// away and back
		Vec3 posV = new Vec3((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
		NaUtilsNBTStatics.putVec3(nbt, "Pos", posV);
		return nbt;
	}

	@Override
	protected void beforeSave(Mob fromMob) {
		MinecraftForge.EVENT_BUS.post(new NFFMobRespawnerBeforeConstructEvent(fromMob, this));
	}

	@Override
	protected void afterSave(Mob fromMob, CompoundTag original) {
		MinecraftForge.EVENT_BUS.post(new NFFMobRespawnerAfterConstructEvent(fromMob, this, original));
	}

	public UUID getOwnerUUID()
	{
		return CNFFTamedCommonData.getOwnerUUIDFromMobTag(getMobNbt());
	}
	
	public String getModId()
	{
		return CNFFTamedCommonData.getModIdFromMobTag(getMobNbt());
	}
	
	public Component getName()
	{
		return NaUtilsEntityStatics.getNameFromNbt(getMobNbt(), getType());
	}
	
	public UUID getUUID()
	{
		return getMobNbt().getUUID("UUID");
	}

	@Override
	public void writeNBT(CompoundTag writeInto) {
		super.writeNBT(writeInto);
		writeInto.putBoolean("no_expire", noExpire);
		writeInto.putBoolean("invulnerable", invulnerable);
		writeInto.putBoolean("recover_in_void", recoverInVoid);
		stack.getOrCreateTag().put(ITEM_STACK_NBT_PATH, writeInto);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNBT(nbt);
		this.noExpire = nbt.getBoolean("no_expire");
		this.invulnerable = nbt.getBoolean("invulnerable");
		this.recoverInVoid = nbt.getBoolean("recover_in_void");
	}

	@Override
	public void saveFromMob(Mob mob) {
		super.saveFromMob(mob);
		this.writeNBT(getOrCreateNBT());
	}


	/**
	 * Check if an ItemStack is valid to respawn the mob.
	 */
	public static boolean isValidRespawner(ItemStack itemStack) {
		if (itemStack.isEmpty()) return false;
		if (!itemStack.hasTag()) return false;
		CompoundTag nbt = itemStack.getTag().getCompound(ITEM_STACK_NBT_PATH);
		return !nbt.isEmpty()
				&& nbt.contains(MOB_TYPE_KEY, Tag.TAG_STRING)
				&& nbt.contains(MOB_NBT_KEY, Tag.TAG_COMPOUND)
				&& nbt.contains("no_expire", Tag.TAG_BYTE)
				&& nbt.contains("invulnerable", Tag.TAG_BYTE)
				&& nbt.contains("recover_in_void", Tag.TAG_BYTE);
	}

	public boolean isNFFRespawnerItem() {return this.get().getItem() instanceof NFFMobRespawnerItem;}
}
