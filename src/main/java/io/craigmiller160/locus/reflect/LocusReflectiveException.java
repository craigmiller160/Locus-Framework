package io.craigmiller160.locus.reflect;

import io.craigmiller160.locus.LocusException;

/**
 * An extension of LocusException to wrap and handle any
 * exceptions directly from the reflective operations of
 * this framework.
 *
 * Created by craig on 3/12/16.
 */
public class LocusReflectiveException extends LocusException{

    public LocusReflectiveException() {
    }

    public LocusReflectiveException(String message) {
        super(message);
    }

    public LocusReflectiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusReflectiveException(Throwable cause) {
        super(cause);
    }
}
