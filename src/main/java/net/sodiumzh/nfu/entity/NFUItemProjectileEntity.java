package net.sodiumzh.nfu.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.sodiumzh.nfu.registry.NFUEntityDataSerializers;
import net.sodiumzh.nfu.util.NFUMathStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// TODO Move this to NFU
public class NFUItemProjectileEntity extends ThrowableItemProjectile {

    protected static final EntityDataAccessor<ParticleOptions> PARTICLE
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.PARTICLE);
    protected static final EntityDataAccessor<Integer> PARTICLE_AMOUNT
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Vec3> PARTICLE_SPEED
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, NFUEntityDataSerializers.VEC3.get());
    protected static final EntityDataAccessor<Boolean> FIRE_IMMUNE
        = SynchedEntityData.defineId(NFUItemProjectileEntity.class, EntityDataSerializers.BOOLEAN);



    private float gravity = 0.03F;
    private int lifetime = 10 * 20;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, BlockHitResult> onHitBlock = null;
    @Nullable
    private BiConsumer<NFUItemProjectileEntity, EntityHitResult> onHitEntity = null;
    @Nullable
    private Consumer<NFUItemProjectileEntity> onTick = null;

    public NFUItemProjectileEntity(EntityType<? extends NFUItemProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARTICLE, ParticleTypes.ASH);
        this.entityData.define(PARTICLE_AMOUNT, 0);
        this.entityData.define(PARTICLE_SPEED, Vec3.ZERO);
        this.entityData.define(FIRE_IMMUNE, true);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }



    public NFUItemProjectileEntity setDisplayedItem(@Nonnull ItemStack itemStack) {
        this.setItem(itemStack);
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

    public NFUItemProjectileEntity setOnHitEntity(BiConsumer<NFUItemProjectileEntity, EntityHitResult> action) {
        this.onHitEntity = action;
        return this;
    }

    public NFUItemProjectileEntity setOnHitBlock(BiConsumer<NFUItemProjectileEntity, BlockHitResult> action) {
        this.onHitBlock = action;
        return this;
    }

    public NFUItemProjectileEntity setOnHitBlockOrEntity(BiConsumer<NFUItemProjectileEntity, HitResult> action) {
        this.onHitBlock = action::accept;
        this.onHitEntity = action::accept;
        return this;
    }

    public NFUItemProjectileEntity setOnTick(@Nullable Consumer<NFUItemProjectileEntity> action) {
        this.onTick = action;
        return this;
    }

    @Override
    public ItemStack getItem() {
        return this.getItemRaw();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.lifetime >= 0 && this.tickCount > this.lifetime)
            this.discard();
        if (this.level().isClientSide && this.entityData.get(PARTICLE_AMOUNT) > 0) {
            Vec3 center = this.getBoundingBox().getCenter();
            for (int i = 0 ; i < this.entityData.get(PARTICLE_AMOUNT); ++i) {
                Vec3 speed = this.getEntityData().get(PARTICLE_SPEED);
                this.level().addParticle(this.getEntityData().get(PARTICLE),
                    center.x, center.y, center.z, speed.x, speed.y, speed.z);
            }
        }
        if (this.onTick != null)
            this.onTick.accept(this);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide && this.onHitEntity != null) {
            this.onHitEntity.accept(this, pResult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide && this.onHitBlock != null) {
            this.onHitBlock.accept(this, pResult);
       }
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    @Override
    public boolean fireImmune() {
        return this.entityData.get(FIRE_IMMUNE);
    }

    public NFUItemProjectileEntity setFireImmune(boolean value) {
        this.entityData.set(FIRE_IMMUNE, value);
        return this;
    }

    public NFUItemProjectileEntity setGravity(float value) {
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

}
