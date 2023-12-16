package net.sodiumstudio.befriendmobs.entity.befriended;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.bmevents.entity.ai.BefriendedChangeAiStateEvent;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImpl;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImplDefault;
import net.sodiumstudio.befriendmobs.entity.capability.HealingItemTable;
import net.sodiumstudio.befriendmobs.events.BMEntityEvents;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.item.MobRespawnerItem;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.nautils.Wrapped;
import net.sodiumstudio.nautils.annotation.DontCallManually;
import net.sodiumstudio.nautils.annotation.DontOverride;
import net.sodiumstudio.nautils.object.ItemOrKey;

public interface IBefriendedMob extends ContainerListener  {

	/* Initialization */
	
	/** Initialize a mob.
	 * On reading from NBT, the befriendedFrom mob is null, so implementation must handle null cases.
	 * @param playerUUID Player UUID who owns this mob.
	 * @param from The source mob from which this mob was befriended or converted. NULLABLE!
	 * <p>========
	 * <p>初始化生物。
	 * <p>在读取NBT时{@code befriendedFrom}生物为null，因此实现必须处理null的情况。
	 * @param playerUUID 拥有此生物的玩家UUID。
	 * @param from 友好化或转化为该生物的来源生物。可以为null！
	 */
	@DontOverride
	public default void init(@Nonnull UUID playerUUID, @Nullable Mob from)
	{
		this.setOwnerUUID(playerUUID);
		if (from != null)
		{
			this.asMob().setHealth(from.getHealth());
		}
		this.setInventoryFromMob();
		this.updateAttributes();
		if (this.getAnchorPos() != null)
		{
			this.setAnchorPos(this.asMob().position());
		}
		this.onInit(playerUUID, from);
	}

	/**
	 * Custom actions invoked after {@link IBefriendedMob#init(UUID, Mob)}.
	 * On reading from NBT, the befriendedFrom mob is null, so implementation must handle null cases.
	 * @param playerUUID Player UUID who owns this mob.
	 * @param from The source mob from which this mob was befriended or converted. NULLABLE!
	 * <p>========
	 * <p>在初始化{@link IBefriendedMob#init(UUID, Mob)}后执行的自定义操作。
	 * <p>在读取NBT时{@code befriendedFrom}生物为null，因此实现必须处理null的情况。
	 * @param playerUUID 拥有此生物的玩家UUID。
	 * @param from 友好化或转化为该生物的来源生物。可以为null！
	 */
	@DontCallManually
	public default void onInit(@Nonnull UUID playerUUID, @Nullable Mob from) {}

	/**
	 * Get whether this mob has finished initialization.
	 * <p>After finishing initialization the mob will start updating from its inventory.
	 * <p>获取是否该生物已经完成初始化。
	 * <p>在完成初始化后，生物将开始基于附加道具栏更新。
	 */
	@DontOverride
	public default boolean hasInit()
	{
		return this.getData().hasInit();
	}
	
	/** Label a mob as finished initialization after reading nbt, copying from other, etc.
	 * <p>Only after labeled init, the mob will update from inventory.
	 * <p>After spawning and deserializing, call this.
	 * <p>Don't worry about if the presets in BefriendMobs API has already labeled init, 
	 * as labeling again will not do anything if so.
	 * <p>标记一个生物为已初始化，在进行读取NBT、从其他对象复制等操作之后。
	 * <p>在生成和读档之后调用此函数。
	 * <p>无需考虑BefriendMobs API的预设中是否已经标记了已初始化。重复标记不会做任何事情。
	 */
	@DontOverride
	public default void setInit()
	{
		this.getData().setInitState(true);;
	}

	/** Label a mob not finished initialization.
	 * <p>Call this only when the presets has labeled init but you need some extra actions that needs to keep it not init.
	 * <p>Currently the init label affects only inventory updating.
	 * <p>标记一个生物为未完成初始化。
	 * <p>当预设已经标记为了已初始化，但需要进行的额外操作要求保持未初始化时，调用此函数。
	 * <p>目前已初始化标记仅用于附加道具栏更新。
	 */
	@DontOverride
	public default void setNotInit()
	{
		this.getData().setInitState(false);
	}
	
	/* Ownership */
	
