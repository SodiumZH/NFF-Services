package net.sodiumzh.nfu.exception;

/**
 * Thrown if some registration is required for some operation but the registration is missing.
 */
public class MissingRegistryException extends RuntimeException {

    public MissingRegistryException(String desc) {
        super(desc);
    }

    public MissingRegistryException(String desc, Exception reason) {
        super(desc, reason);
    }

}
