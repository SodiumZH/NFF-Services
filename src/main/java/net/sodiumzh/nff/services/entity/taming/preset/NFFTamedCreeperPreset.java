package net.sodiumzh.nff.services.entity.taming.preset;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.ai.goal.NFFGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamedStatics;
import net.sodiumzh.nff.services.inventory.NFFTamedInventoryMenu;

public abstract class NFFTamedCreeperPreset extends Monster implements INFFTamed, PowerableMob
{

	/* Adjusted from vanilla creeper */

	protected static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData
			.defineId(NFFTamedCreeperPreset.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData
			.defineId(NFFTamedCreeperPreset.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData
			.defineId(NFFTamedCreeperPreset.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Integer> DATA_SWELL = SynchedEntityData
			.defineId(NFFTamedCreeperPreset.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_SWELL_LAST_TICK= SynchedEntityData
			.defineId(NFFTamedCreeperPreset.class, EntityDataSerializers.INT);

	public int maxSwell = 30;

	protected int explosionRadius = 3;
	protected int droppedSkulls;
	public boolean canExplode = true;
	public boolean canIgnite = true;
	public boolean shouldDiscardAfterExplosion = false;
	public boolean shouldDestroyBlocks = false;
	public boolean shouldAlwaysDropOnDestroyBlocks = true;
	public int ignitionCooldownTicks = 200;
	protected int currentIgnitionCooldown = 0;
	
	
	public NFFTamedCreeperPreset(EntityType<? extends NFFTamedCreeperPreset> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
		this.xpReward = 0;
		Arrays.fill(this.armorDropChances, 0f);
		Arrays.fill(this.handDropChances, 0f);
		/* Initialization */
	}

	// Swell value related
	public int getSwell()
	{
		return entityData.get(DATA_SWELL);
	}

	public void setSwell(int val)
	{
		entityData.set(DATA_SWELL, val);
	}
	
	protected int getSwellLastTick()
	{
		return entityData.get(DATA_SWELL_LAST_TICK);
	}
	
	protected void setSwellLastTick(int val)
	{
		entityData.set(DATA_SWELL_LAST_TICK, val);
	}
	
	// Update swell from SwellDir
	protected void updateSwell()
	{
		entityData.set(DATA_SWELL_LAST_TICK, getSwell());
		int newSwell = getSwell() + getSwellDir();
		newSwell = Mth.clamp(newSwell, 0, maxSwell);
		setSwell(newSwell);
	}
	
	
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
	}

	@Override
	public int getMaxFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		boolean flag = super.causeFallDamage(pFallDistance, pMultiplier, pSource);
		this.setSwell(getSwell() + (int) (pFallDistance * 1.5F));
		if (this.getSwell() > this.maxSwell - 5)
		{
			this.setSwell(this.maxSwell - 5);
		}

		return flag;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SWELL_DIR, -1);
		this.entityData.define(DATA_IS_POWERED, false);
		this.entityData.define(DATA_IS_IGNITED, false);
		this.entityData.define(DATA_SWELL, 0);
		this.entityData.define(DATA_SWELL_LAST_TICK, 0);
		//this.entityData.define();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.entityData.get(DATA_IS_POWERED))
		{
			tag.putBoolean("powered", true);
		}

		tag.putShort("Fuse", (short) this.maxSwell);
		tag.putByte("ExplosionRadius", (byte) this.explosionRadius);
		tag.putBoolean("ignited", this.isIgnited());
		tag.putInt("current_ignition_cooldown", currentIgnitionCooldown);
		tag.putInt("ignition_cooldown", ignitionCooldownTicks);
		
		NFFTamedStatics.addBefriendedCommonSaveData(this, tag);
	}

	/**
	 * (abstract) Protected helper method to read subclass mob data from NBT.
	 */
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.entityData.set(DATA_IS_POWERED, tag.getBoolean("powered"));
		if (tag.contains("Fuse", 99))
		{
			this.maxSwell = tag.getShort("Fuse");
		}

		if (tag.contains("ExplosionRadius", 99))
		{
			this.explosionRadius = tag.getByte("ExplosionRadius");
		}

		if (tag.getBoolean("ignited"))
		{
			this.setIgnited(true);
		}
		if (tag.contains("ignition_cooldown"))
			this.ignitionCooldownTicks = tag.getInt("ignition_cooldown");
		if (tag.contains("current_ignition_cooldown"))
			this.currentIgnitionCooldown = tag.getInt("current_ignition_cooldown");
		
		NFFTamedStatics.readBefriendedCommonSaveData(this, tag);
		/* Add more save data... */
		this.setInit();
	}

	/**
	 * Called to update the mob's position/logic.
	 */
	@Override
	public void tick() {
		if (this.isAlive())
		{
			if (this.isIgnited())
			{
				this.setSwellDir(1);
			}

			if (getSwellDir() > 0 && this.getSwell() == 0)
			{
				this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
				this.gameEvent(GameEvent.PRIME_FUSE);
			}

			updateSwell();

			if (this.getSwell() == this.maxSwell)
			{
				this.explodeCreeper();
			}
			
			if (currentIgnitionCooldown > 0)
				currentIgnitionCooldown--;
			
		} 
		super.tick();
	}

		
	

