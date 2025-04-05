package net.sodiumzh.nautils.registries;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public abstract class NaUtilsRegistryGenerateValuesEvent extends Event {

    public final NaUtilsRegistry<?> registry;

    protected NaUtilsRegistryGenerateValuesEvent(NaUtilsRegistry<?> registry) {
        this.registry = registry;
    }

    /**
     * Posted right before a registry generates its values on common setup phase.
     */
    public static class Common extends NaUtilsRegistryGenerateValuesEvent implements IModBusEvent {
        public Common(NaUtilsRegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on client setup phase.
     */
    public static class Client extends NaUtilsRegistryGenerateValuesEvent implements IModBusEvent {
        public Client(NaUtilsRegistry<?> registry) {
            super(registry);
        }
    }

    /**
     * Posted right before a registry generates its values on server setup phase.
     */
    public static class Server extends NaUtilsRegistryGenerateValuesEvent {
        public Server(NaUtilsRegistry<?> registry) {
            super(registry);
        }
    }

}
