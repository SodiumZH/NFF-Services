package net.sodiumzh.nff.services.entity.taming;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.annotation.DontCallManually;
import net.sodiumzh.nautils.annotation.DontOverride;
import net.sodiumzh.nautils.containers.CyclicSwitch;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.capability.CHealingHandlerImpl;
import net.sodiumzh.nff.services.entity.capability.CHealingHandlerImplDefault;
import net.sodiumzh.nff.services.entity.capability.HealingItemTable;
import net.sodiumzh.nff.services.event.entity.NFFTamedCommonDataConstructEvent;
import net.sodiumzh.nff.services.event.entity.ai.NFFTamedChangeAiStateEvent;
import net.sodiumzh.nff.services.eventlisteners.NFFEntityEventListeners;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.item.NFFMobRespawnerItem;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public interface INFFTamed extends ContainerListener, OwnableEntity  {

	public static final CyclicSwitch<NFFTamedMobAIState> DEFAULT_AI_SWITCH = new CyclicSwitch<>
		(NFFTamedMobAIState.WAIT, NFFTamedMobAIState.FOLLOW, NFFTamedMobAIState.WANDER);
	
	/* Common */
	/**
	 * Check if an object has an {@code NFFTamed} interface.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this instead of {@code instanceof} check.
	 */
	public static boolean isTamed(Object o)
	{
		if (o instanceof INFFTamed bm)
			return true;
		else return false;
	}
	
	/**
	 * Cast an object to the {@code NFFTamed} interface. Null if failed.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this to cast a mob to NFF Tamed.
	 */
	@Nullable
	public static INFFTamed asTamed(Object o)
	{
		if (o instanceof INFFTamed bm)
			return bm;
		else return null;
	}
	
	/**
	 * Do an action if an object has {@code NFFTamed} interface.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * you can use this to safely cast and do things to BM.
	 * @return Whether the action is invoked.
	 */
	public static boolean ifTamed(Object o, Consumer<INFFTamed> action)
	{
		if (INFFTamed.isTamed(o))
		{
			action.accept(INFFTamed.asTamed(o));
			return true;
		}
		else return false;
	}
	
	/**
	 * Check if a mob has {@code NFFTamed} interface and satisfied the given condition.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this instead of {@code instanceof} check and followed checks of the cast BM.
	 */
	public static boolean isTamedAnd(Object o, Predicate<INFFTamed> cond)
	{
		if (!isTamed(o)) return false;
		return cond.test(asTamed(o));
	}	
	
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
		if (!this.asMob().level.isClientSide)
		{
			this.setOwnerUUID(playerUUID);
			if (from != null)
			{
				this.asMob().setHealth(from.getHealth());
			}
			//this.setInventoryFromMob();
		/*	if (this.getAnchorPos() != null)
			{
				this.setAnchorPos(this.asMob().position());
			}*/
			this.asMob().setPersistenceRequired();
			this.onInit(playerUUID, from);
		}
	}

	/**
	 * Custom actions invoked after {@link INFFTamed#init(UUID, Mob)}.
	 * On reading from NBT, the befriendedFrom mob is null, so implementation must handle null cases.
	 * @param playerUUID Player UUID who owns this mob.
	 * @param from The source mob from which this mob was befriended or converted. NULLABLE!
	 * <p>========
	 * <p>在初始化{@link INFFTamed#init(UUID, Mob)}后执行的自定义操作。
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
	 * <p>Don't worry about if the presets in NFFServices API has already labeled init, 
	 * as labeling again will not do anything if so.
	 * <p>标记一个生物为已初始化，在进行读取NBT、从其他对象复制等操作之后。
	 * <p>在生成和读档之后调用此函数。
	 * <p>无需考虑BefriendMobs API的预设中是否已经标记了已初始化。重复标记不会做任何事情。
	 */
	@DontOverride
	public default void setInit()
	{
		this.getData().setInitState(true);
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
	* <p>Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	* <p>获取拥有者的玩家实体。
	* <p>拥有者实体，若拥有者不在世界中时返回null。
	* <p>警告：在初始化时调用此函数请谨慎！如果拥有者尚未初始化，此函数会返回null。
	*/
	@Override
	@DontOverride
	@Nullable
	public default Player getOwner() 
	{
		return getOwnerInDimension();
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
	* <p>获取拥有者的UUID。
	*/
	@Override
	@DontOverride
	@Nonnull
	public default UUID getOwnerUUID()
	{
		return this.getData().getOwnerUUID();
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
		if (!this.asMob().level.isClientSide)
			this.getData().setOwnerUUID(ownerUUID);
	}

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
	 * Get current AI state as enum.
	 * <p>以枚举类的形式获取当前AI状态。
	 */
	@DontOverride
	public default NFFTamedMobAIState getAIState()
	{
		return this.getData().getAIState();
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
	public default NFFTamedMobAIState switchAIState()
	{		
		NFFTamedMobAIState nextState = getNextAIState();
		if (MinecraftForge.EVENT_BUS.post(new NFFTamedChangeAiStateEvent(this, getAIState(), nextState)))
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
	public default NFFTamedMobAIState getNextAIState()
	{
		NFFTamedMobAIState res = DEFAULT_AI_SWITCH.next(getAIState());
		return res != null ? res : NFFTamedMobAIState.WAIT;
	}
	
	/**
	 * Set the AI state.
	 * @param postEvent Whether it should post a {@link NFFTamedChangeAiStateEvent}.
	 * <p>========
	 * <p>设置AI状态。
	 * @param postEvent 是否需要发射{@link NFFTamedChangeAiStateEvent}事件。
	 */
	@DontOverride
	public default void setAIState(NFFTamedMobAIState state, boolean postEvent)
	{
		if (state == this.getAIState())
			return;
		if (postEvent && MinecraftForge.EVENT_BUS.post(new NFFTamedChangeAiStateEvent(this, getAIState(), state)))
			return;
		this.getData().setAIState(state);
	}
	
	/** Get if a target mob can be attacked by this mob.
	 * Called in target goals.
	*/

	public default boolean wantsToAttack(LivingEntity pTarget)
	{
		return NFFTamedStatics.wantsToAttackDefault(this, pTarget);
	}
	
	/** 
	 * <b> Don't call manually! </b> This method is only called in {@link NFFEntityEventListeners#onLivingChangeTarget}. 
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
	* <b> Don't call manually! </b> This method is only called in {@link NFFEntityEventListeners#onLivingChangeTarget}. 
	* Get the previous target after updating target.
	* This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	*/
	@DontOverride
	@DontCallManually
	public default void setPreviousTarget(LivingEntity target)
	{
		this.getData().setPreviousTarget(target);
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
	
	public default NFFTamedMobInventory getAdditionalInventory() {return this.getData().getAdditionalInventory();}
	
	/**
	 * @deprecated Use {@code createAdditionalInventory} to override inventory.
	 */
	@Deprecated
	public default int getInventorySize() {return getAdditionalInventory().getContainerSize();}
	
	/**
	 * Method to create additional inventory. Invoked on befriended or loaded.
	 */
	public NFFTamedMobInventory createAdditionalInventory();
	
	/**
	 *  Set mob data from befriendedInventory.
	 *  <p><u>DO NOT override this.</u> Create subclasses of {@link NFFTamedMobInventory} and override {@link NFFTamedMobInventory#syncToMob} instead.
	 */
	@DontOverride
	public default void updateFromInventory()
	{
		if (!this.asMob().level.isClientSide)
			this.getAdditionalInventory().syncToMob(this.asMob());
	}
	
	/** Set befriendedInventory from mob data, usually for initializing
	 * <p><u>DO NOT override this.</u> Create subclasses of {@link NFFTamedMobInventory} and override {@link NFFTamedMobInventory#updateFromMob} instead.
	 */
	@DontOverride
	public default void setInventoryFromMob()
	{
		if (!this.asMob().level.isClientSide)
			this.getAdditionalInventory().getFromMob(this.asMob());
	}

	@Nullable
	public NFFTamedInventoryMenu makeMenu(int containerId, Inventory playerInventory, Container container);

	/* ContainerListener interface */
	/** DO NOT override this. Override onInventoryChanged instead. */
	@DontOverride
	@Override
	public default void containerChanged(Container pContainer) 
	{
		if (!(pContainer instanceof NFFTamedMobInventory))
			throw new UnsupportedOperationException("INFFTamed container only receives NFFTamedMobInventory.");
		if (hasInit())
			updateFromInventory();
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

	@DontOverride
	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume, int cooldown)
	{
		MutableObject<Boolean> succeeded = new MutableObject<>(false);		
		this.asMob().getCapability(NFFCapRegistry.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			succeeded.setValue(l.applyHealingItem(stack, value, consume, cooldown));
		});		
		return succeeded.getValue();
	}
	
	/** Add all usable items here, including non-consuming items. Value is HP it can heal. */
	@Nullable
	public default HealingItemTable getHealingItems()
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
	public NFFMobRespawnerItem getRespawnerType();
	
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

	/**
	 * Get this as Mob.
	 */
	@DontOverride
	public default Mob asMob()
	{
		return (Mob)this;
	}
	
	/**
	 * Get this as INFFTamed.
	 */
	@DontOverride
	public default INFFTamed getBM()
	{
		return this;
	}

	/**
	 * Specify the mod ID this mob belongs to.
	 */
	@Deprecated
	public default String getModId()
	{
		return ForgeRegistries.ENTITY_TYPES.getKey(asMob().getType()).getNamespace();
	}

	/**
	 * Get the capability for storage of additional data.
	 */
	public default CNFFTamedCommonData getData()
	{
		MutableObject<CNFFTamedCommonData> res = new MutableObject<CNFFTamedCommonData>(null);
		asMob().getCapability(NFFCapRegistry.CAP_BEFRIENDED_MOB_DATA).ifPresent((cap) ->
		{
			res.setValue(cap);
		});
		if (res.getValue() == null)
			// Sometimes it's called after the capability is detached, so return a temporal dummy cap
			return new CNFFTamedCommonData.Values(this);	
		return res.getValue();
	}
	
	/**
	 * Invoked after data capability initialized (constructor done), before {@link NFFTamedCommonDataConstructEvent}.
	 * <p>Mainly for creating additional synched data fields.
	 */
	public default void onDataInit(CNFFTamedCommonData dataCap) {}
	
	/**
	 * Get the UUID identifier of this mob. (Not the entity UUID. This is for identifying a mob even if it respawned with a new UUID)
	 */
	@DontOverride
	public default UUID getIdentifier()
	{
		return this.getData().getIdentifier();
	}
	
	/* Behaviors */
	
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
	 * Only when {@link INFFTamed#golemAttitude} is {@link GolemAttitude#CUSTOM}, check if a golem should attack
	 * when it attempts to set target to this mob.
	 */
	public default boolean shouldGolemAttack(AbstractGolem golem)
	{
		return true;
	}
	
	/**
	 * If true, it can attack creepers (mobs under {@link Creeper} class).
	 */
	public default boolean canAttackCreeper()
	{
		return false;
	}
	
	/**
	 * If true, it can attack ghasts (mobs under {@link Ghast} class).
	 */
	public default boolean canAttackGhast()
	{
		return false;
	}
	
	/**
	 * If true, the mob can prevent other player's sleep. It never prevents the owner's sleep.
	 */
	public default boolean canPreventOtherPlayersSleep(ServerPlayer player)
	{
		return false;
	}
}