	/** 
	 * Get owner as player entity.
	 * @return Owner as entity, or null if the owner is absent in the level.
	 * @deprecated Use {@code getOwnerInDimension} or {@code getOwnerInWorld} instead.
	* <p>Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	* <p>获取拥有者的玩家实体。
	* <p>拥有者实体，若拥有者不在世界中时返回null。
	* <p>警告：在初始化时调用此函数请谨慎！如果拥有者尚未初始化，此函数会返回null。
	*/
	@DontOverride
	@Nullable
	@Deprecated
	public default Player getOwner() 
	{
		if (getOwnerUUID() != null)
			return asMob().level.getPlayerByUUID(getOwnerUUID());
		else return null;
	}
	
	/**
	 * Get owner if the owner is in the same dimension. Otherwise return {@code null}.
	 */
	@DontOverride
	@Nullable
	public default Player getOwnerInDimension()
	{
		if (getOwnerUUID() != null)
		{
			return this.asMob().level.getPlayerByUUID(getOwnerUUID());
		}
		else return null;
	}
	
	/**
	 * Get owner if the owner is in any dimension. Otherwise return {@code null}.
	 * <p> In client it will only check the loaded dimension.
	 */
	@DontOverride
	@Nullable
	public default Player getOwnerInWorld()
	{
		if (getOwnerUUID() != null)
		{
			if (this.asMob().level.isClientSide)
			{
				return getOwnerInDimension();
			}
			else
			{
				MinecraftServer sv = this.asMob().level.getServer();
				Player owner = null;
				for (Level level: sv.getAllLevels())
				{
					owner = level.getPlayerByUUID(getOwnerUUID());
					if (owner != null)
						return owner;
				}
				return null;
			}
		}
		return null;
	}
	
	
	/** 
	 * Get owner as UUID.
	* <p>Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	* <p>获取拥有者的UUID。
	* <p>警告：在初始化时调用此函数请谨慎！如果拥有者尚未初始化，此函数会返回null。
	*/
	@DontOverride
	@Nullable
	public default UUID getOwnerUUID()
	{
		if (!asMob().getEntityData().get(getOwnerUUIDAccessor()).isPresent())
		{
			BefriendMobs.LOGGER.error("Befriended mob \"" + this.asMob().getName().getString() + "\" missing owner. If this happens not on initialization phase, maybe IBefriendedMob#init() wasn't called on init.");
			return null;
		}
		return asMob().getEntityData().get(getOwnerUUIDAccessor()).get();
	}
	
	/** Set owner from player entity.
	 * <p>从玩家实体设置拥有者。
	 */
	@DontOverride
	public default void setOwner(@Nonnull Player owner)
	{
		setOwnerUUID(owner.getUUID());
	}
	
	/**
	* Set owner from player UUID.
	* <p>从玩家UUID设置拥有者。
	*/
	@DontOverride
	public default void setOwnerUUID(@Nonnull UUID ownerUUID)
	{
		asMob().getEntityData().set(getOwnerUUIDAccessor(), Optional.of(ownerUUID));
	}
	
	/**
	 * Get owner UUID as {@link EntityDataAccessor}. Attach this to accessor defined in mob class.
	 * <p>以实体数据访问器（{@link EntityDataAccessor}）的形式获取拥有者UUID。在生物类中将该方法关联到相应访问器上。
	 */
	public EntityDataAccessor<Optional<UUID>> getOwnerUUIDAccessor();
	
	/**
	 * Check if owner is in the level.
	 * @deprecated Use {@code isOwnerInDimension} or {@code isOwnerInWorld} instead.
	 * <p>检查拥有者是否在同一世界中。
	 */
	@DontOverride
	public default boolean isOwnerPresent()
	{
		return getOwner() != null;
	}
	
	/**
	 * Check if the owner is in the same dimension as the mob.
	 */
	@DontOverride
	public default boolean isOwnerInDimension()
	{
		return this.getOwnerInDimension() != null;
	}
	
	/**
	 * Check if the owner is in the server in any dimension.
	*/
	@DontOverride
	public default boolean isOwnerInWorld()
	{
		return this.getOwnerInWorld() != null;
	}
	
	/* -------------------------------------------------------- */
	/* AI configs */
	
