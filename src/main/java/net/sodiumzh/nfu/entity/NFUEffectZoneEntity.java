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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.util.TriPredicate;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.math.Field3D;
import net.sodiumzh.nfu.math.IFieldPattern3D;
import net.sodiumzh.nfu.math.IInequalityPattern3D;
import net.sodiumzh.nfu.math.Inequality3D;
import net.sodiumzh.nfu.object.IChainModifiable;
import net.sodiumzh.nfu.registry.NFUEntityDataSerializers;
import net.sodiumzh.nfu.registry.NFUEntityTypes;
import net.sodiumzh.nfu.util.NFUInfoStatics;
import net.sodiumzh.nfu.util.NFUMathStatics;
import net.sodiumzh.nfu.util.NFUReflectionStatics;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class NFUEffectZoneEntity extends Projectile implements IChainModifiable<NFUEffectZoneEntity> {

    protected static final EntityDataAccessor<ParticleOptions> PARTICLE
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.PARTICLE);
    protected static final EntityDataAccessor<Integer> PARTICLE_AMOUNT
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<Field3D>> PARTICLE_SPEED_FUNCTION
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, NFUEntityDataSerializers.OPTIONAL_FIELD_3D.get());
    protected static final EntityDataAccessor<Inequality3D> PARTICLE_AREA
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, NFUEntityDataSerializers.INEQUALITY_3D.get());
    protected static final EntityDataAccessor<Optional<AABB>> PARTICLE_AREA_BOUNDING_BOX
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, NFUEntityDataSerializers.OPTIONAL_BOUNDING_BOX.get());
    protected static final EntityDataAccessor<Float> GRAVITY
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> SCALE_WIDTH
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> SCALE_HEIGHT
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<String> IDENTIFIER
        = SynchedEntityData.defineId(NFUEffectZoneEntity.class, EntityDataSerializers.STRING);

    protected static final Field FIELD_ENTITY_DIMENSIONS;
    static {
        try {
            FIELD_ENTITY_DIMENSIONS = Entity.class.getDeclaredField(NFUReflectionStatics.remapFieldName("f_19815_"));
            FIELD_ENTITY_DIMENSIONS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ReflectionFailedException(e);
        }
    }

        private int lifetime = 10 * 20;
    @Nullable
    private Consumer<NFUEffectZoneEntity> onServerTick = null;
    @Nullable
    private BiConsumer<NFUEffectZoneEntity, Entity> onOverlapEntity = null;
    @Nullable
    private BiConsumer<NFUEffectZoneEntity, LivingEntity> onOverlapLiving = null;
    @Nullable
    private TriPredicate<NFUEffectZoneEntity, BlockPos, BlockState> blockOverlapFilter = null;
    @Nullable
    private BiPredicate<NFUEffectZoneEntity, Entity> entityOverlapFilter = null;
    @Nullable
    private BiPredicate<NFUEffectZoneEntity, LivingEntity> livingOverlapFilter = null;
    @Nullable
    private TriConsumer<NFUEffectZoneEntity, BlockPos, BlockState> onOverlapBlock = null;
    private final Multimap<Integer, Consumer<NFUEffectZoneEntity>> scheduledServerActions = HashMultimap.create();
    private boolean overlapIgnoresOwner = true;

    public NFUEffectZoneEntity(EntityType<? extends NFUEffectZoneEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static NFUEffectZoneEntity create(Level level) {
        return new NFUEffectZoneEntity(NFUEntityTypes.DEFAULT_EFFECT_ZONE.get(), level);
    }

    public static NFUEffectZoneEntity create(LivingEntity owner) {
        var res = new NFUEffectZoneEntity(NFUEntityTypes.DEFAULT_EFFECT_ZONE.get(), owner.level);
        res.setOwner(owner);
        return res;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PARTICLE, ParticleTypes.ASH);
        this.entityData.define(PARTICLE_AMOUNT, 0);
        this.entityData.define(PARTICLE_SPEED_FUNCTION, Optional.empty());
        this.entityData.define(PARTICLE_AREA, Inequality3D.limitedInOne());
        this.entityData.define(PARTICLE_AREA_BOUNDING_BOX, Optional.empty());
        this.entityData.define(GRAVITY, 0f);
        this.entityData.define(SCALE_WIDTH, 1f);
        this.entityData.define(SCALE_HEIGHT, 1f);
        this.entityData.define(IDENTIFIER, "nfulib:effect_zone");
    }

    public NFUEffectZoneEntity particle(@Nonnull ParticleOptions type, int frequency) {
        this.entityData.set(PARTICLE, type);
        this.entityData.set(PARTICLE_AMOUNT, frequency);
        return this;
    }

    /**
     * Set area shape to add particles. Note that the inequality input is in INTERNAL COORDINATION, i.e.
     * bounding box center = (0,0,0) and corners are (1,1,1), (-1,-1,-1) etc.
     * <p>Input nonnull. To make the particles fill the whole bounding box, use {@link Inequality3D#limitedInOne()}.
     * <p>Note: never use {@link Inequality3D#fullSpace()} or {@link IInequalityPattern3D#FULL_SPACE},
     * otherwise the particle will spawn all over the level and will not be seen.
     */
    public NFUEffectZoneEntity particleAreaShape(@Nonnull Inequality3D shape) {
        this.entityData.set(PARTICLE_AREA, shape);
        return this;
    }

    /**
     * This bounding box should be as small as possible but can fully contain the spawning area. Set this only under either case below:
     * <p>(a) the particle area's volume is significantly smaller than the whole bounding box's volume, for performance consideration;
     * <p>If the particle area is too small, particle spawning may undergo too many failed attempts and cause resource
     * waste. At this time, the spawn area can be limited to improve the performance.
     * <p>(b) the particle area exceeds the entity bounding box. Otherwise, the area will be truncated by the entity bounding box.
     */
    public NFUEffectZoneEntity particleAreaBoundingBox(AABB bb) {
        this.entityData.set(PARTICLE_AREA_BOUNDING_BOX, Optional.ofNullable(bb));
        return this;
    }

    /**
     * Define the particle velocity as a function of the position.
     */
    public NFUEffectZoneEntity particleVelocityFunction(Field3D function) {
        this.entityData.set(PARTICLE_SPEED_FUNCTION, Optional.ofNullable(function));
        return this;
    }

    public NFUEffectZoneEntity particleFixedVelocity(Vec3 vel) {
        return this.particleVelocityFunction(IFieldPattern3D.ZERO.get().field().putValueAddition(vel));
    }

    public NFUEffectZoneEntity particleRandomSpeed(Vec3 speed) {
        return this.particleVelocityFunction(IFieldPattern3D.RANDOM_GAUSSIAN.get().field().scaleValue(speed));
    }

    public NFUEffectZoneEntity setOnServerTick(@Nullable Consumer<NFUEffectZoneEntity> action) {
        this.onServerTick = action;
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        // Handle server actions
        if (!this.level.isClientSide) {
            if (this.lifetime >= 0 && this.tickCount > this.lifetime)
                this.discard();
            if (this.onServerTick != null)
                this.onServerTick.accept(this);
            if (this.onOverlapEntity != null || this.onOverlapLiving != null) {
                this.level.getEntities(this, this.getBoundingBox().inflate(20)).stream()
                    .filter(e -> e.getBoundingBox().intersects(this.getBoundingBox()) && this.canOverlapEntity(e))
                    .forEach(e -> {
                        if (this.onOverlapEntity != null)
                            this.onOverlapEntity.accept(this, e);
                        if (this.onOverlapLiving != null && e instanceof LivingEntity l)
                            this.onOverlapLiving.accept(this, l);
                    });
            }
            if (this.onOverlapBlock != null) {
                var stream = BlockPos.betweenClosedStream(this.getBoundingBox());
                Level level = this.level;
                if (this.blockOverlapFilter != null)
                    stream = stream.filter(p -> blockOverlapFilter.test(this, p, level.getBlockState(p)));
                stream.forEach(p -> this.onOverlapBlock.accept(this, p, level.getBlockState(p)));
            }
            this.scheduledServerActions.get(this.tickCount).forEach(c -> c.accept(this));
        }
        // Handle particles
        else if (this.entityData.get(PARTICLE_AMOUNT) > 0) {
            // Cache constants
            int particleAmount = this.entityData.get(PARTICLE_AMOUNT);
                // *Now it's in entity BB's internal coordination
            Inequality3D shape = this.entityData.get(PARTICLE_AREA);
            AABB shapeBB = this.entityData.get(PARTICLE_AREA_BOUNDING_BOX)
                .orElse(new AABB(-1, -1, -1, 1, 1, 1));
            UnaryOperator<Vec3> speedFunction = this.entityData.get(PARTICLE_SPEED_FUNCTION).orElse(null);
            if (speedFunction == null) speedFunction = v -> Vec3.ZERO;
            ParticleOptions particleOptions = this.entityData.get(PARTICLE);

            for (int i = 0; i < particleAmount; ++i) {
                Vec3 particlePosRel = this.randomParticlePos(shape, shapeBB);
                if (particlePosRel == null) continue;
                Vec3 particlePosAbs = NFUMathStatics.relToAbs(particlePosRel, this.getBoundingBox());
                Vec3 speed = speedFunction.apply(particlePosRel);
                this.level.addParticle(particleOptions, particlePosAbs.x, particlePosAbs.y, particlePosAbs.z,
                    speed.x, speed.y, speed.z);
            }
        }
        // Handle motion
        this.setPos(this.position().add(this.getDeltaMovement()));
        this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, -this.getGravity(), 0.0));
        this.updateEntitySize();
    }

    private Vec3 randomParticlePos(Inequality3D shape, AABB shapeBB) {
        Vec3 res = null;
        for (int i = 0; i < 16; ++i) {
            res = NFUMathStatics.rndPosition(shapeBB);
            if (shape.test(res)) break;
        }
        return res;
    }

    public float getGravity() {
        return this.entityData.get(GRAVITY);
    }

    public NFUEffectZoneEntity setGravity(float value) {
        this.entityData.set(GRAVITY, value);
        this.setNoGravity(value > 0f);
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
     * Set how many ticks it should exist at most. After this lifetime, this entity will be auto removed. -1 means
     * persistent (i.e. you must manually call discard() somewhere to remove it).
     */
    public NFUEffectZoneEntity setLifetime(int ticks) {
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
     * Set actions invoked on server every tick when an entity is overlapping this effect zone.
     * Note: Living entities will also invoke this action.
     */
    public NFUEffectZoneEntity setOnServerGenericEntityOverlap(@Nullable BiConsumer<NFUEffectZoneEntity, Entity> action) {
        this.onOverlapEntity = action;
        return this;
    }

    /**
     * Set actions invoked on server every tick when a living entity is overlapping this effect zone.
     */
    public NFUEffectZoneEntity setOnServerLivingOverlap(@Nullable BiConsumer<NFUEffectZoneEntity, LivingEntity> action) {
        this.onOverlapLiving = action;
        return this;
    }

    /**
     * If block overlapping needs to be handled, set which types of blocks should be processed.
     * As block overlapping must be handled every tick for every overlapping block, it may cause performance issues
     * if the action is costly and the effect zone is large. Use this filter to reduce the block amount to process.
     */
    public NFUEffectZoneEntity setBlockOverlapFilter(@Nullable TriPredicate<NFUEffectZoneEntity, BlockPos, BlockState> filter) {
        this.blockOverlapFilter = filter;
        return this;
    }

    /**
     * If block overlapping needs to be handled, set which types of blocks should be processed.
     * As block overlapping must be handled every tick for every overlapping block, it may cause performance issues
     * if the action is costly and the effect zone is large. Use this filter to reduce the block amount to process.
     */
    public NFUEffectZoneEntity setEntityOverlapFilter(@Nullable BiPredicate<NFUEffectZoneEntity, Entity> filter) {
        this.entityOverlapFilter = filter;
        return this;
    }

    /**
     * If block overlapping needs to be handled, set which types of blocks should be processed.
     * As block overlapping must be handled every tick for every overlapping block, it may cause performance issues
     * if the action is costly and the effect zone is large. Use this filter to reduce the block amount to process.
     */
    public NFUEffectZoneEntity setLivingOverlapFilter(@Nullable BiPredicate<NFUEffectZoneEntity, LivingEntity> filter) {
        this.livingOverlapFilter = filter;
        return this;
    }

    protected boolean canOverlapEntity(Entity e) {
        if (overlapIgnoresOwner && e.equals(this.getOwner())) return false;
        if (entityOverlapFilter != null && entityOverlapFilter.test(this, e)) return false;
        if (livingOverlapFilter != null && e instanceof LivingEntity l && livingOverlapFilter.test(this, l)) return false;
        return true;
    }

    protected boolean canOverlapBlock(BlockPos pos, BlockState bs) {
        return this.blockOverlapFilter != null && this.blockOverlapFilter.test(this, pos, bs);
    }

    /**
     * Set actions on each overlapping block every tick.
     * <p>Prevent using this if the effect zone is large. It will be invoked on every block
     * it's overlapping every tick, and may cause possible performance issues.
     * If possible, call {@code blockOverlapFilter} to reduce the amount of handled blocks.
     */
    public NFUEffectZoneEntity setServerBlockOverlap(@Nullable TriConsumer<NFUEffectZoneEntity, BlockPos, BlockState> action) {
        this.onOverlapBlock = action;
        return this;
    }

    public NFUEffectZoneEntity setOverlapIgnoresOwner(boolean value) {
        this.overlapIgnoresOwner = value;
        return this;
    }



    public Inequality3D getParticleArea() {
        return this.entityData.get(PARTICLE_AREA).relToAbs(this.getBoundingBox());
    }

    public Field3D getSpeedFunction() {
        return this.entityData.get(PARTICLE_SPEED_FUNCTION).orElse(Field3D.zero());
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /**
     * Set this entity's center to be a given center.
     * @param shouldSync Set this true if you're running this method ONLY ON SERVER, and the value will be set on
     *                   the server and synched to the client, and on client it won't do anything. False if you're running
     *                   this method on both sides without synching.
     */
    public NFUEffectZoneEntity alignCenterTo(Vec3 center, boolean shouldSync) {
        Vec3 targetPos = center.subtract(0, this.getBoundingBox().getYsize() / 2d, 0);
        if (shouldSync && !this.level.isClientSide)
            ServerEntityMotion.movement(targetPos.subtract(this.position())).apply(this);
        else if (!shouldSync)
            this.setPos(targetPos);
        return this;
    }

    /**
     * Set this entity's center to be a given center. Not synched.
     */
    public NFUEffectZoneEntity alignCenterTo(Vec3 center) {
        return alignCenterTo(center, false);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(this.entityData.get(SCALE_WIDTH), this.entityData.get(SCALE_HEIGHT));
    }

    protected void updateEntitySize() {
        try {
            FIELD_ENTITY_DIMENSIONS.set(this, EntityDimensions.scalable(this.entityData.get(SCALE_WIDTH), this.entityData.get(SCALE_HEIGHT)));
            this.setBoundingBox(this.makeBoundingBox());
        } catch (IllegalAccessException e) {
            throw new ReflectionFailedException(e);
        }
    }

    public NFUEffectZoneEntity setScale(double width, double height) {
        this.entityData.set(SCALE_WIDTH, (float)width);
        this.entityData.set(SCALE_HEIGHT, (float)height);
        updateEntitySize();
        return this;
    }

    @Override
    public AABB makeBoundingBox() {
        return super.makeBoundingBox();
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
     * as they cannot be distinguished by entity type. Default is {@code "nfulib:effect_zone"}.
     */
    @Nonnull
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(this.entityData.get(IDENTIFIER));
    }

    /**
     * Set identifier. The identifier is an additional string for each entity for distinguishing from each other,
     * as they cannot be distinguished by entity type. Default is {@code "nfulib:effect_zone"}.
     */
    public NFUEffectZoneEntity setIdentifier(@Nonnull ResourceLocation identifier) {
        this.entityData.set(IDENTIFIER, identifier.toString());
        return this;
    }

    public NFUEffectZoneEntity scheduleServerAction(int timePoint, Consumer<NFUEffectZoneEntity> action) {
        this.scheduledServerActions.put(timePoint, action);
        return this;
    }

    @Override
    public Component getName() {
        ResourceLocation id = this.getIdentifier();
        return NFUInfoStatics.createTranslatable("entity." + id.getNamespace() + ".effect_zone." + id.getPath());
    }

}
