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

}