	/** 
	 * Get the AI state as {@link EntityDataAccessor}. Attach this to accessor defined in mob class.
	 * <p>以实体数据访问器（{@link EntityDataAccessor}）的形式获取AI状态。在生物类中将该方法关联到相应访问器上。
	*/
	public EntityDataAccessor<Integer> getAIStateData();
	
	/** 
	 * Get current AI state as enum.
	 * <p>以枚举类的形式获取当前AI状态。
	 */
	@DontOverride
	public default BefriendedAIState getAIState()
	{
		return BefriendedAIState.fromID(asMob().getEntityData().get(getAIStateData()));
	}
	
	/** A preset action when switching AI e.g. on right click.
	 * By default it cycles among Wait, Follow and Wander.
	 * <p>DO NOT override this. Override {@code getNextAIState()} instead.
	 * @return The new AI state.
	 * <p>========
	 * <p>切换AI的操作预设，例如按下右键时。默认会在等待、跟随和游荡之间循环切换。
	 * <p>不要重载这个函数。如有需要请重载{@code getNextAIState()}。
	 * @return 新的AI状态。
	 */
	@DontOverride
	public default BefriendedAIState switchAIState()
	{		
		BefriendedAIState nextState = getNextAIState();
		if (MinecraftForge.EVENT_BUS.post(new BefriendedChangeAiStateEvent(this, getAIState(), nextState)))
			return getAIState();
		setAIState(nextState, false);
		return nextState;
	}
	
	/**
	 * Get the next AI State after a switching action e.g. right click.
	 * <p>Called in {@code switchAIState()} above.
	 * <p>获取切换后的AI状态。
	 * <p>在上面的{@code switchAIState()}中调用。
	 */
	@DontCallManually
	public default BefriendedAIState getNextAIState()
	{
		BefriendedAIState state = getAIState();
		if (BefriendedAIState.fromID(state.id + 1) != null)
		{
			return BefriendedAIState.fromID(state.id + 1);
		}
		else return BefriendedAIState.fromID(0);
	}
	
	/**
	 * Set the AI state.
	 * @param postEvent Whether it should post a {@link BefriendedChangeAiStateEvent}.
	 * <p>========
	 * <p>设置AI状态。
	 * @param postEvent 是否需要发射{@link BefriendedChangeAiStateEvent}事件。
	 */
	@DontOverride
	public default void setAIState(BefriendedAIState state, boolean postEvent)
	{
		if (state == getAIState())
			return;
		if (postEvent && MinecraftForge.EVENT_BUS.post(new BefriendedChangeAiStateEvent(this, getAIState(), state)))
			return;
		asMob().getEntityData().set(getAIStateData(), state.id);
	}
	
	/** Get if a target mob can be attacked by this mob.
	 * Called in target goals.
	*/

	public default boolean wantsToAttack(LivingEntity pTarget)
	{
		return BefriendedHelper.wantsToAttackDefault(this, pTarget);
	}
	
	/** 
	 * <b> Don't call manually! </b> This method is only called in {@link BMEntityEvents#onLivingChangeTarget}. 
	 * Get the previous target before updating target.
	 * This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	 */
	@DontOverride
	@DontCallManually
	public default LivingEntity getPreviousTarget()
	{
		return this.getData().getPreviousTarget();
	}
	
	/** 
	* <b> Don't call manually! </b> This method is only called in {@link BMEntityEvents#onLivingChangeTarget}. 
	* Get the previous target after updating target.
	* This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	*/
	@DontOverride
	@DontCallManually
	public default void setPreviousTarget(LivingEntity target)
	{
		this.getData().setPreviousTarget(target);;
	}
	
	/** Get the anchor pos that the mob won't stroll too far from it
	* If you want to disable anchor, just override this method and return null
	*/
	@Nullable
	public default Vec3 getAnchorPos() 
	{
		return this.getData().getAnchor();
	}
	
	@DontOverride
	public default void setAnchorPos(Vec3 pos) 
	{
		this.getData().setAnchor(pos);
	}
	
	public default double getAnchoredStrollRadius()  
	{
		return 64.0d;
	}
	
