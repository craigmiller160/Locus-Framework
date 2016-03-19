package io.craigmiller160.locus.util;

import org.junit.Test;

/**
 * A JUnit test class for the LocusScannerImpl
 * class.
 *
 * Created by craig on 3/16/16.
 */
public class LocusScannerImplTest {

    @Test
    public void testScanPackage(){
        LocusScanner scanner = new LocusScannerImpl();
        String packageName = "io.craigmiller160.locus.sample";
        LocusStorage storage = new LocusStorage();

        scanner.scanPackage(packageName, storage);
    }

}
