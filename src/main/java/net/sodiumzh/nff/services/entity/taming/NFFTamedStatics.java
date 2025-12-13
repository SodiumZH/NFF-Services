package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nff.services.network.ClientboundNFFGUIOpenPacket;
import net.sodiumzh.nff.services.network.NFFChannels;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUNetworkStatics;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A function library for befriended mobs
 */

public class NFFTamedStatics
{

	/* AI */

	/**
	 *  Default settings of the rule about what the mob can attack.
	 */
	public static boolean wantsToAttackDefault(INFFTamed mob, LivingEntity target) {
		if (target instanceof Creeper && !mob.canAttackCreeper())
			return false;
		else if (target instanceof Ghast && !mob.canAttackGhast())
			return false;
		else return !mob.isAllyTo(target);
	}

	/* Save & Load */

	/*@Deprecated
	public static void addBefriendedCommonSaveData(INFFTamed mob, CompoundTag nbt, String modId) {		
		addBefriendedCommonSaveData(mob, nbt);
	}*/

	/**
	 * @deprecated No longer used, moved to data
	 */
	/*@Deprecated
	public static void addBefriendedCommonSaveData(INFFTamed mob, CompoundTag nbt)
	{
		nbt.put("bm_common", new CompoundTag());
		nbt.getCompound("bm_common").putString("mod_id", mob.getModId());
		if (mob.getOwnerUUID() != null)
			nbt.getCompound("bm_common").putUUID("owner", mob.getOwnerUUID());
		nbt.getCompound("bm_common").putString("ai_state", mob.getAIState().getId().toString());
		mob.getAdditionalInventory().saveToTag(nbt.getCompound("bm_common"), "inventory");*/
		
		/*String modId = mob.getModId();
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
	}*/
	
	/*@Deprecated	// Use version without modid input
	public static void readBefriendedCommonSaveData(INFFTamed mob, CompoundTag nbt, String inModId)
	{
		readBefriendedCommonSaveData(mob, nbt);
	}*/

	/*public static void readBefriendedCommonSaveData(INFFTamed mob, CompoundTag nbt) {
		
		if (nbt.contains("bm_common", NFUNBTStatics.TAG_COMPOUND_ID))
		{
			if (nbt.getCompound("bm_common").getUUID("owner") == null)
			{
				new IllegalStateException("Reading befriended mob data error: invalid owner. Was INFFTamed.init() not called?").printStackTrace();
				return;
			}
			mob.setOwnerUUID(nbt.getCompound("bm_common").getUUID("owner"));
			mob.init(mob.getOwnerUUID(), null);
			if (nbt.getCompound("bm_common").contains("ai_state", Tag.TAG_STRING))
				mob.setAIState(NFFTamedMobAIState.fromID(new ResourceLocation(nbt.getCompound("bm_common").getString("ai_state"))), false);
			else mob.setAIState(NFFTamedMobAIState.WAIT, false);
			mob.getAdditionalInventory().readFromTag(nbt.getCompound("bm_common").getCompound("inventory"));
		}
	}*/

	/**
	 * Convert a befriended mob to other type. This action will keep its data.
	 * @param target The mob to convert.
	 * @param newType The type converting to, must implement {@code INFFTamed} interface.
	 * @return The new mob reference.
	 */
	public static INFFTamed convertToOtherBefriendedType(INFFTamed target, EntityType<? extends Mob> newType)
	{
		// Additional inventory will be invalidated upon convertion, so backup as a tag
		CompoundTag mobTag = new CompoundTag();
		target.asMob().saveWithoutId(mobTag);
		// Do convertion
		
		Mob newMob = NFUEntityStatics.replaceMob(newType, target.asMob());
		if (INFFTamed.get(newMob).isEmpty())
			throw new UnsupportedOperationException("NFFTamedStatics::convertToOtherBefriendedType supports mobs implementing INFFTamed.");
		newMob.load(mobTag);
		// Write the inventory back
		/*if(inventoryTag.getInt("size") != newMob.getAdditionalInventory().getContainerSize())
			throw new UnsupportedOperationException("NFFTamedStatics::convertToOtherBefriendedType additional inventory must have same size before and after conversion.");
		newMob.getAdditionalInventory().readFromTag(inventoryTag);
		// Do other settings
		newMob.setAIState(target.getAIState(), false);
		newMob.init(target.getOwnerUUID(), target.asMob());
		newMob.updateFromInventory();*/
		// setInit() needs to call manually
		
		return (INFFTamed)newMob;
	}
	
	/* Inventory */

