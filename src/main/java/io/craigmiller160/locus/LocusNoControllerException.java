package io.craigmiller160.locus;

/**
 * An extension of the LocusException for if a
 * controller requested is not available, either
 * because it doesn't exist or because the developer
 * didn't configure it properly.
 *
 * Created by craig on 3/12/16.
 */
public class LocusNoControllerException extends LocusException{

    public LocusNoControllerException() {
    }

    public LocusNoControllerException(String message) {
        super(message);
    }

    public LocusNoControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusNoControllerException(Throwable cause) {
        super(cause);
    }
}
