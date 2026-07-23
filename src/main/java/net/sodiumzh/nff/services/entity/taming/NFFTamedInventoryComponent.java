package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nfu.entity.component.EntityComponentBase;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NFFTamedInventoryComponent extends EntityComponentBase<Mob> {

    @Nullable
    private NFFTamedMobInventory inventory = null;

    public NFFTamedInventoryComponent(Mob entity) {
        super(entity);
    }

    @Override
    public void tick() {
        if (!this.isClientSide() && this.inventory != null && this.getEntity().equals(this.inventory.getOwner()))
        {
            this.inventory.syncToMob(this.getEntity());
        }
    }

    @Override
    public void joinLevel() {
        if (inventory == null)
            inventory = INFFTamed.get(this.getEntity())
                .orElseThrow(() -> new IllegalCallerException("NFF Tamed Mob Inventory access on non-NFF mob."))
                .createAdditionalInventory();
    }

    @Override
    public @Nullable CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (this.inventory != null)
            nbt.put("inventory", inventory.toTag());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (inventory == null)
            inventory = INFFTamed.get(this.getEntity())
                .orElseThrow(() -> new IllegalCallerException("NFF Tamed Mob Inventory access on non-NFF mob."))
                .createAdditionalInventory();
        ListTag listTag = nbt.getList("inventory", Tag.TAG_COMPOUND);
        if (!listTag.isEmpty()) {
            inventory.fromTag(listTag);
        }
    }

    public void getInventory() {
        Optional.of(inventory).orElseGet(() ->
            NFFTamedMobInventory.createEmpty(INFFTamed.get(this.getEntity())
                .orElseThrow(() -> new IllegalCallerException("NFF Tamed Mob Inventory access on non-NFF mob."))));
    }
}
