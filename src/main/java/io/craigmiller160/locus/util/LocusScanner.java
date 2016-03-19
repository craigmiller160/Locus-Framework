package io.craigmiller160.locus.util;

/**
 * Created by craig on 3/16/16.
 */
public interface LocusScanner {

    public static final String DEFAULT_CONFIG = "locus.xml";

    void scanPackage(String packageName, LocusStorage storage);

}
