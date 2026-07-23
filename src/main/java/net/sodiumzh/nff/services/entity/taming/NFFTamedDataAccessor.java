package net.sodiumzh.nff.services.entity.taming;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.registry.NFFEntityComponents;
import net.sodiumzh.nfu.annotation.DontCallManually;
import net.sodiumzh.nfu.entity.component.EntityComponentAPI;
import net.sodiumzh.nfu.function.MutablePredicate;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A proxy of entity components for NFF tamed mobs.
 */
public class NFFTamedDataAccessor {

    protected static NFFTamedDataAccessor INSTANCE = null;

    protected INFFTamed tamed;

    protected NFFTamedDataAccessor(INFFTamed tamed) {
        this.tamed = tamed;
    }

    public static NFFTamedDataAccessor get(INFFTamed tamed) {
        if (INSTANCE == null) INSTANCE = new NFFTamedDataAccessor(tamed);
        INSTANCE.tamed = tamed;
        return INSTANCE;
    }

    public INFFTamed getOwner() {
        return this.tamed;
    }

    protected void setOwner(INFFTamed tamed) {
        this.tamed = tamed;
    }

    public NFFTamedDataComponent getDataComponent() {
        return EntityComponentAPI.getComponentManager(this.tamed.asMob())
            .getSubComponentByPath(NFFEntityComponents.PATH_TAMED_DATA, NFFEntityComponents.TAMED_DATA.get()).orElse(NFFEntityComponents.TAMED_DATA.get().create(this.tamed.asMob()));
    }

    public NFFTamedSyncherComponent getSyncherComponent() {
        return EntityComponentAPI.getComponentManager(this.tamed.asMob())
            .getSubComponentByPath(NFFEntityComponents.PATH_TAMED_SYNCHER, NFFEntityComponents.TAMED_SYNCHER.get()).orElse(NFFEntityComponents.TAMED_SYNCHER.get().create(this.tamed.asMob()));
    }

    public NFFTamedInventoryComponent getInventoryComponent() {
        return EntityComponentAPI.getComponentManager(this.tamed.asMob())
            .getSubComponentByPath(NFFEntityComponents.PATH_TAMED_INVENTORY, NFFEntityComponents.TAMED_INVENTORY.get()).orElse(NFFEntityComponents.TAMED_INVENTORY.get().create(this.tamed.asMob()));
    }

    // General //

    /** Get the befriended mob owning this data. */
    public INFFTamed asTamed() {
        return tamed;
    }

    /** Get sun immunity. */
    public MutablePredicate<INFFTamed> getSunImmunity() {
        return this.getDataComponent().getSunImmunity();
    }

    /** Get temporary object from a key from table. Temporary object table is a non-serialized object table to store any objects,
     * not directly accessible but only with {@code getTempObject}, {@code addTempObject} and {@code removeTempObject}.
     * @return Object if present, or null if not.
     */
    @Nullable
    public Object getTransientVariable(String key) {
        return this.getDataComponent().getVariable(key);
    }

    /** Add a temporary object to table. Temporary object table is a non-serialized object table to store any objects.*/
    public void addTransientVariable(String key, Object obj) {
        this.getDataComponent().putTransientVariable(key, obj);
    }

    /** Remove a temporary object from table. Temporary object table is a non-serialized object table to store any objects.
     * If the key is absent, it will not do anything.
     */
    public void removeTransientVariable(String key) {
        this.getDataComponent().putTransientVariable(key, null);
    }

    /**
     * Force access a value with casted class.
     * If it's absent, return null. If class mismatches, log error and return null.
     * @return Value with casted class, or null if absent or class mismatching.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getTransientVariableCast(String key)
    {
        Object obj = this.getTransientVariable(key);
        if (obj == null) return null;
        try {
            return (T) obj;
        }
        catch (ClassCastException e) {
            LogUtils.getLogger().error("CNFFTamedCommonData#getTempObjectCasted: class cast mismatch found.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the UUID identifier of this mob. (Independent to the entity UUID.
     * This is generated on befriended for identifying a mob even if it has respawned with a new UUID.)
     */
    public UUID getIdentifier() {
        return this.getSyncherComponent().getIdentifier();
    }

