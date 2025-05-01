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
    public static class Common extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public Common(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on client setup phase.
     */
    public static class Client extends NFURegistryGenerateValuesEvent implements IModBusEvent {
        public Client(NFURegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on server setup phase.
     */
    public static class Server extends NFURegistryGenerateValuesEvent {
        public Server(NFURegistry<?> registry) {
            super(registry);
        }
    }

}
