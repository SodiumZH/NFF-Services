package net.sodiumzh.nfu.exception;

/**
 * Showing that the generics type declaration or usage is somehow wrong.
 */
public class IllegalGenericsException extends RuntimeException {

    public IllegalGenericsException(String msg) {
        super(msg);
    }

    public IllegalGenericsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IllegalGenericsException(Throwable cause) {
        super(cause);
    }
}