	/**
	 * Sets the active target the Goal system uses for tracking
	 */
	@Override
	public void setTarget(@Nullable LivingEntity pTarget) {
		if (!(pTarget instanceof Goat))
		{
			super.setTarget(pTarget);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource pDamageSource) {
		return SoundEvents.CREEPER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		return super.doHurtTarget(target);
	}

	@Override
	public boolean isPowered() {
		return this.entityData.get(DATA_IS_POWERED);
	}

	public void setPowered(boolean powered)
	{
		this.entityData.set(DATA_IS_POWERED, powered);
	}
	
	/**
	 * Params: (Float)Render tick. Returns the intensity of the creeper's flash when
	 * it is ignited.
	 */
	public float getSwelling(float pPartialTicks) {
		return Mth.lerp(pPartialTicks, (float) this.getSwellLastTick(), (float) this.getSwell()) / (float) (this.maxSwell - 2);
	}

	/**
	 * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
	 */
	public int getSwellDir() {
		return this.entityData.get(DATA_SWELL_DIR);
	}

	/**
	 * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
	 */
	 // Use setSwelling() instead
	public void setSwellDir(int val) {
		this.entityData.set(DATA_SWELL_DIR, val);
	}

	@Deprecated // Use getSwellDir instead
	public boolean isSwelling() {
		return this.entityData.get(DATA_SWELL_DIR) > 0;
	}

	@Deprecated // Use setSwellDir instead
	public void setSwelling(boolean swelling) {
		this.entityData.set(DATA_SWELL_DIR, swelling ? 1 : -1);
	}

	@Override
	public void thunderHit(ServerLevel level, LightningBolt lightning) {
		super.thunderHit(level, lightning);
		this.entityData.set(DATA_IS_POWERED, true);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		return super.mobInteract(player, hand);
	}

	/**
	 * Creates an explosion as determined by this creeper's power and explosion
	 * radius.
	 */
	protected void explodeCreeper() {
		if (!this.level.isClientSide)
		{
			Explosion.BlockInteraction explosion$blockinteraction = shouldDestroyBlocks && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)
					? (shouldAlwaysDropOnDestroyBlocks ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.DESTROY) : Explosion.BlockInteraction.NONE;
			float f = this.isPowered() ? 2.0F : 1.0F;

			if (!shouldDiscardAfterExplosion)
			{
				// Set 2 tick invulnerable to avoid being damaged by the explosion
				this.invulnerableTime += 2;
				this.resetExplosionProcess();
			} else
			{
				this.dead = true;
			}

			this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f,
					explosion$blockinteraction);

			if (shouldDiscardAfterExplosion)
				this.discard();

			this.spawnLingeringCloud();
		}
		else
		{
			if (!shouldDiscardAfterExplosion)
				this.resetExplosionProcess();
		}
	}

	protected void resetExplosionProcess()
	{
		this.setIgnited(false);
		this.setSwellDir(-1);
		this.setSwell(0);
		this.setSwellLastTick(0);
		this.currentIgnitionCooldown = 0;
	}
	
	protected void spawnLingeringCloud() {
		Collection<MobEffectInstance> collection = this.getActiveEffects();
		if (!collection.isEmpty())
		{
			AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
			areaeffectcloud.setRadius(2.5F);
			areaeffectcloud.setRadiusOnUse(-0.5F);
			areaeffectcloud.setWaitTime(10);
			areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
			areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

			for (MobEffectInstance mobeffectinstance : collection)
			{
				areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
			}

			this.level.addFreshEntity(areaeffectcloud);
		}

	}

	public boolean isIgnited() {
		return this.entityData.get(DATA_IS_IGNITED);
	}

	@Deprecated // Use setIgnited instead
	public void ignite() {
		setIgnited(true);
	}

	public void setIgnited(boolean ignited) {
		this.entityData.set(DATA_IS_IGNITED, ignited);
	}

	/**
	 * Returns true if an mob is able to drop its skull due to being blown up by
	 * this creeper.
	 * 
	 * Does not test if this creeper is charged" the caller must do that. However,
	 * does test the doMobLoot gamerule.
	 */
	public boolean canDropMobsSkull() {
		return this.isPowered() && this.droppedSkulls < 1;
	}

	public void increaseDroppedSkulls() {
		++this.droppedSkulls;
	}

	/* BefriendedMob setup */

	// Interaction

	public boolean playerIgniteDefault(Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(Items.FLINT_AND_STEEL) && this.canIgnite && this.currentIgnitionCooldown == 0)
		{
			this.level.playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE,
					this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			if (!this.level.isClientSide)
			{
				this.setIgnited(true);
				itemstack.hurtAndBreak(1, player, (p) ->
				{
					p.broadcastBreakEvent(hand);
				});
			}
			return true;
		}
		return false;
	}
	
	public boolean playerIgniteDefault(Player player, InteractionHand hand, Item ignitingItem)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(ignitingItem) && this.canIgnite && this.currentIgnitionCooldown == 0)
		{
			this.level.playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE,
					this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			if (!this.level.isClientSide)
			{
				this.setIgnited(true);
				itemstack.hurtAndBreak(1, player, (p) ->
				{
					p.broadcastBreakEvent(hand);
				});
			}
			return true;
		}
		return false;
	}
	
	public boolean playerIgnite(Player player, InteractionHand hand, Item ignitingItem)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(ignitingItem) && this.canIgnite && this.currentIgnitionCooldown == 0)
		{
			if (!this.level.isClientSide)
			{
				this.setIgnited(true);
			}
			return true;
		}
		return false;
	}

	// Interaction end

	// Inventory related
	// Generally no need to modify unless noted

	@Override
	public void updateFromInventory() {
		if (!this.level.isClientSide) {
			/* If mob's properties (e.g. equipment, HP, etc.) needs to sync with inventory, set here */
		}
	}

	@Override
	public void setInventoryFromMob() {
		if (!this.level.isClientSide) {
			/* If inventory needs to be set from mob's properties on initialization, set here */
		}
	}

	@Override
	public NFFTamedInventoryMenu makeMenu(int containerId, Inventory playerInventory, Container container) {
		return null; /* return new YourMenuClass(containerId, playerInventory, container, this) */
	}

	// Inventory end

	// save&load

	// ==================================================================== //
	// ========================= General Settings ========================= //
	// Generally these can be copy-pasted to other INFFTamed classes //

	// ------------------ INFFTamed interface ------------------ //

	protected boolean initialized = false;

	@Override
	public boolean hasInit()
	{
		return initialized;
	}

	@Override
	public void setInit()
	{
		initialized = true;
	}

	// AI related

	protected LivingEntity PreviousTarget = null;

	@Override
	public LivingEntity getPreviousTarget() {
		return PreviousTarget;
	}

	@Override
	public void setPreviousTarget(LivingEntity target) {
		PreviousTarget = target;
	}

	protected Vec3 anchorPos = new Vec3(0, 0, 0);	// This is not important as we initial it again in init()
	@Override
	public Vec3 getAnchorPos() {return anchorPos;}
	
	@Override
	public void setAnchorPos(Vec3 pos) {anchorPos = new Vec3(pos.x, pos.y, pos.z);}
	
	@Override
	public double getAnchoredStrollRadius()  {return 64.0d;}
	
	/* Inventory */

	// ------------------ INFFTamed interface end ------------------ //

	// ------------------ Misc ------------------ //

	@Override
	public boolean isPersistenceRequired() {
		return true;
	}

	/* add @Override annotation if inheriting Monster class */
	/* @Override */
	@Override
	public boolean isPreventingPlayerRest(Player pPlayer) {
		return false;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	// ========================= General Settings end ========================= //
	// ======================================================================== //

	public static class SwellGoal extends NFFGoal
	{
		// Merged from vanilla creeper swell goal
		protected final NFFTamedCreeperPreset creeper;
		@Nullable
		protected LivingEntity target;
		protected double stopDistance = 7.0d;	// Stop swelling when target is further than this distance
		protected double startDistance = 3.0d;	// Start swelling when target is closer than this distance
		protected boolean targetedSwelling = true;	// 

		public SwellGoal(NFFTamedCreeperPreset creeper)
		{
			super(creeper);
			this.creeper = creeper;
			this.mob = creeper;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.allowAllStates();
		}

		public SwellGoal targetless()
		{
			targetedSwelling = false;
			return this;
		}
		
		public SwellGoal startDistance(double distance)
		{
			this.startDistance = distance;
			return this;
		}
		
		public SwellGoal stopDistance(double distance)
		{
			this.stopDistance = distance;
			return this;
		}
		
		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		@Override
		public boolean checkCanUse() {
			if (isDisabled())
				return false;
			else if (this.creeper.getSwell() == 0)
				return false;
			//else if ()
			return this.creeper.getSwellDir() > 0
					|| this.targetedSwelling && this.creeper.getTarget() != null && this.creeper.distanceToSqr(this.creeper.getTarget()) < startDistance * startDistance;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void onStart() {
			//this.creeper.getNavigation().stop();
			this.target = this.creeper.getTarget();
			if (this.creeper.getSwell() == 0)
			{
				this.creeper.setSwellDir(1);			
			}
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		@Override
		public void onStop() {
		//	this.target = null;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void onTick() {
			if (targetedSwelling)
			{
				if (this.target == null)
					this.creeper.setSwellDir(-1);
				else if (this.creeper.distanceToSqr(this.target) > stopDistance * stopDistance)	
					this.creeper.setSwellDir(-1);
				else if (!this.creeper.getSensing().hasLineOfSight(this.target))
					this.creeper.setSwellDir(-1);
				
				//if (this.creeper.)
			}
			else
				this.creeper.setSwellDir(1);
		}
	}
	
}
