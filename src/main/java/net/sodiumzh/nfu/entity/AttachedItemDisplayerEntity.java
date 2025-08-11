package net.sodiumzh.nfu.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * An entity which displays an {@link ItemStack} icon and moves together with another entity.
 */
public class AttachedItemDisplayerEntity extends Entity implements ItemSupplier {

    private static final EntityDataAccessor<ItemStack> DISPLAYING_ITEM =
        SynchedEntityData.defineId(AttachedItemDisplayerEntity.class, EntityDataSerializers.ITEM_STACK);
    private Supplier<Vec3> offset = () -> Vec3.ZERO;
    private Entity attachedTo;

    public AttachedItemDisplayerEntity(EntityType<? extends AttachedItemDisplayerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DISPLAYING_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    public ItemStack getItem() {
        return this.getEntityData().get(DISPLAYING_ITEM);
    }

    public AttachedItemDisplayerEntity setItem(ItemStack item) {
        this.getEntityData().set(DISPLAYING_ITEM, item);
        return this;
    }

    public Entity getAttachedEntity() {
        return attachedTo;
    }

    public AttachedItemDisplayerEntity setAttachedEntity(@Nonnull Entity entity) {
        attachedTo = entity;
        return this;
    }

    public Supplier<Vec3> getOffset() {
        return offset;
    }

    public AttachedItemDisplayerEntity setOffset(Supplier<Vec3> offset) {
        this.offset = offset;
        return this;
    }

    public AttachedItemDisplayerEntity setOffset(Vec3 offset) {
        this.offset = () -> offset;
        return this;
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.attachedTo == null || !this.attachedTo.isAlive() || !Objects.equals(this.level, this.attachedTo.level)) {
                this.discard();
                return;
            }
            this.moveTo(this.attachedTo.getBoundingBox().getCenter().add(this.offset.get()));
        }
    }

    public AttachedItemDisplayerEntity sineHovering(Vec3 offsetCenter, double amplitude, int periodTicks) {
        this.setOffset(() -> offsetCenter.add(0d,
            amplitude * Math.sin((double)(this.tickCount % periodTicks) / (double)periodTicks), 0d));
        return this;
    }

    public Packet<?> getAddEntityPacket() {
        Entity entity = this.attachedTo;
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level.getEntity(pPacket.getData());
        if (entity != null) {
            this.attachedTo = entity;
        }

    }
}
