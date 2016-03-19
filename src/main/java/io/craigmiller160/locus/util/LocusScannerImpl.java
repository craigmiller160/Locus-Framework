package io.craigmiller160.locus.util;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by craig on 3/12/16.
 */
public class LocusScannerImpl implements LocusScanner{

    private static final Logger logger = LoggerFactory.getLogger(LocusScannerImpl.class);

    LocusScannerImpl(){}

    @Override
    public void scanPackage(String packageName, LocusStorage storage) {
        logger.debug("Scanning package \"" + packageName + "\" for annotated classes");
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName));
    }


}
