package net.sodiumzh.nfu.exception;

/**
 * Thrown if any NFU reflection utility encounters an exception.
 */
public class ReflectionFailedException extends RuntimeException {

    public ReflectionFailedException(String string) {
        super(string);
    }

    public ReflectionFailedException(Throwable t) {
        super(t);
    }

}
