package net.sodiumzh.nff.services.entity.taming.presets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sodiumzh.nff.services.entity.ai.goal.NFFGoal;
import net.sodiumzh.nff.services.entity.ai.goal.presets.NFFMeleeAttackGoal;
import net.sodiumzh.nff.services.entity.ai.goal.presets.NFFWaterAvoidingRandomStrollGoal;
import net.sodiumzh.nff.services.entity.ai.goal.presets.target.NFFHurtByTargetGoal;
import net.sodiumzh.nff.services.entity.ai.goal.presets.target.NFFNearestAttackableTargetGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.NFFTamedStatics;

public abstract class NFFTamedEnderManPreset extends Monster implements INFFTamed, NeutralMob
{

	protected static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	protected static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(
			SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double) 0.15F,
			AttributeModifier.Operation.ADDITION);
	protected static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
	protected static final int MIN_DEAGGRESSION_TIME = 600;
	protected int lastStareSound = Integer.MIN_VALUE;
	protected int targetChangeTime;
	protected static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	protected int remainingPersistentAngerTime;
	@Nullable
	protected UUID persistentAngerTarget;
	public boolean canAutoTakeBlocks = false;
	public boolean canAutoPlaceBlocks = false;
	public boolean angryOnLookedAt = false;
	public boolean teleportOnHurtByWater = true;
	public boolean teleportNotOnHurtByWater = false;
	public boolean teleportToAvoidProjectile = true;
	protected String modId;
	
	protected static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData
			.defineId(NFFTamedEnderManPreset.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
	protected static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData
			.defineId(NFFTamedEnderManPreset.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData
			.defineId(NFFTamedEnderManPreset.class, EntityDataSerializers.BOOLEAN);

	@SuppressWarnings("deprecation")
	public NFFTamedEnderManPreset(EntityType<? extends NFFTamedEnderManPreset> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
		//this.maxUpStep = 1.0F;
		this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
		this.xpReward = 0;
		Arrays.fill(this.armorDropChances, 0f);
		Arrays.fill(this.handDropChances, 0f);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		//this.goalSelector.addGoal(1, new BefriendedEnderManGoals.FreezeWhenLookedAt(this));
		this.goalSelector.addGoal(2, new NFFMeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(7, new NFFWaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(10, new NFFTamedEnderManPreset.LeaveBlockGoal(this));
		this.goalSelector.addGoal(11, new NFFTamedEnderManPreset.TakeBlockGoal(this));
		// this.targetSelector.addGoal(1, new
		// BefriendedEnderManGoals.LookForPlayerGoal(this, this::isAngryAt));
		this.targetSelector.addGoal(2, new NFFHurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NFFNearestAttackableTargetGoal<>(this, Endermite.class, true, false));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	/**
	 * Sets the active target the Goal system uses for tracking
	 */
	@Override
	public void setTarget(@Nullable LivingEntity pLivingEntity) {
		AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
		if (pLivingEntity == null)
		{
			this.targetChangeTime = 0;
			this.entityData.set(DATA_CREEPY, false);
			this.entityData.set(DATA_STARED_AT, false);
			attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
		} else
		{
			this.targetChangeTime = this.tickCount;
			this.entityData.set(DATA_CREEPY, true);
			if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING))
			{
				attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
			}
		}

		super.setTarget(pLivingEntity); // Forge: Moved down to allow event handlers to write data manager values.
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_CARRY_STATE, Optional.empty());
		this.entityData.define(DATA_CREEPY, false);
		this.entityData.define(DATA_STARED_AT, false);
	}

	@Override
	public void startPersistentAngerTimer() {
		/*this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));*/
	}

	@Override
	public void setRemainingPersistentAngerTime(int pTime) {
		/*this.remainingPersistentAngerTime = pTime;*/
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return 0;/*return this.remainingPersistentAngerTime;*/
	}

	@Override
	public void setPersistentAngerTarget(@Nullable UUID pTarget) {
		/*this.persistentAngerTarget = pTarget;*/
	}

	@Override
	@Nullable
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	public void playStareSound() {
		if (this.tickCount >= this.lastStareSound + 400)
		{
			this.lastStareSound = this.tickCount;
			if (!this.isSilent())
			{
				this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE,
						this.getSoundSource(), 2.5F, 1.0F, false);
			}
		}

	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
		if (DATA_CREEPY.equals(pKey) && this.hasBeenStaredAt() && this.level().isClientSide)
		{
			this.playStareSound();
		}

		super.onSyncedDataUpdated(pKey);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		BlockState blockstate = this.getCarriedBlock();
		if (blockstate != null)
		{
			tag.put("carriedBlockState", NbtUtils.writeBlockState(blockstate));
		}

		this.addPersistentAngerSaveData(tag);
		NFFTamedStatics.addBefriendedCommonSaveData(this, tag);
	}

	/**
	 * (abstract) Protected helper method to read subclass mob data from NBT.
	 */
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		BlockState blockstate = null;
		if (tag.contains("carriedBlockState", 10))
		{
			blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("carriedBlockState"));
			if (blockstate.isAir())
			{
				blockstate = null;
			}
		}

		this.setCarriedBlock(blockstate);
		this.readPersistentAngerSaveData(this.level(), tag);
		NFFTamedStatics.readBefriendedCommonSaveData(this, tag);
		/* Add more save data... */
		this.setInit();
	}

	public HashSet<Item> getMaskTypes()
	{
		HashSet<Item> set = new HashSet<Item>();
		set.add(Items.CARVED_PUMPKIN);
		return set;
	}
	
	public boolean isMask(ItemStack stack, Player player) 
	{
		return getMaskTypes().contains(stack.getItem());
	}

	/**
	 * Checks to see if this NFFTamedEnderManPreset should be attacking this
	 * player
	 *//*
	public boolean isLookingAtMe(Player player) {
		ItemStack helmet = player.getInventory().armor.get(3);
		if (!this.angryOnLookedAt
				|| isMask(helmet, player) 				 
				|| this.getOwnerUUID() != null && this.getOwnerUUID().equals(player.getUUID())
				|| MinecraftForge.EVENT_BUS.post(new AngerEvent(this, player)))
		{
			return false;
		} else
		{
			Vec3 vec3 = player.getViewVector(1.0F).normalize();
			Vec3 vec31 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(),
					this.getZ() - player.getZ());
			double d0 = vec31.length();
			vec31 = vec31.normalize();
			double d1 = vec3.dot(vec31);
			return d1 > 1.0D - 0.025D / d0 ? player.hasLineOfSight(this) : false;
		}
	}
*/
	@Override
	protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
		return 2.55F;
	}

	/**
	 * Called every tick so the mob can update its state as required. For
	 * example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	@SuppressWarnings("resource")
	@Override
	public void aiStep() {
		if (this.level().isClientSide)
		{
			for (int i = 0; i < 2; ++i)
			{
				this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D,
						this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
						(this.random.nextDouble() - 0.5D) * 2.0D);
			}
		}

		this.jumping = false;
		if (!this.level().isClientSide)
		{
			this.updatePersistentAnger((ServerLevel) this.level(), true);
		}

		super.aiStep();
	}

	@Override
	public boolean isSensitiveToWater() {
		return true;
	}

	@Override
	protected void customServerAiStep() {
/*
		if (this.level().isDay() && this.tickCount >= this.targetChangeTime + 600)
		{
			float f = this.getBrightness();
			if (f > 0.5F && this.level().canSeeSky(this.blockPosition())
					&& this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F)
			{
				this.setTarget((LivingEntity) null);
				this.teleport();
			}
		}
*/
		super.customServerAiStep();
	}

	public boolean tryTeleportOnWaterHurt(int tryTimes)
	{
		if (!this.teleportOnHurtByWater)
			return false;
		for (int i = 0; i < tryTimes; ++i)
		{
			if (this.teleport())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean tryTeleportToAvoidProjectile(int tryTimes)
	{
		if (!teleportToAvoidProjectile)
			return false;
		for (int i = 0; i < tryTimes; ++i)
		{
			if (this.teleport())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean tryTeleportInOtherCases(int tryTimes)
	{
		if (!this.teleportNotOnHurtByWater)
			return false;
		for (int i = 0; i < tryTimes; ++i)
		{
			if (this.teleport())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean teleport() {
		if (!this.level().isClientSide() && this.isAlive())
		{
			double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
			double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
			double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
			return this.teleport(d0, d1, d2);
		} else
		{
			return false;
		}
	}

	public boolean teleportTowards(Entity pTarget) {
		Vec3 vec3 = new Vec3(this.getX() - pTarget.getX(), this.getY(0.5D) - pTarget.getEyeY(),
				this.getZ() - pTarget.getZ());
		vec3 = vec3.normalize();
		double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
		double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3.y * 16.0D;
		double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
		return this.teleport(d1, d2, d3);
	}

	public boolean teleport(double pX, double pY, double pZ) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(pX, pY, pZ);

		while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight()
				&& !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion())
		{
			blockpos$mutableblockpos.move(Direction.DOWN);
		}

		BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
		boolean flag = blockstate.blocksMotion();
		boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1)
		{
			net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
					.onEnderTeleport(this, pX, pY, pZ);
			if (event.isCanceled())
				return false;
			boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
			if (flag2 && !this.isSilent())
			{
				this.level().playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT,
						this.getSoundSource(), 1.0F, 1.0F);
				this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			}

			return flag2;
		} else
		{
			return false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource pDamageSource) {
		return SoundEvents.ENDERMAN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDERMAN_DEATH;
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
		super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
		BlockState blockstate = this.getCarriedBlock();
		if (blockstate != null)
		{
			this.spawnAtLocation(blockstate.getBlock());
		}

	}

	public void setCarriedBlock(@Nullable BlockState pState) {
		this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(pState));
	}

	@Nullable
	public BlockState getCarriedBlock() {
		return this.entityData.get(DATA_CARRY_STATE).orElse((BlockState) null);
	}

	/**
	 * Called when the mob is attacked.
	 */
	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (this.isInvulnerableTo(pSource))
		{
			return false;
		} 
		else if (pSource.is(DamageTypes.DROWN))
		{
			return this.tryTeleportOnWaterHurt(64);
		}
		
		else if (pSource.isIndirect())
		{
			Entity entity = pSource.getDirectEntity();
			boolean isByWater;
			if (entity instanceof ThrownPotion)
			{
				isByWater = this.hurtWithCleanWater(pSource, (ThrownPotion) entity, pAmount);
			} else
			{
				isByWater = false;
			}
			
			if (isByWater)	// Hurt by water bottle
			{
				this.tryTeleportOnWaterHurt(64);
				return true;
			}
			else	// Hurt by projectile
			{
				return this.tryTeleportToAvoidProjectile(64);
			}
		}
		else
		{
			boolean flag = super.hurt(pSource, pAmount);
			if (!this.level().isClientSide() && !(pSource.getEntity() instanceof LivingEntity)
					&& this.random.nextInt(10) != 0)
			{
				this.tryTeleportInOtherCases(1);
			}
			return flag;
		}
	}

	protected boolean hurtWithCleanWater(DamageSource pSource, ThrownPotion pPotion, float pAmount) {
		if (!this.isSensitiveToWater())
			return false;
		ItemStack itemstack = pPotion.getItem();
		Potion potion = PotionUtils.getPotion(itemstack);
		List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
		boolean flag = potion == Potions.WATER && list.isEmpty();
		return flag ? super.hurt(pSource, pAmount) : false;
	}

	public boolean isCreepy() {
		return this.entityData.get(DATA_CREEPY);
	}

	public boolean hasBeenStaredAt() {
		return this.entityData.get(DATA_STARED_AT);
	}

	public void setBeingStaredAt() {
		this.entityData.set(DATA_STARED_AT, true);
	}

	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
	}

	/* INFFTamed interface */

	// Initialization

	// Initialization end

	// Interaction

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (!player.isShiftKeyDown())
		{
			if (player.getUUID().equals(getOwnerUUID()))
			{
				if (!player.level().isClientSide())
				{
					switchAIState();
				}
				return InteractionResult.sidedSuccess(player.level().isClientSide());
			}
			/* Other actions */
			return InteractionResult.PASS;
		}
		else
		{
			if (player.getUUID().equals(getOwnerUUID()))
			{

				if (hand.equals(InteractionHand.MAIN_HAND))
					NFFTamedStatics.openBefriendedInventory(player, this);
				return InteractionResult.sidedSuccess(player.level().isClientSide());
			}
			/* Other actions... */
			return InteractionResult.PASS;
		}
	}

	// Interaction end

	// Inventory related
	// Generally no need to modify unless noted

	@Override
	public void updateFromInventory() {
		if (!this.level().isClientSide)
		{
			/*
			 * If mob's properties (e.g. equipment, HP, etc.) needs to sync with inventory,
			 * set here
			 */
		}
	}

	@Override
	public void setInventoryFromMob() {
		if (!this.level().isClientSide)
		{
			/*
			 * If inventory needs to be set from mob's properties on initialization, set
			 * here
			 */
		}
	}


	// Inventory end
	public static class LeaveBlockGoal extends NFFGoal
	{
		protected final NFFTamedEnderManPreset enderman;

		public LeaveBlockGoal(NFFTamedEnderManPreset enderman)
		{
			super(enderman);
			this.mob = enderman;
			this.enderman = enderman;
			this.allowState(WANDER);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		@Override
		public boolean checkCanUse() {
			if (isDisabled())
				return false;
			if (!enderman.canAutoPlaceBlocks)
				return false;
			if (this.enderman.getCarriedBlock() == null)
			{
				return false;
			} else if (!net.minecraftforge.event.ForgeEventFactory
					.getMobGriefingEvent(this.enderman.level(), this.enderman))
			{
				return false;
			} else
			{
				return this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0;
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void onTick() {
			RandomSource random = this.enderman.getRandom();
			Level level = this.enderman.level();
			int i = Mth.floor(this.enderman.getX() - 1.0D + random.nextDouble() * 2.0D);
			int j = Mth.floor(this.enderman.getY() + random.nextDouble() * 2.0D);
			int k = Mth.floor(this.enderman.getZ() - 1.0D + random.nextDouble() * 2.0D);
			BlockPos blockpos = new BlockPos(i, j, k);
			BlockState blockstate = level.getBlockState(blockpos);
			BlockPos blockpos1 = blockpos.below();
			BlockState blockstate1 = level.getBlockState(blockpos1);
			BlockState blockstate2 = this.enderman.getCarriedBlock();
			if (blockstate2 != null)
			{
				blockstate2 = Block.updateFromNeighbourShapes(blockstate2, this.enderman.level(),
						blockpos);
				if (this.canPlaceBlock(level, blockpos, blockstate2, blockstate, blockstate1, blockpos1)
						&& !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(enderman,
								net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level,
										blockpos1),
								net.minecraft.core.Direction.UP))
				{
					level.setBlock(blockpos, blockstate2, 3);
					level.gameEvent(this.enderman, GameEvent.BLOCK_PLACE, blockpos);
					this.enderman.setCarriedBlock((BlockState) null);
				}

			}
		}

		protected boolean canPlaceBlock(Level pLevel, BlockPos pDestinationPos, BlockState pCarriedState,
				BlockState pDestinationState, BlockState pBelowDestinationState, BlockPos pBelowDestinationPos) {
			return pDestinationState.isAir() && !pBelowDestinationState.isAir()
					&& !pBelowDestinationState.is(Blocks.BEDROCK)
					&& !pBelowDestinationState.is(net.minecraftforge.common.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST)
					&& pBelowDestinationState.isCollisionShapeFullBlock(pLevel, pBelowDestinationPos)
					&& pCarriedState.canSurvive(pLevel, pDestinationPos)
					&& pLevel.getEntities(this.enderman,
							AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pDestinationPos))).isEmpty();
		}
	}
