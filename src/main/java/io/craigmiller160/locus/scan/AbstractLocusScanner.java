/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.util.LocusStorage;

/**
 * <p>Abstract implementation of the LocusScanner
 * interface, providing helper methods for parsing.
 * The parsing methods use a separate, common class
 * for doing the actual paring operations, thus
 * increasing the flexibility of this class.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
public abstract class AbstractLocusScanner implements LocusScanner {

    /**
     * The ScanParser, encapsulating the logic used by all subclasses
     * to parse scanned classes and add them properly to the storage.
     */
    private ScanParser parser;

    /**
     * The constructor for LocusScanner implementations.
     */
    protected AbstractLocusScanner(){
        this.parser = new ScanParser();
    }

    /**
     * Parse a model class for any methods that should be added to the
     * LocusStorage. The ScanParser is used to perform the operations
     * herein.
     *
     * @param modelType the class type of the model class.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     */
    protected void parseModelClass(Class<?> modelType, LocusStorage storage, ScannerExclusions exclusions){
        parser.parseModelClass(modelType, storage, exclusions);
    }

    /**
     * Parse a controller class so that it can be added to the
     * LocusStorage. The ScanParser is used to perform the operations
     * herein.
     *
     * @param controllerType the class type of the controller class.
     * @param storage the LocusStorage.
     */
    protected void parseControllerClass(Class<?> controllerType, LocusStorage storage){
        parser.parseControllerClass(controllerType, storage);
    }

    /**
     * Parse a view class for any methods that should be added to the
     * LocusStorage. The ScanParser is used to perform the operations
     * herein.
     *
     * @param viewType the class type of the view class.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     */
    protected void parseViewClass(Class<?> viewType, LocusStorage storage, ScannerExclusions exclusions){
        parser.parseViewClass(viewType, storage, exclusions);
    }

}
