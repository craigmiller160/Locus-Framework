package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.locus.util.ScannerExclusions;

/**
 * Abstract implementation of the LocusScanner
 * interface, providing helper methods for parsing.
 * The parsing methods use a separate, common class
 * for doing the actual paring operations, thus
 * increasing the flexibility of this class.
 *
 * Created by craig on 4/20/16.
 */
public abstract class AbstractLocusScanner implements LocusScanner {

    private ScanParser parser;

    protected AbstractLocusScanner(){
        this.parser = new ScanParser();
    }

    protected void parseModelClass(Class<?> modelType, LocusStorage storage, ScannerExclusions exclusions){
        parser.parseModelClass(modelType, storage, exclusions);
    }

    protected void parseControllerClass(Class<?> controllerType, LocusStorage storage, String name, boolean singleton){
        parser.parseControllerClass(controllerType, storage, name, singleton);
    }

    protected void parseViewClass(Class<?> viewType, LocusStorage storage, ScannerExclusions exclusions){
        parser.parseViewClass(viewType, storage, exclusions);
    }

}
