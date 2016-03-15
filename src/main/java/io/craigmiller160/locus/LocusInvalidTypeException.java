package io.craigmiller160.locus;

/**
 * An extension of LocusException for if object types
 * don't match up. This is primarily for the convenience
 * methods offered by this framework, with its wide variety
 * of types. Internal casting is frequently done, and if the
 * type found doesn't match the type expected, this exception
 * is thrown.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvalidTypeException extends LocusException {

    public LocusInvalidTypeException() {
    }

    public LocusInvalidTypeException(String message) {
        super(message);
    }

    public LocusInvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusInvalidTypeException(Throwable cause) {
        super(cause);
    }
}
