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
import io.craigmiller160.utils.reflect.ReflectiveException;

/**
 * <p>The interface defining the API for scanning elements
 * in the Locus framework.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
public interface LocusScanner {

    /**
     * Scan an item denoted by the provided String name, and
     * update the provided LocusStorage instance.
     *
     * @param itemToScan the path of the item to scan.
     * @param storage the LocusStorage to update.
     * @throws ReflectiveException if unable to reflectively scan the item.
     */
    void scan(String itemToScan, LocusStorage storage) throws ReflectiveException;

    /**
     * Scan an item denoted by the provided String name, and
     * update the provided LocusStorage instance. To ensure that
     * nothing invalid is scooped up, ScannerExclusions are provided.
     *
     * @param itemToScan the path of the item to scan.
     * @param storage the LocusStorage to update.
     * @param scannerExclusions the ScannerExclusions.
     * @throws ReflectiveException if unable to reflectively scan the item.
     */
    void scan(String itemToScan, LocusStorage storage, ScannerExclusions scannerExclusions) throws ReflectiveException;

}
