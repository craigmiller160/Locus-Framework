/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    protected void parseControllerClass(Class<?> controllerType, LocusStorage storage){
        parser.parseControllerClass(controllerType, storage);
    }

    protected void parseViewClass(Class<?> viewType, LocusStorage storage, ScannerExclusions exclusions){
        parser.parseViewClass(viewType, storage, exclusions);
    }

}
