package net.sodiumstudio.befriendmobs.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedChangeAiStateEvent;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendedMobData;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImpl;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImplDefault;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.item.MobRespawnerItem;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.befriendmobs.registry.BMItems;
import net.sodiumstudio.nautils.Wrapped;
import net.sodiumstudio.nautils.annotation.DontCallManually;
import net.sodiumstudio.nautils.annotation.DontOverride;

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
		return this.getTempData().hasInit;
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
		this.getTempData().hasInit = true;
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
		this.getTempData().hasInit = false;
	}
	
	/* Ownership */
	
	/** 
	 * Get owner as player entity.
	 * @return Owner as entity, or null if the owner is absent in the level.
	* <p>Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	* <p>获取拥有者的玩家实体。
	* <p>拥有者实体，若拥有者不在世界中时返回null。
	* <p>警告：在初始化时调用此函数请谨慎！如果拥有者尚未初始化，此函数会返回null。
	*/
	@DontOverride
	@Nullable
	public default Player getOwner() 
	{
		if (getOwnerUUID() != null)
			return asMob().level.getPlayerByUUID(getOwnerUUID());
		else return null;
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
	 * <p>检查拥有者是否在同一世界中。
	 */
	@DontOverride
	public default boolean isOwnerPresent()
	{
		return getOwner() != null;
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
	
	/** Get the previous target before updating target.
	* This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	*/
	@DontOverride
	@DontCallManually
	public default LivingEntity getPreviousTarget()
	{
		return this.getTempData().previousTarget;
	}
	
	/** Get the previous target after updating target.
	* This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	*/
	@DontOverride
	@DontCallManually
	public default void setPreviousTarget(LivingEntity target)
	{
		this.getTempData().previousTarget = target;
	}
	
	/** Get the anchor pos that the mob won't stroll too far from it
	* If you want to disable anchor, just override this method and return null
	*/
	@Nullable
	public default Vec3 getAnchorPos() 
	{
		return this.getTempData().anchor;
	}
	
	@DontOverride
	public default void setAnchorPos(Vec3 pos) 
	{
		this.getTempData().anchor = pos;
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

	@DontOverride
	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume)
	{
		Wrapped.Boolean succeeded = new Wrapped.Boolean(false);		
		this.asMob().getCapability(BMCaps.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			succeeded.set(l.applyHealingItem(stack, value, consume));
		});		
		return succeeded.get();
	}
	
	/** Add all usable items here, including non-consuming items. Value is HP it can heal. */
	public default Map<Item, Float> getHealingItems()
	{
		return new HashMap<Item, Float>();
	}
	
	// Specify which items in the map above don't consume after usage
	public default Set<Item> getNonconsumingHealingItems()
	{	
		return new HashSet<Item>();
	}
	
	@DontOverride
	public default InteractionResult tryApplyHealingItems(ItemStack stack)
	{
		if (stack.isEmpty())
			return InteractionResult.PASS;
		if (getHealingItems().containsKey(stack.getItem()))
		{
			if (getNonconsumingHealingItems().contains(stack.getItem()))
			{
				return applyHealingItem(stack, getHealingItems().get(stack.getItem()), false) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			}
			else return applyHealingItem(stack, getHealingItems().get(stack.getItem()), true) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
		
		return InteractionResult.PASS;
	}
	
	/* Respawn */
	
	/**
	 * Get the type (subclass) of respawner it will drop.
	 * Return null to disable respawner dropping.
	 */
	@Nullable
	public MobRespawnerItem getRespawnerType();
	
	/**
	 * If true, the respawner will be invulnerable (except creative players, /kill commands and the void)
	 */
	public default boolean isRespawnerInvulnerable()
	{
		return true;
	}
	
	/**
	 * If true, the respawner will be lifted up on drop into the void
	 */
	public default boolean shouldRespawnerRecoverOnDropInVoid()
	{
		return true;
	}
	
	/**
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
	public String getModId();
	
	public default CBefriendedMobData.Values getTempData()
	{
		Wrapped<CBefriendedMobData> res = new Wrapped<CBefriendedMobData>(null);
		asMob().getCapability(BMCaps.CAP_BEFRIENDED_MOB_TEMP_DATA).ifPresent((cap) ->
		{
			res.set(cap);
		});
		if (res.get() == null)
			// Sometimes it's called after the capability is detached, so return a temporal dummy cap
			return new CBefriendedMobData.Values(this);	
		return res.get().values();
	}

}
