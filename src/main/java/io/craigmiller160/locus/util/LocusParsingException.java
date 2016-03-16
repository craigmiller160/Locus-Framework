package io.craigmiller160.locus.util;

import io.craigmiller160.locus.LocusException;

/**
 * A special exception for if an error occurs while trying to
 * parse the configuration file.
 *
 * Created by craig on 3/15/16.
 */
public class LocusParsingException extends LocusException {

    public LocusParsingException() {
    }

    public LocusParsingException(String message) {
        super(message);
    }

    public LocusParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusParsingException(Throwable cause) {
        super(cause);
    }
}
