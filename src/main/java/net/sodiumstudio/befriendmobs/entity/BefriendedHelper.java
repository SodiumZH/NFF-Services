package net.sodiumstudio.befriendmobs.entity;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.network.BMChannels;
import net.sodiumstudio.befriendmobs.network.ClientboundBefriendedGuiOpenPacket;
import net.sodiumstudio.nautils.EntityHelper;
import net.sodiumstudio.nautils.NbtHelper;

/**
 * A function library for befriended mobs
 */

public class BefriendedHelper
{

	/* AI */

	/**
	 *  Default settings of the rule of what the mob can attack.
	 *  <p>判断生物是否可以攻击对象的规则的默认预设。
	 */
	public static boolean wantsToAttackDefault(IBefriendedMob mob, LivingEntity target) {
		// Don't attack creeper or ghast
		if ((target instanceof Creeper) || (target instanceof Ghast))
			return false;
		// For tamable mobs: attack untamed or (others' mobs if pvp allowed)
		else if (target instanceof TamableAnimal tamable)
			return !tamable.isTame() || (mob.isOwnerPresent() && tamable.getOwner() != mob.getOwner()
					&& (mob.getOwner()).canHarmPlayer((Player) (tamable.getOwner())));
		// For players: attack if pvp allowed
		else if (target instanceof Player targetPlayer)
			return mob.isOwnerPresent() && mob.getOwner().canHarmPlayer(targetPlayer);
		// For IBefriendedMob: similar to tamable mobs
		else if (target instanceof IBefriendedMob bef)
			return mob.isOwnerPresent() && bef.getOwner() != mob.getOwner() && (mob.getOwner()).canHarmPlayer(bef.getOwner());
		// For horses: attack untamed only
		else if (target instanceof AbstractHorse && ((AbstractHorse) target).isTamed())
			return false;
		// Can attack other
		else
			return true;
	}

	/* Save & Load */

	@Deprecated
	public static void addBefriendedCommonSaveData(IBefriendedMob mob, CompoundTag nbt, String modId) {		
		addBefriendedCommonSaveData(mob, nbt);
	}

	/**
	 *  This will read owner, AI state and additional inventory
	 *  <p>读取拥有者信息、AI状态及附加道具栏
	*/
	public static void addBefriendedCommonSaveData(IBefriendedMob mob, CompoundTag nbt)
	{
		String modId = mob.getModId();
		String ownerKey = modId + ":befriended_owner";
		String aiStateKey = modId + ":befriended_ai_state";
		String inventoryKey = modId + ":befriended_additional_inventory";
		// Mod ID
		nbt.putString("befriended_mod_id", modId);
		// Owner UUID
		if (mob.getOwnerUUID() != null)
			nbt.putUUID(ownerKey, mob.getOwnerUUID());
		else
			nbt.putUUID(ownerKey, new UUID(0, 0));
		nbt.putInt(aiStateKey, mob.getAIState().id);
		mob.getAdditionalInventory().saveToTag(nbt, inventoryKey);
	}
	
	@Deprecated	// Use version without modid input
	public static void readBefriendedCommonSaveData(IBefriendedMob mob, CompoundTag nbt, String inModId)
	{
		readBefriendedCommonSaveData(mob, nbt);
	}
	
	/**
	 * Read mob's Mod Id, owner, AI state and additional inventory information.
	 * <p>读取生物所属的Mod ID、拥有者信息、AI状态及附加道具栏
	 */
	
