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

/**
 * <p>A factory class that produces instances
 * of LocusScanner implementations.</p>
 *
 * @author craigmiller
 * @version 1.1
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