	/**
	 * Open the inventory GUI of the mob.
	 * <p>Warning: DO NOT call this if {@link INFFTamed#makeMenu} method returns null, otherwise it will crash the game.
	 */
	public static void openBefriendedInventory(Player player, INFFTamed mob) {
		LivingEntity living = (LivingEntity) mob;
		if (!player.level.isClientSide && player instanceof ServerPlayer sp
				&& (!living.isVehicle() || living.hasPassenger(player)))
		{
			
			if (player.containerMenu != player.inventoryMenu)
			{
				player.closeContainer();
			}

			sp.nextContainerCounter();
			ClientboundNFFGUIOpenPacket packet = new ClientboundNFFGUIOpenPacket(sp.containerCounter,
					mob.getAdditionalInventory().getContainerSize(), living.getId());
			NFUNetworkStatics.sendToPlayer(NFFChannels.BM_CHANNEL, packet, sp);
			sp.containerMenu = mob.makeMenu(sp.containerCounter, sp.getInventory(), mob.getAdditionalInventory());
			if (sp.containerMenu == null)
				return;
			sp.initMenu(sp.containerMenu);
			MinecraftForge.EVENT_BUS.post(
					new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.containerMenu));
		}
	}

	/**
	 * Get the Mod Id which the mob belongs to, with an nbt for deserialization before the mob spawns
	 * <p>使用一个用于读档的NBT标签，在未实际生成生物前获取生物所属的MOD ID
	 * @deprecated Use {@link CNFFTamedCommonData#getModIdFromMobTag} instead
	 */
	@Deprecated
	public static String getModIdFromNbt(CompoundTag nbt)
	{
		return CNFFTamedCommonData.getModIdFromMobTag(nbt);
	}
	
	/**
	 * @deprecated Use {@link CNFFTamedCommonData#getOwnerUUIDFromMobTag} instead
	 */
	@Deprecated
	public static UUID getOwnerUUIDFromNbt(CompoundTag nbt)
	{
		return CNFFTamedCommonData.getOwnerUUIDFromMobTag(nbt);
	}
	
	/**
	 * @deprecated Use {@link NFUEntityStatics#getNameFromNbt} instead
	 */
	@Deprecated
	public static Component getNameFromNbt(CompoundTag nbt, EntityType<?> type)
	{
		return NFUEntityStatics.getNameFromNbt(nbt, type);
	}
	
	/**
	 * Get owner if the owner is closer than the given distance of the mob. Otherwise return {@link Optional#empty}.
	 * @param mob Mob (implements {@link INFFTamed}) to test. No need to do {@link INFFTamed#isOwnerInDimension} check.
	 * @param radius Search area
	 * @param sphericalArea If true, it will search in a sphere with given radius. Otherwise search in a box with given radius.
	 * @return Owner if the owner is present and in the given area. Otherwise {@link Optional#empty}.
	 */
	public static Optional<Player> getOwnerInArea(INFFTamed mob, double radius, boolean sphericalArea)
	{
		if (!mob.isOwnerInDimension())
			return Optional.empty();
		List<Entity> list = mob.asMob().level.getEntities(mob.asMob(), NFUEntityStatics.getNeighboringArea(mob.asMob(), radius), e -> e == mob.getOwner());
		if (list.isEmpty())
			return Optional.empty();
		else if (!sphericalArea)
			return Optional.of((Player)(list.get(0)));
		else if (mob.asMob().distanceToSqr(list.get(0)) <= radius * radius)
			return Optional.of((Player)(list.get(0)));
		else return Optional.empty();
	}

	public static List<Mob> getOwningMobsInArea(Player player, EntityType<? extends Mob> type, double radius, boolean sphericalArea)
	{
		Stream<Entity> stream = player.level.getEntities(player, player.getBoundingBox().inflate(radius, radius, radius),
				e -> (e.getType() == type && e instanceof INFFTamed bm && bm.getOwner() == player)).stream();
		if (sphericalArea)
			stream = stream.filter(e -> e.distanceToSqr(player) <= radius * radius);
		return stream.map(e -> (Mob)e).collect(Collectors.toList());
	}
	
	/**
	 * Check if a living entity ({@code test}) should be considered as ally by an {@code OwnableEntity} ({@code entity}) under BMF rule.
	 * <p>This method is private because it doesn't involve BM, so directly calling this may cause unexpected
	 * behavior changes on vanilla mobs. Call {@code isLivingAlliedToBM} and {@code isBMAlliedToOwnable} instead.
	 * <p>On server only. On client always {@code false}.
	 */
	static boolean isLivingAlliedToOwnableUnsafe(OwnableEntity ownable, LivingEntity target)
	{
		if (ownable == null || target == null) return false;
		Level level = target.level;
		if (level.isClientSide) return false;
		// Get the actual mob. In the future INFFTamed may become a capability and may not refer to the mob itself
		// Null means impossible to get the mob reference from the argument, and only owners will be compared
		LivingEntity ownableMob = ownable instanceof INFFTamed t ? t.asMob() : (ownable instanceof LivingEntity l ? l : null);
		if (target.equals(ownableMob)) return true;
		// Recursively search self and owners
		Set<UUID> selfAndOwners = new HashSet<>();
		Entity ptr = ownableMob != null ? ownableMob : (ownable.getOwner() != null ? ownable.getOwner() : null);
		if (ptr == null && ownable.getOwnerUUID() != null) selfAndOwners.add(ownable.getOwnerUUID());
		while (ptr != null) {
			selfAndOwners.add(ptr.getUUID());
			Entity ptrCopy = ptr;
			UUID uuid = INFFTamed.get(ptrCopy).map(INFFTamed::getOwnerUUID).orElseGet(() ->
				ptrCopy instanceof OwnableEntity o ? o.getOwnerUUID() : null);
			Entity owner = INFFTamed.get(ptrCopy).map(t -> (Entity) t.getOwner()).orElseGet(() ->
				ptrCopy instanceof OwnableEntity o ? o.getOwner() : null);
			if (uuid != null) {
				if (selfAndOwners.contains(uuid)) break;	// Preventing cyclic reference in getOwner()
				selfAndOwners.add(uuid);
			}
			ptr = owner;	// When the owner exists but not in level, it's still possible to record this owner, but not above
		}
		// Recursively search target and owners
		Set<UUID> targetAndOwners = new HashSet<>();
		Entity ptr1 = target;
		while (ptr1 != null) {
			targetAndOwners.add(ptr1.getUUID());
			Entity ptrCopy = ptr1;
			UUID uuid = INFFTamed.get(ptrCopy).map(INFFTamed::getOwnerUUID).orElseGet(() ->
				ptrCopy instanceof OwnableEntity o ? o.getOwnerUUID() : null);
			Entity owner = INFFTamed.get(ptrCopy).map(t -> (Entity) t.getOwner()).orElseGet(() ->
				ptrCopy instanceof OwnableEntity o ? o.getOwner() : null);
			if (uuid != null) {
				if (targetAndOwners.contains(uuid)) break;	// Preventing cyclic reference in getOwner()
				targetAndOwners.add(uuid);
			}
			ptr1 = owner;	// When the owner exists but not in level, it's still possible to record this owner, but not above
		}
		// Compare UUID to cover cases when owner is not present
		// Case when the target is owned by self or self's owner
		boolean selfIsPlayerOwned = false;
		for (UUID uuid: selfAndOwners) {
			if (targetAndOwners.contains(uuid)) return true;
			if (level.getPlayerByUUID(uuid) != null) selfIsPlayerOwned = true;
		}
		// Case when the target is owned by someone and pvp isn't allowed
		if (level.getServer() != null && !level.getServer().isPvpAllowed() && selfIsPlayerOwned) {
			if (targetAndOwners.stream().anyMatch(uuid -> level.getPlayerByUUID(uuid) != null)) return true;
		}
		return false;
	}

	/**
	 * Check if a BM is considered as ally by an {@code OwnableEntity}.
	 * <p>On server only. On client always {@code false}.
	 * @deprecated Use {@link INFFTamed#isAllyTo} instead.
	 */
	@Deprecated
	public static boolean isBMAlliedToOwnable(OwnableEntity entity, INFFTamed test)
	{
		if (entity instanceof INFFTamed i)
			return test.isAllyTo(i.asMob());
		else if (entity instanceof LivingEntity le)
			return test.isAllyTo(le);
		else return false;
	}
	
	/**
	 * Check if a {@code LivingEntity} is considered as ally by a BM.
	 * <p>On server only. On client always {@code false}.
	 * @deprecated Use {@link INFFTamed#isAllyTo} instead.
	 */
	@Deprecated
	public static boolean isLivingAlliedToBM(INFFTamed bm, LivingEntity test)
	{
		return bm.isAllyTo(test);
	}

	/**
	 * Get the LivingEntity instance from {@link OwnableEntity} interface. It handles both
	 * {@link INFFTamed} cases and Livings directly implementing {@link OwnableEntity}.
	 * <p>Generally it shouldn't return {@link Optional#empty}, but as we cannot guarantee
	 * other mods don't attach OwnableEntity to non-living classes, we still use optional here
	 */
	@Nullable
	public static Optional<LivingEntity> livingFromOwnableInterface(OwnableEntity ownable) {
		return Optional.ofNullable(INFFTamed.get(ownable).map(e -> (LivingEntity) e.asMob())
			.orElseGet(() -> ownable instanceof LivingEntity l ? l : null));
	}

	/**
	 * Get the {@link OwnableEntity} interface from LivingEntity instance. It handles both
	 * {@link INFFTamed} cases and Livings directly implementing {@link OwnableEntity}.
	 * <p>Empty if the mob doesn't use {@link INFFTamed} or directly implement {@link OwnableEntity}
	 */
	public static Optional<OwnableEntity> ownableFromLiving(LivingEntity living) {
		return Optional.ofNullable(INFFTamed.get(living).map(i -> (OwnableEntity)i)
			.orElseGet(() -> living instanceof OwnableEntity o ? o : null));
	}
}