    /**
     * Generate UUID identifier of this mob. (Independent to the entity UUID.
     * This is generated on befriended for identifying a mob even if it has respawned with a new UUID.)
     */
    @ApiStatus.Internal
    public void generateIdentifier() {
        this.getSyncherComponent().generateIdentifier();
    }

    /**
     * Get the registry key of the entity type of this mob on befriended.
     * <p>
     * This allows to read the mob's "initial" type after it converts to other
     * types somehow.
     */
    public EntityType<? extends Mob> getInitialEntityType() {
        return this.getDataComponent().getInitialEntityType();
    }

    /**
     * Record the registry key of the entity type of this mob on befriended.
     * <p>
     * This allows to read the mob's "initial" type after it converts to other
     * types somehow.
     */
    public void recordEntityType() {
        this.getDataComponent().recordEntityType();
    }

    // Owner info related //

    /**
     * Get the owner's display name, or "(Unknown)" if not found.
     */
    public String getOwnerName() {
        return this.getSyncherComponent().getOwnerName();
    }

    /**
     * Set the owner's display name stored.
     */
    public void setOwnerName(String val) {
        this.getSyncherComponent().setOwnerName(val);
    }

    /**
     * Get the date player encountered it, including befriended, or took ownership from another player.
     * @since 0.x.20
     * @return An int[3] indicating year, month and day, or (0,0,0) if not recorded (legacy).
     */
    @Nullable
    public int[] getEncounteredDate() {
        return this.getSyncherComponent().getEncounteredDate();
    }

    /**
     * Record info about befriending time and location. Invoked in {@link NFFTamingProcess#doTaming}.
     */
    public void setEncounteredDate(int[] val) {
        this.getSyncherComponent().setEncounteredDate(val);
    }

    public void recordEncounteredDate()
    {
        LocalDate now = LocalDate.now();
        int[] date = new int[] {now.get(ChronoField.YEAR), now.get(ChronoField.MONTH_OF_YEAR), now.get(ChronoField.DAY_OF_MONTH)};
        this.setEncounteredDate(date);
    }

    public UUID getOwnerUUID() {
        return this.getSyncherComponent().getOwnerUUID();
    }

    public void setOwnerUUID(UUID value) {
        this.getSyncherComponent().setOwnerUUID(value);
    }

    // Behavior related //

    /**
     * Get random stroll anchor point as vector.
     */
    public Vec3 getAnchor() {
        return this.getDataComponent().getRandomStrollAnchor();
    }

    /**
     * Set random stroll anchor point.
     */
    public void setAnchor(Vec3 anchor) {
        this.getDataComponent().setRandomStrollAnchor(anchor);
    }

    public NFFTamedMobAIState getAIState() {
        return this.getSyncherComponent().getAIState();
    }

    public void setAIState(NFFTamedMobAIState state) {
        this.getSyncherComponent().setAIState(state);
    }

    /**
     * Get the attack target. Same to {@link Mob#getTarget()} on server. On client,
     * it will be read from the synched getter cache.
     */
    @Nullable
    public LivingEntity getAttackTarget() {
        return this.getSyncherComponent().getAttackTarget();
    }

    /**
     * <b> Don't call manually! </b> This method is only called in {@link INFFTamed#getPreviousTarget}.
     */
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    @Nullable
    public LivingEntity getPreviousTarget() {
        return this.getDataComponent().getPreviousTarget();
    }

    /**
     * <b> Don't call manually! </b> This method is only called in {@link INFFTamed#setPreviousTarget}.
     */
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    public void setPreviousTarget(@Nullable LivingEntity target) {
        this.getDataComponent().setPreviousTarget(target);
    }

