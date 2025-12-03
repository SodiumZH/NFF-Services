package net.sodiumzh.nff.services.entity.taming;

import net.sodiumzh.nfu.entity.MobApplicableItemTable;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Labels that the process can optionally use a {@link MobApplicableItemTable}
 * to decide items.
 */
public interface IItemTableUsingProcess<T extends NFFTamingProcess> {

    public default T getProcess() {
        return (T) this;
    }

    /**
     * Returns an accessor of a {@link MobApplicableItemTable} to show this process
     * is using a {@link MobApplicableItemTable} for finding the items and delta progress.
     * Null return means the process doesn't use a MAIT but another way to decide
     * items.
     */
    @Nullable
    public Supplier<MobApplicableItemTable> getItemGivingTableOverride();

    /**
     * Set the accessor of {@link MobApplicableItemTable} if this process should use one.
     * Set null to disable it.
     */
    public T setItemGivingTableOverride(@Nullable Supplier<MobApplicableItemTable> override);

}
