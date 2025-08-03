package net.sodiumzh.nfu.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.sodiumzh.nfu.math.Field3D;
import net.sodiumzh.nfu.math.IFieldPattern3D;
import net.sodiumzh.nfu.math.IInequalityPattern3D;
import net.sodiumzh.nfu.math.Inequality3D;
import net.sodiumzh.nfu.registry.NFUEntityDataSerializers;
import net.sodiumzh.nfu.util.NFUMathStatics;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

// TODO Move this to NFU
public class NFUEffectZoneEntity extends ThrowableItemProjectile {

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


    private float gravity = 0.03F;
    private int lifetime = 10 * 20;
    @Nullable
    private Consumer<NFUEffectZoneEntity> onServerTick = null;
    @Nullable
    private BiConsumer<NFUEffectZoneEntity, Entity> onOverlapEntity = null;
    @Nullable
    private BiConsumer<NFUEffectZoneEntity, LivingEntity> onOverlapLiving = null;
    @Nullable
    private BiPredicate<BlockPos, BlockState> blockOverlapFilter = null;
    @Nullable
    private TriConsumer<NFUEffectZoneEntity, BlockPos, BlockState> onOverlapBlock = null;
    private boolean onlyAffectsLivingEntity = false;

    public NFUEffectZoneEntity(EntityType<? extends NFUEffectZoneEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARTICLE, ParticleTypes.ASH);
        this.entityData.define(PARTICLE_AMOUNT, 0);
        this.entityData.define(PARTICLE_SPEED_FUNCTION, Optional.empty());
        this.entityData.define(PARTICLE_AREA, Inequality3D.limitedInOne());
        this.entityData.define(PARTICLE_AREA_BOUNDING_BOX, Optional.empty());
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }



    public NFUEffectZoneEntity setDisplayedItem(@Nonnull ItemStack itemStack) {
        this.setItem(itemStack);
        return this;
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
    public ItemStack getItem() {
        return this.getItemRaw();
    }

    @Override
    public void tick() {
        super.tick();
        // Handle server actions
        if (!this.level().isClientSide) {
            if (this.lifetime >= 0 && this.tickCount > this.lifetime)
                this.discard();
            if (this.onServerTick != null)
                this.onServerTick.accept(this);
            if (this.onOverlapEntity != null || this.onOverlapLiving != null) {
                this.level().getEntities(this, this.getBoundingBox().inflate(20))
                    .stream().filter(e -> e.getBoundingBox().intersects(this.getBoundingBox()))
                    .forEach(e -> {
                        if (this.onOverlapEntity != null)
                            this.onOverlapEntity.accept(this, e);
                        if (this.onOverlapLiving != null && e instanceof LivingEntity l)
                            this.onOverlapLiving.accept(this, l);
                    });
            }
            if (this.onOverlapBlock != null) {
                var stream = BlockPos.betweenClosedStream(this.getBoundingBox());
                Level level = this.level();
                if (this.blockOverlapFilter != null)
                    stream = stream.filter(p -> blockOverlapFilter.test(p, level.getBlockState(p)));
                stream.forEach(p -> this.onOverlapBlock.accept(this, p, level.getBlockState(p)));
            }
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
                this.level().addParticle(particleOptions, particlePosAbs.x, particlePosAbs.y, particlePosAbs.z,
                    speed.x, speed.y, speed.z);
            }
        }

    }

    private Vec3 randomParticlePos(Inequality3D shape, AABB shapeBB) {
        Vec3 res = null;
        for (int i = 0; i < 16; ++i) {
            res = NFUMathStatics.rndPosition(shapeBB);
            if (shape.test(res)) break;
        }
        return res;
    }


    @Override
    public float getGravity() {
        return this.gravity;
    }

    public NFUEffectZoneEntity setGravity(float value) {
        this.gravity = value;
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
    public NFUEffectZoneEntity setBlockOverlapFilter(@Nullable BiPredicate<BlockPos, BlockState> filter) {
        this.blockOverlapFilter = filter;
        return this;
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

}