    // Inventory related //

    public NFFTamedMobInventory getAdditionalInventory() {
        return this.getInventoryComponent().get();
    }

    // Synched Data related //

    /**
     * Create a synched data. Synched data must be created before other any operations.
     * @param <T>Data class. Exactly same to the data class in {@code dataSerialzier}.
     * @param key Data key as string.
     * @param dataSerialzier Data serializer applied.
     * @param initValue Default value if not set.
     */
    public <T> void createSynchedData(String key, NFUDataSerializer<T> dataSerialzier, T initValue) {
        this.getSyncherComponent().createSynchedData(key, dataSerialzier, initValue, true);
    }

    public <T> boolean hasSynchedData(String key, Class<T> dataType) {
        return this.getSyncherComponent().hasSynchedData(key, dataType);
    }

    public <T> Optional<T> getSynchedData(String key, Class<T> dataClass) {
        return this.getSyncherComponent().getSynchedData(key, dataClass);
    }

    public <T> Optional<T> getSynchedDataUnchecked(String key) {
        return this.getSyncherComponent().getSynchedDataUnchecked(key);
    }

    /**
     * Set synched data value. Only on server.
     * @param key Synched data key
     * @param dataClass Expected data class. <i>This is a salt to ensure you know what class this field expects to receive.</i>
     * @param value New value.
     */
    public <T> void setSynchedData(String key, Class<T> dataClass, T value) {
        this.getSyncherComponent().setSynchedData(key, dataClass, value);
    }

    public void setSynchedDataClient(String key, NFUDataSerializer<?> serializer, Object value) {
        this.getSyncherComponent().setSynchedData(key, (Class<Object>)value.getClass(), value);
    }

    /**
     * Define a synched getter. Synched getters get from a {@link Supplier} every tick from server and store it on client.
     * They are not saved into data.
     * <p>When a field is accessed on the client, it will read the cache value synched from server (if no synching happened,
     * it's the default value).
     */
    @Deprecated
    public <T> void createSynchedGetter(String key, NFUDataSerializer<T> serializer, @Nonnull T defaultValue, Function<Mob, T> accessorOnServer) {
        this.getSyncherComponent().createSynchedGetter(key, serializer, defaultValue, accessorOnServer);
    }

    /**
     * Define a synched getter. Synched getters get from a {@link Supplier} every tick from server and store it on client.
     * They are not saved into data.
     * <p>When a field is accessed on the client, it will read the cache value synched from server (if no synching happened,
     * it's the default value).
     */
    @Deprecated
    public <T> void createSynchedGetter(String key, NFUDataSerializer<T> serializer, @Nonnull T defaultValue, Supplier<T> accessorOnServer) {
        this.getSyncherComponent().createSynchedGetter(key, serializer, defaultValue, e -> accessorOnServer.get());
    }

    /**
     * Get synched getter from key and type.
     * <p>Safe to call on both sides. On server, it will be directly accessed by the supplier,
     * and on client it will be read from the cached field which is updated on synching.
     * <p>Note: this includes an unsafe casting. Double-check the type before using.
     * Return {@code null} if not present.
     */
    public <T> T getSynchedGetter(String key, Class<T> type) {
        return this.getSyncherComponent().getSynchedGetter(key, type).orElse(null);
    }

    /**
     * Get a synched getter as a raw {@link Object}.
     * <p>Safe to call on both sides. On the main side, it will be directly accessed by the supplier,
     * and on the synched side it will be read from the cached field which is updated on synching.
     * <p> Null if the key doesn't exist.
     */
    @Nullable
    public Object getSynchedGetter(String key) {
        return this.getSyncherComponent().getSynchedGetter(key).orElse(null);
    }


    /**
     * Set how many ticks to do a sync
     */
    public void setSyncInterval(int ticks) {
        this.getSyncherComponent().setSyncInterval(ticks);
    }

}
