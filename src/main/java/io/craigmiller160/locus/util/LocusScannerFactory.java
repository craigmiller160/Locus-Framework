package io.craigmiller160.locus.util;

/**
 * Created by craig on 3/16/16.
 */
public class LocusScannerFactory {

    public LocusScannerFactory() {
    }

    /**
     * Get a new instance of this factory class.
     *
     * @return a new instance of LocusScannerFactory.
     */
    public static LocusScannerFactory getInstance() {
        return new LocusScannerFactory();
    }

    public LocusScanner newLocusScanner(){
        return new LocusScannerImpl();
    }

}
