package net.sodiumzh.nfu.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An {@code EntityTimerAccessor} is used for accessing a timer of unspecified {@link CEntityTimerCapability}-carrying
 * entities with a given key. It allows to access the timer without explicit strings to simplify the code,
 * preventing accident string error and repeated {@code getCapability} calls.
 * <p>Note: The accessor is singleton for each combination of capability and key. To get the
 * accessor instance, use {@link EntityTimerAccessor#get} method which gets the accessor if it's present, or create
 * if absent.
 * <p>Note: It's recommended to <u>only use this as static fields</u>. Get the accessor instance as non-static field
 * is allowed, but <u>do never call {@EntityTimerAccessor#addOnExpireAction} as it will add an </u>
 */
@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityTimerAccessor {

    private static final List<EntityTimerAccessor> ALL_ACCESSORS = new ArrayList<>();

    private final String key;
    private final Capability<? extends CEntityTimerCapability<?>> cap;
    private final List<Consumer<Entity>> expireActions = new ArrayList<>();

    private EntityTimerAccessor(@Nonnull String key, Capability<? extends CEntityTimerCapability<?>> cap) {
        this.key = key;
        this.cap = (Capability<? extends CEntityTimerCapability<?>>)cap;
        ALL_ACCESSORS.add(this);
    }

    /**
     * Gets the accessor for a combination of timer capability and key, or create if absent.
     */
    public static EntityTimerAccessor get(@Nonnull String key, Capability<? extends CEntityTimerCapability<?>> cap) {
        for (EntityTimerAccessor accessor: ALL_ACCESSORS) {
            if (accessor.key.equals(key) && accessor.cap.equals(cap))
                return accessor;
        }
        return new EntityTimerAccessor(key, cap);
    }

    private Optional<CEntityTimerCapability<?>> getCap(Entity inst) {
        return inst.getCapability(cap).resolve().map(cap -> ((CEntityTimerCapability<?>)cap));
    }

    public String getKey() {return key;}

    /**
     * Get the timer remaining time of this key for a given entity instance. Returns 0 if the entity doesn't have
     * this accessor's timer capability.
     */
    public int getRemainingTime(Entity inst) {
        return getCap(inst).map(c -> c.getTimerRemainingTime(key)).orElse(0);
    }

    /**
     * Set the timer remaining time of this key for a given entity instance. No action if the entity doesn't have
     * this accessor's timer capability.
     */
    public void setTimer(Entity inst, int val) {
        getCap(inst).ifPresent(c -> c.setTimer(key, val));
    }

    /**
     * Remove the timer remaining time of this key for a given entity instance. No action if the entity doesn't have
     * this accessor's timer capability.
     */
    public void removeTimer(Entity inst, boolean postEvent) {
        getCap(inst).ifPresent(c -> c.removeTimer(key, postEvent));
    }

    public boolean hasTimer(Entity inst) {
        return this.getRemainingTime(inst) != 0;
    }

    /**
     * Add an action to invoke on this accessor's timer expiring.
     * <p>Warning: <u>Never call this except in the static field initialization!</u> Non-static call will cause an exception.
     * This method will change the global instance. Running this method in non-static code will cause a rick of adding
     * duplicate actions, which make the same action run multiple times, wasting resource and causing unexpected behaviors.
     */
    public EntityTimerAccessor addOnExpireAction(Consumer<Entity> action) {
        if (!isCalledFromStaticInitializer())
            throw new IllegalStateException("NFU#EntityTimerAccessor#addOnExpireAction can only be called in static initialization.");
        expireActions.add(action);
        return this;

    }

    /**
     * Add an action to invoke on this accessor's timer expiring. Base entity class is specified to allow subclass consumers.
     */
    public <T extends Entity> EntityTimerAccessor addOnExpireAction(Class<T> baseClass, Consumer<? super T> action) {
        return addOnExpireAction(e -> {
            if (baseClass.isAssignableFrom(e.getClass())) action.accept((T)e);
        });
    }

    /**
     * Check if the operation is invoked in static code. Used to check {@code addOnExpireAction},
     */
    private boolean isCalledFromStaticInitializer() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if ("<clinit>".equals(element.getMethodName())) {
                return true;  // It's inside a static block or static field initialization
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void notifyExpire(CEntityTimerCapability.ExpireEvent event) {
       for (EntityTimerAccessor accessor: ALL_ACCESSORS) {
           var cap = event.getCapability();
           Entity e = cap.getEntity();
           if (cap.equals(accessor.getCap(e).orElse(null)) && event.getKey().equals(accessor.key)) {
               accessor.expireActions.forEach(action -> action.accept(e));
           }
       }
    }
}
