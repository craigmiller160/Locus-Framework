package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.locus.util.ScannerExclusions;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by craig on 4/20/16.
 */
public class LocusClassScanner extends AbstractLocusScanner {

    private static final Logger logger = LoggerFactory.getLogger(LocusPackageScanner.class);

    LocusClassScanner(){}

    @Override
    public void scan(String itemToScan, LocusStorage storage) throws ReflectiveException {

    }

    @Override
    public void scan(String itemToScan, LocusStorage storage, ScannerExclusions scannerExclusions) throws ReflectiveException {

    }
}