/*
	public static class LookForPlayerGoal<T extends LivingEntity> 
		extends NearestAttackableTargetGoal<T>
	{
		@Nullable
		protected T pendingTarget;
		protected int aggroTime;
		protected int teleportTime;
		protected final TargetingConditions startAggroTargetConditions;
		protected final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat()
				.ignoreLineOfSight();

		public LookForPlayerGoal(NFFTamedEnderManPreset enderman, Class<T> type,
				@Nullable Predicate<LivingEntity> pSelectionPredicate)
		{
			super(enderman, type, 10, false, false, pSelectionPredicate);
			this.enderman = enderman;
			this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance())
					.selector((p_32578_) ->
					{
						return enderman.isLookingAtMe((Player) p_32578_);
					});
		}


		public boolean checkCanUse() {
			this.pendingTarget = this.enderman.level().getNearestPlayer(this.startAggroTargetConditions,
					this.enderman);
			return this.pendingTarget != null;
		}

		public void start() {
			this.aggroTime = this.adjustedTickDelay(5);
			this.teleportTime = 0;
			this.enderman.setBeingStaredAt();
		}

		public void stop() {
			this.pendingTarget = null;
			super.stop();
		}

		public boolean checkCanContinueToUse() {
			if (this.pendingTarget != null)
			{
				if (!this.enderman.isLookingAtMe(this.pendingTarget))
				{
					return false;
				} else
				{
					this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
					return true;
				}
			} else
			{
				return this.target != null
						&& this.continueAggroTargetConditions.test(this.enderman, this.target) ? true
								: super.checkCanContinueToUse();
			}
		}

		public void tick() {
			if (this.enderman.getTarget() == null)
			{
				super.setTarget((LivingEntity) null);
			}

			if (this.pendingTarget != null)
			{
				if (--this.aggroTime <= 0)
				{
					this.target = this.pendingTarget;
					this.pendingTarget = null;
					super.start();
				}
			} else
			{
				if (this.target != null && !this.enderman.isPassenger())
				{
					if (this.enderman.isLookingAtMe((Player) this.target))
					{
						if (this.target.distanceToSqr(this.enderman) < 16.0D)
						{
							this.enderman.teleport();
						}

						this.teleportTime = 0;
					} else if (this.target.distanceToSqr(this.enderman) > 256.0D
							&& this.teleportTime++ >= this.adjustedTickDelay(30)
							&& this.enderman.teleportTowards(this.target))
					{
						this.teleportTime = 0;
					}
				}

				super.tick();
			}

		}
	}
*/
	public static class TakeBlockGoal extends NFFGoal
	{
		protected final NFFTamedEnderManPreset enderman;

		public TakeBlockGoal(NFFTamedEnderManPreset enderman)
		{
			super(enderman);
			this.mob = enderman;
			this.enderman = enderman;
			this.allowState(WANDER);
		}

		@Override
		public boolean checkCanUse() {
			if (isDisabled())
				return false;
			if (!enderman.canAutoTakeBlocks)
				return false;
			if (this.enderman.getCarriedBlock() != null)
			{
				return false;
			} else if (!net.minecraftforge.event.ForgeEventFactory
					.getMobGriefingEvent(this.enderman.level(), this.enderman))
			{
				return false;
			} else
			{
				return this.enderman.getRandom().nextInt(reducedTickDelay(20)) == 0;
			}
		}

		@Override
		public void onTick() {
			RandomSource random = this.enderman.getRandom();
			Level level = this.enderman.level();
			int i = Mth.floor(this.enderman.getX() - 2.0D + random.nextDouble() * 4.0D);
			int j = Mth.floor(this.enderman.getY() + random.nextDouble() * 3.0D);
			int k = Mth.floor(this.enderman.getZ() - 2.0D + random.nextDouble() * 4.0D);
			BlockPos blockpos = new BlockPos(i, j, k);
			BlockState blockstate = level.getBlockState(blockpos);
			Vec3 vec3 = new Vec3(this.enderman.getBlockX() + 0.5D, j + 0.5D,
					this.enderman.getBlockZ() + 0.5D);
			Vec3 vec31 = new Vec3(i + 0.5D, j + 0.5D, k + 0.5D);
			BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE,
					ClipContext.Fluid.NONE, this.enderman));
			boolean flag = blockhitresult.getBlockPos().equals(blockpos);
			if (blockstate.is(BlockTags.ENDERMAN_HOLDABLE) && flag)
			{
				level.removeBlock(blockpos, false);
				level.gameEvent(this.enderman, GameEvent.BLOCK_DESTROY, blockpos);
				this.enderman.setCarriedBlock(blockstate.getBlock().defaultBlockState());
			}

		}
	}

	public static class AngerEvent extends LivingEvent
	{
	    protected final Player player;

	    public AngerEvent(NFFTamedEnderManPreset enderman, Player player)
	    {
	        super(enderman);
	        this.player = player;
	    }

	    /**
	     * The player that is being checked.
	     */
	    public Player getPlayer()
	    {
	        return player;
	    }

	    @Override
	    public NFFTamedEnderManPreset getEntity()
	    {
	        return (NFFTamedEnderManPreset) super.getEntity();
	    }
	}
}
