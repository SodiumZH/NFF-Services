package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.sodiumzh.nfu.entity.IMobSpecific;
import net.sodiumzh.nfu.object.FilteredMapper;
import net.sodiumzh.nfu.registry.NFUCapabilities;
import net.sodiumzh.nfu.util.NFUContainerStatics;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUNBTStatics;
import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nfu.annotation.DontCallManually;
import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nfu.container.CyclicSwitch;
import net.sodiumzh.nfu.entity.MobApplicableItemTable;
import net.sodiumzh.nfu.object.FilteredMapper;
import net.sodiumzh.nfu.registry.NFUCapabilities;
import net.sodiumzh.nfu.util.NFUContainerStatics;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUNBTStatics;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.capability.CHealingHandlerImpl;
import net.sodiumzh.nff.services.entity.capability.CHealingHandlerImplDefault;
import net.sodiumzh.nff.services.event.entity.NFFTamedCommonDataConstructEvent;
import net.sodiumzh.nff.services.event.entity.ai.NFFTamedChangeAiStateEvent;
import net.sodiumzh.nff.services.eventlistener.NFFEntityEventListeners;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.item.NFFMobRespawnerItem;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface INFFTamed extends ContainerListener, OwnableEntity {

	public static final CyclicSwitch<NFFTamedMobAIState> DEFAULT_AI_SWITCH = new CyclicSwitch<>
		(NFFTamedMobAIState.WAIT, NFFTamedMobAIState.FOLLOW, NFFTamedMobAIState.WANDER);

	/**
	 * Mapper to test if a mob is {@code INFFTamed}, and cast it to {@code INFFTamed}.
	 */
	public static final FilteredMapper<Object, INFFTamed> IS_TAMED_MAPPER =
			FilteredMapper.unconditionalNoVararg(Object.class, INFFTamed.class, obj -> {
				if (obj instanceof INFFTamed tamed) return tamed;
				else return null;
	});

	public static Optional<INFFTamed> get(Object o) {
		return IS_TAMED_MAPPER.apply(o);
	}

	/* Common */
	/**
	 * Check if an object has an {@code NFFTamed} interface.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this instead of {@code instanceof} check.
	 */
	@Deprecated
	public static boolean isTamed(Object o)
	{
		return IS_TAMED_MAPPER.apply(o).isPresent();
	}
	
	/**
	 * Cast an object to the {@code NFFTamed} interface. Null if failed.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this to cast a mob to NFF Tamed.
	 */
	@Deprecated
	@Nullable
	public static INFFTamed asTamed(Object o)
	{
		return IS_TAMED_MAPPER.apply(o).orElse(null);
	}
	
	/**
	 * Do an action if an object has {@code NFFTamed} interface.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * you can use this to safely cast and do things to BM.
	 * @return Whether the action is invoked.
	 */
	@Deprecated
	public static boolean ifTamed(Object o, Consumer<INFFTamed> action)
	{
		return IS_TAMED_MAPPER.apply(o).filter(tamed -> {action.accept(tamed); return true;}).isPresent();
	}
	
	/**
	 * Check if a mob has {@code NFFTamed} interface and satisfied the given condition.
	 * <p>
	 * As INFFTamed could also be implemented in capabilities instead of the mob class in the future,
	 * always use this instead of {@code instanceof} check and followed checks of the cast BM.
	 */
	@Deprecated
	public static boolean isTamedAnd(Object o, Predicate<INFFTamed> cond)
	{
		return get(o).filter(cond).isPresent();
	}	
	
	/* Initialization */
	
	/** Initialize a mob.
	 * On reading from NBT, the befriendedFrom mob is null, so implementation must handle null cases.
	 * @param playerUUID Player UUID who owns this mob.
	 * @param from The source mob from which this mob was befriended or converted. NULLABLE!
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
	 */
	@DontCallManually
	public default void onInit(@Nonnull UUID playerUUID, @Nullable Mob from) {}

	/**
	 * Get whether this mob has finished initialization.
	 * <p>After finishing initialization the mob will start updating from its inventory.
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
	*/
	@Override
	@DontOverride
	@Nullable
	public default UUID getOwnerUUID()
	{
		return this.getData().getOwnerUUID();
	}
	
	/** Set owner from player entity.
	 */
	@DontOverride
	public default void setOwner(@Nonnull Player owner)
	{
		setOwnerUUID(owner.getUUID());
	}
	
	/**
	* Set owner from player UUID.
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
	 */
	@DontOverride
	@Deprecated
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

	public default boolean isOwnedBy(Entity test) {
		return this.getOwnerUUID() != null && test != null && test.getUUID().equals(this.getOwnerUUID());
	}

	public default boolean isOwnedBy(UUID test) {
		return this.getOwnerUUID() != null && test != null && Objects.equals(this.getOwnerUUID(), test);
	}

	@Deprecated
	public default boolean isOwner(Entity test) {
		return isOwnedBy(test);
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
		return 16.0d;
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
	 * Check if another mob should be accounted as ally of self, and should not attack each other.
	 */
	public default boolean isAllyTo(LivingEntity other) {
		return NFFTamedStatics.isLivingAlliedToOwnableUnsafe(this, other);
	}

	/**
	 * Check if another mob should be accounted as ally of the tamed mob, and should not attack each other.
	 */
	public static boolean isAlly(INFFTamed tamed, LivingEntity other) {
		return tamed.isAllyTo(other);
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
	 * @deprecated Use {@code getAdditionalInventory().syncToMob(this.asMob())}.
	 */
	@Deprecated
	@DontOverride
	public default void updateFromInventory()
	{
		this.getAdditionalInventory().syncToMob(this.asMob());
	}
	
	/** Set befriendedInventory from mob data, usually for initializing
	 * <p><u>DO NOT override this.</u> Create subclasses of {@link NFFTamedMobInventory} and override {@link NFFTamedMobInventory#getFromMob} instead.
	 * @deprecated Use {@code getAdditionalInventory().getFromMob(this.asMob())}.
	 */
	@DontOverride
	@Deprecated
	public default void setInventoryFromMob()
	{
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
	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume, int cooldown, Player player)
	{
		MutableObject<Boolean> succeeded = new MutableObject<>(false);		
		this.asMob().getCapability(NFFCapRegistry.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			succeeded.setValue(l.applyHealingItem(stack, value, consume, cooldown, player));
		});		
		return succeeded.getValue();
	}
	
	/** Add all usable items here, including non-consuming items. Value is HP it can heal. */
	@Nullable
	public default MobApplicableItemTable getHealingItems()
	{
		return null;
	}

	@DontOverride
	public default InteractionResult tryApplyHealingItems(ItemStack stack, Player player)
	{
		if (stack.isEmpty())
			return InteractionResult.PASS;
		MobApplicableItemTable table = getHealingItems();
		if (table == null) 
		{
			return InteractionResult.PASS;
		}
		MobApplicableItemTable.Outcome output = table.getOutcome(this.asMob(), stack).orElse(null);
		if (output != null)
		{
			return applyHealingItem(stack, (float)(output.amount()), !output.noConsume(), output.cooldown(), player) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
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
	@Deprecated
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
	 * Get the UUID identifier of this mob. (Not the entity UUID. This is for identifying a mob even if it respawned with a new UUID).
	 * Returns empty uuid (0, 0) if the data cap is lost (may occasionally happen).
	 */
	@DontOverride
	@Nonnull
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
		 * Custom, defined in {@link INFFTamed#shouldGolemAttack}.
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

	// Static

	/**
	 * Common initialization when a new tamed mob is created but not loaded from NBT, either from taming or other ways.
	 * @param player owner.
	 * @param from The "wild" mob from which this mob is tamed. Null if it's not created by taming.
	 */
	public default void commonInit(@Nonnull Player player, @Nullable Mob from)
	{
		this.setOwner(player);
		this.getData().setOwnerName(player.getName().getString());
		this.init(player.getUUID(), from);
		this.setInventoryFromMob();
		this.getData().generateIdentifier();
		this.getData().recordEntityType();
		this.getData().recordEncounteredDate();
	}

	// ===== Mob Search ===

	/**
	 *  Only on server, record the current location to the owner's data.
	 *  Called in {@link NFFEntityEventListeners#onLivingUpdate}.
	 */
	public default void recordLocationToOwner() {
		Player player = this.getOwnerInWorld();
		if (player == null) return;
		player.getCapability(NFUCaps.CAP_ENTITY_DATA).ifPresent(c -> {
			if (!c.getNBT().contains("tamedMobLocations", Tag.TAG_COMPOUND))
				c.getNBT().put("tamedMobLocations", new CompoundTag());
			MobLocationInfo info = MobLocationInfo.fromMob(this);
			c.getNBT().getCompound("tamedMobLocations").put(info.identifier().toString(), info.save());
		});
	}

	/**
	 * Remove the location when the entity is removed.
	 */
	public default void removeLocationOnOwner() {
		Player player = this.getOwnerInWorld();
		if (player == null) return;
		player.getCapability(NFUCaps.CAP_ENTITY_DATA).ifPresent(c -> {
			c.getNBT().getCompound("tamedMobLocations").remove(this.getIdentifier().toString());
		});
	}

	/** Only on server, get all NFF mob's locations. The keys are Tamed Identifiers, not mob uuid!! */
	public static Map<UUID, MobLocationInfo> getAllMobLocations(Player player) {
		if (!(player.level instanceof ServerLevel sl)) return new HashMap<>();
		AtomicReference<Map<UUID, Optional<MobLocationInfo>>> res =
				new AtomicReference<>(new HashMap<>());
		player.getCapability(NFUCaps.CAP_ENTITY_DATA).ifPresent(c -> {
			if (!c.getNBT().contains("tamedMobLocations", Tag.TAG_COMPOUND)) return;
			res.set(NFUNBTStatics.mapFromCompoundTag(c.getNBT().getCompound("tamedMobLocations"),
					UUID::fromString, tag -> Optional.ofNullable(MobLocationInfo.load((CompoundTag) tag, sl))));
		});
		return NFUContainerStatics.iterableToMap(res.get().values().stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(MobLocationInfo::isValid)
				.toList(),
				MobLocationInfo::identifier, info -> info);
	}

	/**
	 * Find tamed mob by its tamed mob identifier (not entity uuid). Will search in all loaded dimensions.
	 * @param identifier Tamed mob identifier. (Not the entity UUID!)
	 * @param context Any server level that can provide a context to the server.
	 * @return Find result.
	 */
	public static Optional<Mob> byIdentifier(UUID identifier, ServerLevel context) {
		for (ServerLevel sl: context.getServer().getAllLevels()) {
			var list = sl.getEntities(EntityTypeTest.forClass(Mob.class), mob ->
					INFFTamed.get(mob).filter(m -> m.getIdentifier().equals(identifier)).isPresent());
			if (!list.isEmpty()) return Optional.of(list.get(0));
		}
		return Optional.empty();
	}

	/**
	 * Remove suspicious tamed location stored in player that the mob may no longer exist。
	 * A suspicious location entry is defined as the entry in which the pos is loaded
	 * but the mob isn't found in level。
	 * */
	public static void removeSuspiciousMobLocations(Player player) {
		if (!(player.level instanceof ServerLevel sl)) return;
		player.getCapability(NFUCaps.CAP_ENTITY_DATA).ifPresent(c -> {
			List<UUID> levelLoadedIdentifiers = NFUEntityStatics.getEntitiesOnServer(sl, EntityTypeTest.forClass(Mob.class),
							e -> INFFTamed.get(e).filter(tamed -> Objects.equals(tamed.getOwner(), player)).isPresent())
					.stream().map(e -> INFFTamed.get(e).orElse(null)).filter(Objects::nonNull)
					.map(INFFTamed::getIdentifier).toList();
			List<INFFTamed.MobLocationInfo> savedLocations =
					c.getNBT().getCompound("tamedMobLocations").getAllKeys()
							.stream().map(k -> INFFTamed.MobLocationInfo.load(c.getNBT().getCompound("tamedMobLocations").getCompound(k), sl))
							.filter(Objects::nonNull).toList();
			List<UUID> suspiciousIdentifiers = new ArrayList<>();
			for (INFFTamed.MobLocationInfo loc: savedLocations) {
				ServerLevel dim = sl.getServer().getLevel(loc.dimension());
				if (dim == null || dim.isLoaded(loc.pos()) && !levelLoadedIdentifiers.contains(loc.identifier()))
					suspiciousIdentifiers.add(loc.identifier());
			}
			suspiciousIdentifiers.forEach(id -> c.getNBT().getCompound("tamedMobLocations").remove(id.toString()));
		});
	}

	public static record MobLocationInfo(UUID identifier, Component mobName, ResourceKey<Level> dimension, BlockPos pos) {

		public static MobLocationInfo fromMob(INFFTamed mob) {
			return new MobLocationInfo(mob.getIdentifier(), mob.asMob().getName(),
					mob.asMob().level.dimension(), mob.asMob().getOnPos());
		}

		public CompoundTag save() {
			CompoundTag nbt = new CompoundTag();
			nbt.putUUID("identifier", this.identifier());
			nbt.putString("name", Component.Serializer.toJson(mobName()));
			nbt.putString("dimension", dimension().location().toString());
			nbt.putIntArray("pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			return nbt;
		}

		@Nullable
		public static MobLocationInfo load(CompoundTag nbt, ServerLevel context) {

			if (!nbt.hasUUID("identifier")) return null;
			UUID id = nbt.getUUID("identifier");
			if (id.equals(new UUID(0L, 0L))) return null;

			Component name = Component.Serializer.fromJson(nbt.getString("name"));

			ResourceLocation dimKey = new ResourceLocation(nbt.getString("dimension"));
			List<ResourceKey<Level>> dim = context.getServer().levelKeys().stream()
					.filter(key -> key.location().equals(dimKey)).toList();
			if (dim.isEmpty()) return null;

			int[] posArray = nbt.getIntArray("pos");

			return new MobLocationInfo(id, name, dim.get(0), new BlockPos(posArray[0], posArray[1], posArray[2]));
		}

		public boolean isValid() {
			return !Objects.equals(identifier(), new UUID(0L, 0L))
					&& mobName() != null && dimension() != null && pos() != null;
		}
	}

}
