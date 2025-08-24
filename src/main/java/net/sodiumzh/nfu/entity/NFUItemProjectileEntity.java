package net.sodiumzh.nfu.entity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.object.IChainModifiable;
import net.sodiumzh.nfu.registry.NFUEntityDataSerializers;
import net.sodiumzh.nfu.registry.NFUEntityTypes;
import net.sodiumzh.nfu.util.NFUInfoStatics;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class NFUItemProjectileEntity extends Projectile implements ItemSupplier, IChainModifiable<NFUItemProjectileEntity> {

    protected static final EntityDataAccessor<ItemStack> DISPLAYED_ITEM
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<Float> SCALE_XZ
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> SCALE_Y
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<ParticleOptions> PARTICLE
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.PARTICLE);
    protected static final EntityDataAccessor<Integer> PARTICLE_AMOUNT
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Vec3> PARTICLE_SPEED
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, NFUEntityDataSerializers.VEC3);
    protected static final EntityDataAccessor<Float> PARTICLE_RANGE
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> FIRE_IMMUNE
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> GRAVITY
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> IGNORES_PORTALS
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> LIQUID_RESISTANCE_FACTOR
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> AIR_RESISTANCE_FACTOR
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<String> IDENTIFIER
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.STRING);

    protected static final Field FIELD_ENTITY_DIMENSIONS;
    protected static final Field FIELD_HIT_IGNORES_OWNER;
    static {
        try {
            FIELD_ENTITY_DIMENSIONS = Entity.class.getDeclaredField(NFUReflectionStatics.remapFieldName("f_19815_"));
            FIELD_ENTITY_DIMENSIONS.setAccessible(true);
            FIELD_HIT_IGNORES_OWNER = Projectile.class.getDeclaredField(NFUReflectionStatics.remapFieldName("f_37246_"));
            FIELD_HIT_IGNORES_OWNER.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ReflectionFailedException(e);
        }

    }
    private int lifetime = 10 * 20;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, BlockHitResult> onHitBlock = null;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, EntityHitResult> onHitEntity = null;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, EntityHitResult> onHitLiving = null;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, HitResult> onHitBlockOrEntity = null;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, HitResult> onHitBlockOrLiving = null;
    @Nullable
    private BiPredicate<NFUItemProjectileEntity, Entity> ignoresEntityIf = null;
    @Nullable
    private BiPredicate<NFUItemProjectileEntity, LivingEntity> ignoresLivingIf = null;
    private boolean ignoresOwner = true;
    @Nullable
    private Consumer<NFUItemProjectileEntity> onTick = null;
    private final Multimap<Integer, Consumer<NFUItemProjectileEntity>> scheduledServerActions = HashMultimap.create();

    public NFUItemProjectileEntity(EntityType<? extends NFUItemProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static NFUItemProjectileEntity create(Level level) {
        return new NFUItemProjectileEntity(NFUEntityTypes.DEFAULT_ITEM_PROJECTILE.get(), level);
    }

    public static NFUItemProjectileEntity create(LivingEntity owner) {
        NFUItemProjectileEntity res = new NFUItemProjectileEntity(NFUEntityTypes.DEFAULT_ITEM_PROJECTILE.get(), owner.level);
        res.setOwner(owner);
        return res;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DISPLAYED_ITEM, ItemStack.EMPTY);
        this.entityData.define(SCALE_XZ, 1f);
        this.entityData.define(SCALE_Y, 1f);
        this.entityData.define(PARTICLE, ParticleTypes.ASH);
        this.entityData.define(PARTICLE_AMOUNT, 0);
        this.entityData.define(PARTICLE_SPEED, Vec3.ZERO);
        this.entityData.define(PARTICLE_RANGE, 0.1f);
        this.entityData.define(FIRE_IMMUNE, true);
        this.entityData.define(GRAVITY, 0.03f);
        this.entityData.define(IGNORES_PORTALS, true);
        this.entityData.define(LIQUID_RESISTANCE_FACTOR, 0.2f);
        this.entityData.define(AIR_RESISTANCE_FACTOR, 0.01f);
        this.entityData.define(IDENTIFIER, "nfulib:item_projectile");
    }

    @Override
    public ItemStack getItem() {
        return this.entityData.get(DISPLAYED_ITEM);
    }

    public NFUItemProjectileEntity setItem(@Nullable ItemStack itemStack) {
        this.entityData.set(DISPLAYED_ITEM, Objects.requireNonNullElse(itemStack, ItemStack.EMPTY));
        return this;
    }

    public NFUItemProjectileEntity particle(@Nonnull ParticleOptions type, int frequency, Vec3 speed) {
        this.entityData.set(PARTICLE, type);
        this.entityData.set(PARTICLE_AMOUNT, frequency);
        this.entityData.set(PARTICLE_SPEED, speed);
        return this;
    }

    public NFUItemProjectileEntity particle(@Nonnull ParticleOptions type, int frequency, double speed) {
        return this.particle(type, frequency, new Vec3(speed, speed, speed));
    }

    public NFUItemProjectileEntity particle(@Nonnull ParticleOptions type, int frequency) {
        return this.particle(type, frequency, Vec3.ZERO);
    }

    public NFUItemProjectileEntity particle(@Nonnull ParticleOptions type) {
        return this.particle(type, 3, Vec3.ZERO);
    }

    /**
     * Set action on this project hit any entity (including living).
     * <p>Note: this will be only invoked on server.
     */
    public NFUItemProjectileEntity setOnHitEntity(BiConsumer<NFUItemProjectileEntity, EntityHitResult> action) {
        this.onHitEntity = action;
        return this;
    }

    /**
     * Set action on this project hit any block.
     * <p>Note: this will be only invoked on server.
     */
    public NFUItemProjectileEntity setOnHitBlock(BiConsumer<NFUItemProjectileEntity, BlockHitResult> action) {
        this.onHitBlock = action;
        return this;
    }

    /**
     * Set action on this project hit living entity.
     * <p>Note: this will be only invoked on server.
     */
    public NFUItemProjectileEntity setOnHitLiving(BiConsumer<NFUItemProjectileEntity, EntityHitResult> action) {
        this.onHitLiving = action;
        return this;
    }

    /**
     * Set action on this project hit block or any entity.
     * <p>Note: this will be only invoked on server.
     */
    public NFUItemProjectileEntity setOnHitBlockOrEntity(BiConsumer<NFUItemProjectileEntity, HitResult> action) {
        this.onHitBlockOrEntity = action;
        return this;
    }

    /**
     * Set action on this project hit block or living entity.
     * <p>Note: this will be only invoked on server.
     */
    public NFUItemProjectileEntity setOnHitBlockOrLiving(BiConsumer<NFUItemProjectileEntity, HitResult> action) {
        this.onHitBlockOrLiving = action;
        return this;
    }

    public NFUItemProjectileEntity setHitIgnoresOwner(boolean shouldIgnoreOwner) {
        try {
            FIELD_HIT_IGNORES_OWNER.set(this, shouldIgnoreOwner);
            this.ignoresOwner = shouldIgnoreOwner;
        } catch (IllegalAccessException e) {
            throw new ReflectionFailedException(e);
        }
        return this;
    }

    public NFUItemProjectileEntity setHitIgnoresEntity(BiPredicate<NFUItemProjectileEntity, Entity> condition) {
        this.ignoresEntityIf = condition;
        return this;
    }

    public NFUItemProjectileEntity setHitIgnoresLiving(BiPredicate<NFUItemProjectileEntity, LivingEntity> condition) {
        this.ignoresLivingIf = condition;
        return this;
    }

    public NFUItemProjectileEntity setOnTick(@Nullable Consumer<NFUItemProjectileEntity> action) {
        this.onTick = action;
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickHit();
        this.tickMotion();
        if (!this.level.isClientSide && this.lifetime >= 0 && this.tickCount > this.lifetime)
            this.discard();
        if (this.level.isClientSide && this.entityData.get(PARTICLE_AMOUNT) > 0) {
            Vec3 center = this.getBoundingBox().getCenter();
            for (int i = 0 ; i < this.entityData.get(PARTICLE_AMOUNT); ++i) {
                Vec3 speed = this.getEntityData().get(PARTICLE_SPEED);
                this.level.addParticle(this.getEntityData().get(PARTICLE),
                    center.x, center.y, center.z, speed.x, speed.y, speed.z);
            }
        }
        if (this.onTick != null)
            this.onTick.accept(this);
        if (!this.level.isClientSide) {
            this.scheduledServerActions.get(this.tickCount).forEach(c -> c.accept(this));
        }
    }

    protected void tickHit() {
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean enteredPortal = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (!this.ignoresPortals() && blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                enteredPortal = true;
            } else if (!this.ignoresPortals() && blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }
                enteredPortal = true;
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS && !enteredPortal && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    protected void tickMotion() {
        // Calculate resistance
        this.checkInsideBlocks();
        this.updateRotation();
        double resistance;
        if (this.isInWater() || this.isInLava()) {
            resistance = this.getLiquidResistanceFactor();
        } else {
            resistance = this.getAirResistanceFactor();
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(1d - resistance));

        // Handle gravity
        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, -this.getGravity(), 0.0));
        }

        // Handle motion
        this.setPos(this.position().add(this.getDeltaMovement()));
        this.updateEntitySize();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            if (this.ignoresOwner && pResult.getEntity().equals(this.getOwner())) return;
            if (ignoresEntityIf != null && ignoresEntityIf.test(this, pResult.getEntity())) return;
            if (ignoresLivingIf != null && pResult.getEntity() instanceof LivingEntity l && ignoresLivingIf.test(this, l)) return;
            Optional.ofNullable(this.onHitEntity).ifPresent(action -> action.accept(this, pResult));
            Optional.ofNullable(this.onHitBlockOrEntity).ifPresent(action -> action.accept(this, pResult));
            if (pResult.getEntity() instanceof LivingEntity) {
                Optional.ofNullable(this.onHitLiving).ifPresent(action -> action.accept(this, pResult));
                Optional.ofNullable(this.onHitBlockOrLiving).ifPresent(action -> action.accept(this, pResult));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level.isClientSide) {
            Optional.ofNullable(this.onHitBlock).ifPresent(action -> action.accept(this, pResult));
            Optional.ofNullable(this.onHitBlockOrEntity).ifPresent(action -> action.accept(this, pResult));
            Optional.ofNullable(this.onHitBlockOrLiving).ifPresent(action -> action.accept(this, pResult));
       }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target)
            && !(this.ignoresEntityIf != null && this.ignoresEntityIf.test(this, target))
            && !(this.ignoresLivingIf != null && target instanceof LivingEntity living && this.ignoresLivingIf.test(this, living));
    }

    public float getGravity() {
        return this.entityData.get(GRAVITY);
    }

    public NFUItemProjectileEntity setGravity(float value) {
        this.entityData.set(GRAVITY, value);
        this.setNoGravity(value > 0f);
        return this;
    }

    @Override
    public boolean fireImmune() {
        return this.entityData.get(FIRE_IMMUNE);
    }

    public NFUItemProjectileEntity setFireImmune(boolean value) {
        this.entityData.set(FIRE_IMMUNE, value);
        return this;
    }

    public float getLiquidResistanceFactor() {
        return this.entityData.get(LIQUID_RESISTANCE_FACTOR);
    }

    public NFUItemProjectileEntity setLiquidResistanceFactor(float val) {
        this.entityData.set(LIQUID_RESISTANCE_FACTOR, val);
        return this;
    }

    public float getAirResistanceFactor() {
        return this.entityData.get(AIR_RESISTANCE_FACTOR);
    }

    public NFUItemProjectileEntity setAirResistanceFactor(float val) {
        this.entityData.set(AIR_RESISTANCE_FACTOR, val);
        return this;
    }

    public boolean ignoresPortals() {
        return this.entityData.get(IGNORES_PORTALS);
    }

    public NFUItemProjectileEntity setIgnoresPortals(boolean val) {
        this.entityData.set(IGNORES_PORTALS, val);
        return this;
    }

    /**
     * Get how many ticks it should exist at most. After this lifetime, this entity will be force removed. -1 means
     * persistent.
     */
    public int getLifetime() {
        return lifetime >= 0 ? lifetime : -1;
    }

    /**
     * Set how many ticks it should exist at most. After this lifetime, this entity will be force removed. -1 means
     * persistent.
     */
    public NFUItemProjectileEntity setLifetime(int ticks) {
        this.lifetime = ticks;
        return this;
    }

    /**
     * Shoot this projectile to a given position.
     */
    public void shootTo(Vec3 target, float speed, float inaccuracy) {
        Vec3 v = target.subtract(this.getBoundingBox().getCenter()).normalize();
        this.shoot(v.x, v.y, v.z, speed, inaccuracy);
    }


    /**
     * Set position on server, and sync to client. Will not do anything on client.
     * <p>Note: this action will send a packet, and frequent calling this method may cause lag or
     * async issues. If you need to set position and velocity on server multiple times
     * in a single method, use {@link ServerEntityMotion} to collect all motions and apply together.
     */
    public void setPosSynched(Vec3 pos) {
        ServerEntityMotion.zero().setExactPos(this, pos).apply(this);
    }

    /**
     * Set position on server, and sync to client. Will not do anything on client.
     * <p>Note: this action will send a packet, and frequent calling this method may cause lag or
     * async issues. If you need to set position and velocity on server multiple times
     * in a single method, use {@link ServerEntityMotion} to collect all motions and apply together.
     */
    public void setPosSynched(double x, double y, double z) {
        ServerEntityMotion.zero().setExactPos(this, x, y, z).apply(this);
    }

    /**
     * Set velocity on server, and sync to client. Will not do anything on client.
     * <p>Note: this action will send a packet, and frequent calling this method may cause lag or
     * async issues. If you need to set position and velocity on server multiple times
     * in a single method, use {@link ServerEntityMotion} to collect all motions and apply together.
     */
    public void setVelocitySynched(Vec3 vel) {
        ServerEntityMotion.zero().setExactVelocity(this, vel).apply(this);
    }

    /**
     * Set velocity on server, and sync to client. Will not do anything on client.
     * <p>Note: this action will send a packet, and frequent calling this method may cause lag or
     * async issues. If you need to set position and velocity on server multiple times
     * in a single method, use {@link ServerEntityMotion} to collect all motions and apply together.
     */
    public void setVelocitySynched(double x, double y, double z) {
        ServerEntityMotion.zero().setExactVelocity(this, x, y, z).apply(this);
    }

    /**
     * Get identifier. The identifier is an additional string for each entity for distinguishing from each other,
     * as they cannot be distinguished by entity type. Default is {@code "nfulib:item_projectile"}.
     */
    @Nonnull
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(this.entityData.get(IDENTIFIER));
    }

    /**
     * Set identifier. The identifier is an additional string for each entity for distinguishing from each other,
     * as they cannot be distinguished by entity type. Default is {@code "nfulib:item_projectile"}.
     */
    public NFUItemProjectileEntity setIdentifier(@Nonnull ResourceLocation identifier) {
        this.entityData.set(IDENTIFIER, identifier.toString());
        return this;
    }

    public NFUItemProjectileEntity scheduleServerActions(int timePoint, Consumer<NFUItemProjectileEntity> action) {
        this.scheduledServerActions.put(timePoint, action);
        return this;
    }

    public NFUItemProjectileEntity setScale(double xzScale, double yScale) {
        this.entityData.set(SCALE_XZ, (float)xzScale);
        this.entityData.set(SCALE_Y, (float)yScale);
        return this;
    }

    protected void updateEntitySize() {
        try {
            FIELD_ENTITY_DIMENSIONS.set(this, EntityDimensions.scalable(this.entityData.get(SCALE_XZ), this.entityData.get(SCALE_Y)));
            this.setBoundingBox(this.makeBoundingBox());
        } catch (IllegalAccessException e) {
            throw new ReflectionFailedException(e);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(this.entityData.get(SCALE_XZ), this.entityData.get(SCALE_Y));
    }

    @Override
    public Component getName() {
        ResourceLocation id = this.getIdentifier();
        return NFUInfoStatics.createTranslatable("entity." + id.getNamespace() + ".item_projectile." + id.getPath());
    }

}
