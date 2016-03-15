package io.craigmiller160.locus;

/**
 * A special exception superclass for wrapping all exceptions
 * thrown by this framework. It is a RuntimeException because
 * exceptions thrown by this framework should not be handled
 * by the application, and signal issues that the developer
 * should deal with.
 *
 * Created by craig on 3/12/16.
 */
public class LocusException extends RuntimeException {

    public LocusException() {
    }

    public LocusException(String message) {
        super(message);
    }

    public LocusException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusException(Throwable cause) {
        super(cause);
    }
}
