package io.craigmiller160.locus.reflect;

import io.craigmiller160.locus.LocusException;

/**
 * A subclass of LocusException intended for wrapping
 * and handling non-reflective exceptions that occur
 * while reflectively accessing a method. Essentially
 * it's a non-checked version of Java's InvocationTargetException.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvocationException extends LocusException{

    //TODO ensure that making this unchecked doesn't lead to potential problems, since it'll be wrapping checked exceptions

    public LocusInvocationException() {
    }

    public LocusInvocationException(String message) {
        super(message);
    }

    public LocusInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusInvocationException(Throwable cause) {
        super(cause);
    }
}
