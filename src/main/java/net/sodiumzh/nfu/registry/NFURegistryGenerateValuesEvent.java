package net.sodiumzh.nfu.registry;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public abstract class NFURegistryGenerateValuesEvent extends Event {

    public final NFURegistry<?> registry;

    protected NFURegistryGenerateValuesEvent(NFURegistry<?> registry) {
        this.registry = registry;
    }

    /**
     * Posted right before a registry generates its values on common setup phase.
     */
    public static class CommonBefore extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public CommonBefore(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right after a registry generates its values on common setup phase.
     */
    public static class CommonAfter extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public CommonAfter(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on client setup phase.
     */
    public static class ClientBefore extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public ClientBefore(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right after a registry generates its values on client setup phase.
     */
    public static class ClientAfter extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public ClientAfter(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on server setup phase.
     */
    public static class ServerBefore extends NFURegistryGenerateValuesEvent {
        public ServerBefore(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right after a registry generates its values on server setup phase.
     */
    public static class ServerAfter extends NFURegistryGenerateValuesEvent {
        public ServerAfter(NFURegistry<?> registry) {
            super(registry);
        }
    }

}
