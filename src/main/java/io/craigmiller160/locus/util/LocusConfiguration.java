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

package io.craigmiller160.locus.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class store the configuration values for the
 * Locus Framework. It is constructed on application
 * startup by the ConfigurationReader and should be
 * preserved throughout the lifecycle of the application.
 *
 * None of these settings are dynamic. They are meant to be set
 * once and then accessed regularly throughout the lifecycle of
 * the application.
 *
 * Created by craig on 3/15/16.
 */
public class LocusConfiguration {

    /**
     * The list of names of packages to be scanned for annotated
     * classes.
     */
    private List<String> packageNames = new ArrayList<>();

    /**
     * The container for the ScannerExclusions used for validation
     * during package scanning.
     */
    private ScannerExclusions scannerExclusions = new ScannerExclusions();

    /**
     * Add the name of a package to the list of packages to be scanned for
     * annotated classes.
     *
     * @param packageName the name of the package to add to the list.
     */
    public void addPackageName(String packageName){
        packageNames.add(packageName);
    }

    /**
     * Remove the name of a package from the list of packages to
     * be scanned for annotated classes.
     *
     * @param packageName the name of the package to remove from the list.
     */
    public void removePackageName(String packageName){
        packageNames.remove(packageName);
    }

    /**
     * Get a count of how many packages to be scanned
     * for annotated classes there are.
     *
     * @return the number of packages to be scanned.
     */
    public int getPackageCount(){
        return packageNames.size();
    }

    /**
     * Get the list of the names of the packages to
     * be scanned for annotated classes.
     *
     * @return the list of package names.
     */
    public List<String> getPackageNames(){
        return packageNames;
    }

    /**
     * Add an exclusion prefix to the ScannerExclusions.
     *
     * @param exclusion the exclusion prefix.
     */
    public void addScannerExclusion(String exclusion){
        scannerExclusions.addExclusion(exclusion);
    }

    /**
     * Add an inclusion prefix to the ScannerExclusions.
     *
     * @param inclusion the inclusion prefix.
     */
    public void addScannerInclusion(String inclusion){
        scannerExclusions.addInclusion(inclusion);
    }

    /**
     * Get the ScannerExclusions for this configuration.
     *
     * @return the ScannerExclusions.
     */
    public ScannerExclusions getScannerExclusions(){
        return scannerExclusions;
    }

}
