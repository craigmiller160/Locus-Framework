package io.craigmiller160.locus.util;

import io.craigmiller160.locus.reflect.LocusReflectiveException;

/**
 * Created by craig on 3/16/16.
 */
public interface LocusScanner {

    String DEFAULT_CONFIG = "locus.xml";

    void scanPackage(String packageName, LocusStorage storage) throws LocusReflectiveException;

}