	/**
	 * Check if a position is further than the stroll radius to the anchor point.
	 * Called in random stroll goals.
	 */
	@DontOverride
	public default boolean isTooFarFromAnchor(Vec3 v)
	{
		Vec3 a = getAnchorPos();
		if (a == null)
			return false;
		double dx = v.x - a.x;
		double dz = v.z - a.z;
		return dx * dx + dz * dz > getAnchoredStrollRadius() * getAnchoredStrollRadius();		
	}
	
	/**
	 * Check if a position is further than the stroll radius to the anchor point.
	 * Called in random stroll goals.
	 */
	@DontOverride
	public default boolean isTooFarFromAnchor(BlockPos pos)
	{
		return 	isTooFarFromAnchor(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
	}
	
	/**
	 * Update anchor point on tick. When the mob isn't waiting, the anchor will follow it;
	 * when the mob enters waiting state, the anchor will stop and the mob gets anchored.
	 * Called on world tick only. Don't call anywhere else.
	 */
	@DontCallManually
	@DontOverride
	public default void updateAnchor()
	{
		if (getAnchorPos() != null)
			setAnchorPos(asMob().position());
	}
	
	/* Inventory */
	
	public BefriendedInventory getAdditionalInventory();

	public int getInventorySize();
	
	// Set mob data from befriendedInventory.
	public void updateFromInventory();
	
	// Set befriendedInventory from mob data, usually for initializing
	public void setInventoryFromMob();
	
	// Get item stack from position in inventory tag
	@DontOverride
	public default ItemStack getInventoryItemStack(int pos)
	{
		if (pos < 0 || pos >= getInventorySize())
			throw new IndexOutOfBoundsException();
		return this.getAdditionalInventory().getItem(pos);
	}
	
	// Get item (type) from position in inventory tag
	@DontOverride
	public default Item getInventoryItem(int pos)
	{
		return this.getInventoryItemStack(pos).getItem();
	}

	@Nullable
	public BefriendedInventoryMenu makeMenu(int containerId, Inventory playerInventory, Container container);

	/* ContainerListener interface */
	/** DO NOT override this. Override onInventoryChanged instead. */
	@DontOverride
	@Override
	public default void containerChanged(Container pContainer) 
	{
		if (!(pContainer instanceof BefriendedInventory))
			throw new UnsupportedOperationException("IBefriendedMob container only receives BefriendedInventory.");
		if (hasInit())
			updateFromInventory();
		updateAttributes();
		onInventoryChanged();
	}

	public default void onInventoryChanged() 
	{
	}

	/**
	 * @deprecated Not implemented
	 */
	@Deprecated
	public default boolean dropInventoryOnDeath()
	{
		return true;
	}
	
	/* Healing related */	

	/**
	 * Get the implementation type of healing handler.
	 */
	public default Class<? extends CHealingHandlerImpl> healingHandlerClass()
	{
		return CHealingHandlerImplDefault.class;
	}

	/**
	 * @deprecated Use cooldown-sensitive version instead
	 */
	@Deprecated
	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume)
	{
		Wrapped.Boolean succeeded = new Wrapped.Boolean(false);		
		this.asMob().getCapability(BMCaps.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			@SuppressWarnings("deprecation")
			int cooldown = l.getHealingCooldownTicks();
			succeeded.set(l.applyHealingItem(stack, value, consume, cooldown));
		});		
		return succeeded.get();
	}
	
	@DontOverride
	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume, int cooldown)
	{
		Wrapped<Boolean> succeeded = new Wrapped<>(false);		
		this.asMob().getCapability(BMCaps.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			succeeded.set(l.applyHealingItem(stack, value, consume, cooldown));
		});		
		return succeeded.get();
	}
	
	/** Add all usable items here, including non-consuming items. Value is HP it can heal. */
	@Nullable
	public default HealingItemTable getHealingItems()
	{
		return null;
	}
	
	/**
	 * @deprecated No longer used. Use {@link HealingItemTable#noConsume()} instead on {@link HealingItemTable} construction in {@code getHealingItems}. 
	 */
	@Nullable
	@Deprecated
	public default Set<ItemOrKey> getNonconsumingHealingItems()
	{	
		return null;
	}
	
	@DontOverride
	public default InteractionResult tryApplyHealingItems(ItemStack stack)
	{
		if (stack.isEmpty())
			return InteractionResult.PASS;
		HealingItemTable table = getHealingItems();
		if (table == null) 
		{
			return InteractionResult.PASS;
		}
		HealingItemTable.Output output = table.getOutput(this.asMob(), stack);
		if (output != null)
		{
			return applyHealingItem(stack, output.amount(), !output.noConsume(), output.cooldown()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}
	
	/* Respawn */
	
	public static enum DeathRespawnerGenerationType
	{
		GIVE,	// Directly give the repawner to the player
		DROP,	// Drop the respawner on the ground
		NONE	// Dont generate respawner
	}
	
	/**
	 * Defines how the respawner should be generated after mob dies.
	 */
	public default DeathRespawnerGenerationType getDeathRespawnerGenerationType()
	{
		return DeathRespawnerGenerationType.DROP;
	}

	/**
	 * (DROP only) 
	 * Get the type (subclass) of respawner it will drop.
	 * If it's null, it will be handled as if generation type is NONE.
	 */
	@Nullable
	public MobRespawnerItem getRespawnerType();
	
	/**
	 * (DROP only)
	 * If true, the respawner will be invulnerable (except creative players, /kill commands and the void)
	 * <p> This works only when Generation Type is {@code DROP}. 
	 */
	public default boolean isRespawnerInvulnerable()
	{
		return true;
	}
	
	/**
	 * (DROP only)
	 * If true, the respawner will be lifted up on drop into the void
	 */
	public default boolean shouldRespawnerRecoverOnDropInVoid()
	{
		return true;
	}
	
	/**
	 * (DROP only)
	 * If true, the respawner will never expire 
	 */
	public default boolean respawnerNoExpire()
	{
		return true;
	}
	
	/* Misc */
	
	@Deprecated	// Update in tick() or in bauble handler
	public default void updateAttributes() {};

	/**
	 * Get this as Mob.
	 */
	@DontOverride
	public default Mob asMob()
	{
		return (Mob)this;
	}
	
	/**
	 * Get this as IBefriendedMob.
	 */
	@DontOverride
	public default IBefriendedMob get()
	{
		return this;
	}

	/**
	 * Specify the mod ID this mob belongs to.
	 */
	public default String getModId()
	{
		return ForgeRegistries.ENTITY_TYPES.getKey(asMob().getType()).getNamespace();
	}
	
	@Deprecated
	public default CBefriendedMobData.Values getTempData()
	{
		Wrapped<CBefriendedMobData> res = new Wrapped<CBefriendedMobData>(null);
		asMob().getCapability(BMCaps.CAP_BEFRIENDED_MOB_DATA).ifPresent((cap) ->
		{
			res.set(cap);
		});
		if (res.get() == null)
			// Sometimes it's called after the capability is detached, so return a temporal dummy cap
			return new CBefriendedMobData.Values(this);	
		return res.get().values();
	}

	public default CBefriendedMobData getData()
	{
		Wrapped<CBefriendedMobData> res = new Wrapped<CBefriendedMobData>(null);
		asMob().getCapability(BMCaps.CAP_BEFRIENDED_MOB_DATA).ifPresent((cap) ->
		{
			res.set(cap);
		});
		if (res.get() == null)
			// Sometimes it's called after the capability is detached, so return a temporal dummy cap
			return new CBefriendedMobData.Values(this);	
		return res.get();
	}

	public static enum GolemAttitude
	{
		/**
		 * Golems will not proactively attack the mob, but will attack for other reasons
		 */
		NEUTRAL, 
		/**
		 * Golems will keep default attitude, usually hostile to mobs under Monster class.
		 */
		DEFAULT, 
		/**
		 * Golems will be totally passive and never attacks the mob
		 */
		PASSIVE,
		/**
		 * Custom, defined in {@link IBefriendMob#shouldGolemAttack}.
		 */
		CUSTOM
	}
	
	/**
	 * Defines how golems should handle hostility towards this mob.
	 */
	public default GolemAttitude golemAttitude()
	{
		return GolemAttitude.NEUTRAL;
	}
	
	/**
	 * Only when {@link IBefriendedMob#golemAttitude} is {@link GolemAttitude#CUSTOM}, check if a golem should attack
	 * when it attempts to set target to this mob.
	 */
	public default boolean shouldGolemAttack(AbstractGolem golem)
	{
		return true;
	}
	
}
