package net.sodiumstudio.befriendmobs.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
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
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedChangeAiStateEvent;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendedMobTempData;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImpl;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerImplDefault;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.registry.BefMobCapabilities;
import net.sodiumstudio.befriendmobs.util.Wrapped;
import net.sodiumstudio.befriendmobs.util.annotation.DontOverride;
import net.sodiumstudio.befriendmobs.util.annotation.NoManualCall;

public interface IBefriendedMob extends ContainerListener  {

	/* Initialization */
	
	/** Initialize a mob.
	 * On reading from NBT, the befriendedFrom mob is null, so implementation must handle null cases.
	 * @param player Player who owns this mob.
	 * @param from The source mob from which this mob was befriended or converted. NULLABLE!
	 */
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
	}

	/**
	 * Get whether this mob has finished initialization.
	 * After finishing initialization the mob will start updating from its inventory.
	 */
	@DontOverride
	public default boolean hasInit()
	{
		return this.getTempData().hasInit;
	}
	
	/** Label a mob as finished initialization after reading nbt, copying from other, etc.
	 * Only after labeled init, the mob will update from inventory.
	 * After spawning and deserializing, call this.
	 * Don't worry about if the presets in BefriendMobs API has already labeled init, 
	 * as labeling again will not do anything if so.
	 */
	@DontOverride
	public default void setInit()
	{
		this.getTempData().hasInit = true;
	}

	/** Label a mob not finished initialization.
	 * Call this only when the presets has labeled init but you need some extra actions that needs to keep it not init.
	 * Currently the init label affects only inventory updating.
	 */
	@DontOverride
	public default void setNotInit()
	{
		this.getTempData().hasInit = false;
	}
	
	/* Ownership */
	
	/** Get owner as player entity.
	* Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	*/
	@DontOverride
	public default Player getOwner() 
	{
		if (getOwnerUUID() != null)
			return asMob().level.getPlayerByUUID(getOwnerUUID());
		else return null;
	}
	// Get owner as UUID.
	// Warning: be careful calling this on initialization! If the owner hasn't been initialized it will return null.
	@DontOverride
	public default UUID getOwnerUUID()
	{
		return asMob().getEntityData().get(getOwnerUUIDAccessor()).get();
	}
	
	// Set owner from player mob.
	@DontOverride
	public default void setOwner(@Nonnull Player owner)
	{
		setOwnerUUID(owner.getUUID());
	}
	
	// Set owner from player UUID.
	@DontOverride
	public default void setOwnerUUID(@Nonnull UUID ownerUUID)
	{
		asMob().getEntityData().set(getOwnerUUIDAccessor(), Optional.of(ownerUUID));
	}
	
	/**
	 * Get owner UUID as entity data accessor. Attach this to accessor defined in mob class.
	 */
	public EntityDataAccessor<Optional<UUID>> getOwnerUUIDAccessor();
	
	/* -------------------------------------------------------- */
	/* AI configs */
	
	// Get the AI state as EntityDataAccessor defined in mob classes.
	public EntityDataAccessor<Byte> getAIStateData();
	
	// Get current AI state as enum.
	@DontOverride
	public default BefriendedAIState getAIState()
	{
		return BefriendedAIState.fromID(asMob().getEntityData().get(getAIStateData()));
	}
	
	/** A preset action when switching AI e.g. on right click.
	 * By default it cycles among Wait, Follow and Wander.
	 * DO NOT override this. Override getNextAIState() instead.
	 * @return The new AI state.
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
	 * Called in switchAIState() above.
	 */
	public default BefriendedAIState getNextAIState()
	{
		return getAIState().defaultSwitch();
	}
	
	@DontOverride
	public default void setAIState(BefriendedAIState state, boolean postEvent)
	{
		if (state == getAIState())
			return;
		if (postEvent && MinecraftForge.EVENT_BUS.post(new BefriendedChangeAiStateEvent(this, getAIState(), state)))
			return;
		asMob().getEntityData().set(getAIStateData(), state.id());
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
	* TODO: move the entity reference into a capability so you don't need to manually override this in mob class
	*/
	@NoManualCall
	public default LivingEntity getPreviousTarget()
	{
		return this.getTempData().previousTarget;
	}
	
	/** Get the previous target after updating target.
	* This function is only called on setting target. DO NOT CALL ANYWHERE ELSE!
	*/
	@NoManualCall
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
	@NoManualCall
	@DontOverride
	public default void updateAnchor()
	{
		if (getAnchorPos() != null)
			setAnchorPos(asMob().position());
	}
	
	/* --------------------------------------------- */
	/* Interaction */
	
	// Actions on player right click the mob
	// Deprecated, use mobInteraction() instead
	@Deprecated
	public default boolean onInteraction(Player player, InteractionHand hand)
	{
		return false;
	}
	
	// Actions on player shift + rightmouse click
	// Deprecated, use mobInteraction() instead
	@Deprecated
	public default boolean onInteractionShift(Player player, InteractionHand hand)
	{
		return false;
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
	// DO NOT override this. Override onInventoryChanged instead.
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
		this.asMob().getCapability(BefMobCapabilities.CAP_HEALING_HANDLER).ifPresent((l) ->
		{
			succeeded.set(l.applyHealingItem(stack, value, consume));
		});		
		return succeeded.get();
	}
	
	/* Add all usable items here, including non-consuming items. Value is HP it can heal. */
	public default HashMap<Item, Float> getHealingItems()
	{
		return new HashMap<Item, Float>();
	}
	
	// Specify which items in the map above don't consume after usage
	public default HashSet<Item> getNonconsumingHealingItems()
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
	 * If true, the mob will drop a respawner on death.
	 */
	public default boolean shouldDropRespawner()
	{
		return true;
	}
	
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
	
	public default CBefriendedMobTempData.Values getTempData()
	{
		Wrapped<CBefriendedMobTempData> res = new Wrapped<CBefriendedMobTempData>(null);
		asMob().getCapability(BefMobCapabilities.CAP_BEFRIENDED_MOB_TEMP_DATA).ifPresent((cap) ->
		{
			res.set(cap);
		});
		if (res.get() == null)
			throw new IllegalStateException("Befriended mob " + asMob().getName().getString() + "missing temp data capability.");
		return res.get().values();
	}
	
}
