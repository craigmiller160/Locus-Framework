package io.craigmiller160.locus.scan;

/**
 * A factory class that produces instances
 * of LocusScanner implementations.
 *
 * Created by craig on 3/16/16.
 */
public class LocusScannerFactory {

    private LocusScannerFactory() {}

    /**
     * Get a new instance of the LocusScanner interface
     * for scanning packages.
     *
     * @return a scanner for packages.
     */
    public static LocusScanner newPackageScanner(){
        return new LocusPackageScanner();
    }

    /**
     * Get a new instance of the LocusScanner interface
     * for scanning classes.
     *
     * @return a scanner for classes.
     */
    public static LocusScanner newClassScanner(){
        return new LocusClassScanner();
    }

}