	public static void readBefriendedCommonSaveData(IBefriendedMob mob, CompoundTag nbt) {
		String modid = null;
		// 
		if (nbt.contains("befriended_mod_id", NbtHelper.TAG_STRING_ID))
		{
			modid = nbt.getString("befriended_mod_id");
		}		
		
		else modid = "dwmg";	// Porting from 1.18.2-s6 & 1.18.2-s7. Later it will be "befriendmobs".
		
		String ownerKey = modid + ":befriended_owner";
		String aiStateKey = modid + ":befriended_ai_state";
		String inventoryKey = modid + ":befriended_additional_inventory";
		UUID uuid = nbt.contains(ownerKey) ? nbt.getUUID(ownerKey) : null;	
		try {
		if (uuid == null)
			throw new IllegalStateException(
					"Reading befriended mob data error: invalid owner. Was IBefriendedMob.init() not called?");
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
		mob.setOwnerUUID(uuid);
		mob.init(mob.getOwnerUUID(), null);
		mob.setAIState(BefriendedAIState.fromID(nbt.getInt(aiStateKey)), false);
		mob.getAdditionalInventory().readFromTag(nbt.getCompound(inventoryKey));
	}

	/**
	 * Convert a befriended mob to other type. This action will keep its data.
	 * @param target The mob to convert.
	 * @param newType The type converting to, must implementing {@code IBefriendedMob} interface.
	 * @return The new mob reference.
	 * <p>========
	 * <p>将一个友好化生物转化为其他类型。这个操作会保持其数据。
	 * @param target 转化前的生物。
	 * @param newType 转化为的类型。其必须实现{@code IBefriendedMob}接口。
	 * @return The new mob reference. 新生物的引用。
	 */
	public static IBefriendedMob convertToOtherBefriendedType(IBefriendedMob target, EntityType<? extends Mob> newType)
	{
		// Additional inventory will be invalidated upon convertion, so backup as a tag
		CompoundTag mobTag = new CompoundTag();
		target.asMob().saveWithoutId(mobTag);
		// Do convertion
		
		Mob newMob = EntityHelper.replaceMob(newType, target.asMob());
		if (!(newMob instanceof IBefriendedMob))
			throw new UnsupportedOperationException("BefriendedHelper::convertToOtherBefriendedType supports mobs implementing IBefriendedMob.");
		newMob.load(mobTag);
		// Write the inventory back
		/*if(inventoryTag.getInt("size") != newMob.getAdditionalInventory().getContainerSize())
			throw new UnsupportedOperationException("BefriendedHelper::convertToOtherBefriendedType additional inventory must have same size before and after conversion.");
		newMob.getAdditionalInventory().readFromTag(inventoryTag);
		// Do other settings
		newMob.setAIState(target.getAIState(), false);
		newMob.init(target.getOwnerUUID(), target.asMob());
		newMob.updateFromInventory();*/
		// setInit() needs to call manually
		
		return (IBefriendedMob)newMob;
	}
	
	/* Inventory */

	/**
	 * Open the inventory GUI of the mob.
	 * <p>Warning: DO NOT call this if {@link IBefriendedMob#makeMenu()} method returns null, otherwise it will crash the game.
	 * <p>打开生物的道具栏GUI。
	 * <p>警告：如果{@link IBefriendedMob#makeMenu()}函数返回null则不要调用这个函数，否则游戏会崩溃。
	 */
	public static void openBefriendedInventory(Player player, IBefriendedMob mob) {
		LivingEntity living = (LivingEntity) mob;
		if (!player.level.isClientSide && player instanceof ServerPlayer sp
				&& (!living.isVehicle() || living.hasPassenger(player)))
		{
			
			if (player.containerMenu != player.inventoryMenu)
			{
				player.closeContainer();
			}

			sp.nextContainerCounter();
			ClientboundBefriendedGuiOpenPacket packet = new ClientboundBefriendedGuiOpenPacket(sp.containerCounter,
					mob.getAdditionalInventory().getContainerSize(), living.getId());
			sp.containerMenu = mob.makeMenu(sp.containerCounter, sp.getInventory(), mob.getAdditionalInventory());
			if (sp.containerMenu == null)
				return;
			BMChannels.BM_CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), packet);
			sp.initMenu(sp.containerMenu);
			MinecraftForge.EVENT_BUS.post(
					new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.containerMenu));
		}
	}

	/**
	 * Get the Mod Id which the mob belongs to, with an nbt for deserialization before the mob spawns
	 * <p>使用一个用于读档的NBT标签，在未实际生成生物前获取生物所属的MOD ID
	 */
	public static String getModIdFromNbt(CompoundTag nbt)
	{
		return nbt.contains("befriended_mod_id", NbtHelper.TAG_STRING_ID) ?
				nbt.getString("befriended_mod_id") : null;
	}
	
	public static UUID getOwnerUUIDFromNbt(CompoundTag nbt)
	{
		String modid = getModIdFromNbt(nbt);
		if (modid == null)
			return null;
		return nbt.contains(modid + ":befriended_owner", NbtHelper.TAG_INT_ARRAY_ID) ? nbt.getUUID(modid + ":befriended_owner") : null;
	}
	
	public static MutableComponent getNameFromNbt(CompoundTag nbt, EntityType<?> type)
	{
		String modid = getModIdFromNbt(nbt);
		if (modid == null)
			return null;
		if (nbt.contains("CustomName", 8)) {
            String s = nbt.getString("CustomName");
            return Component.Serializer.fromJson(s);
        }
		else return (MutableComponent)(type.getDescription());
	}
}
