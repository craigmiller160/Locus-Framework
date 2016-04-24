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

import java.util.Set;
import java.util.TreeSet;

/**
 * <p>A special class to store exclusion and inclusion
 * prefixes to test when scanning classes for methods.
 * Both are prefixes that the fully-qualified class
 * names are tested against to see if they should be
 * scanned into storage.</p>
 *
 * <p>Exclusions are prefixes to NOT scan into the
 * storage. By default, all standard Java SDK classes
 * are in the exclusions. This prevents methods inherited
 * from SDK classes from getting caught up by the scanner,
 * especially with the likelihood they would cause exceptions
 * due to the restrictions on identical methods.</p>
 *
 * <p>Inclusions are prefixes that SHOULD be scanned. Essentially
 * its an optional ability to override the exclusions and allow
 * certain classes through anyway.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
public class ScannerExclusions {

    /**
     * The default package prefixes to exclude. Basically the
     * entire standard java SDK.
     */
    private Set<String> defaultExclusions = new TreeSet<String>(){{
        add("java.");
        add("javax.");
        add("org.ietf");
        add("org.omg");
        add("org.w3c");
        add("org.xml");
        add("com.sun");
    }};

    /**
     * The exclusion prefixes.
     */
    private Set<String> exclusions = new TreeSet<>();

    /**
     * The inclusion prefixes.
     */
    private Set<String> inclusions = new TreeSet<>();

    /**
     * Create a new ScannerExclusions with the default
     * exclusion values.
     */
    public ScannerExclusions(){
        exclusions.addAll(defaultExclusions);
    }

    /**
     * Add a new prefix to be excluded.
     *
     * @param exclusion the prefix to be excluded.
     */
    public void addExclusion(String exclusion){
        exclusions.add(exclusion);
    }

    /**
     * Add a new prefix to be included.
     *
     * @param inclusion the prefix to be included.
     */
    public void addInclusion(String inclusion){
        inclusions.add(inclusion);
    }

    /**
     * Get all exclusion prefixes.
     *
     * @return all exclusion prefixes.
     */
    public Set<String> getAllExclusions(){
        return exclusions;
    }

    /**
     * Get all inclusion prefixes.
     *
     * @return all inclusion prefixes.
     */
    public Set<String> getAllInclusions(){
        return inclusions;
    }

    /**
     * Test if the provided class is allowed. There
     * are two tests performed. First, the fully
     * qualified class name is compared to the
     * exclusion prefixes. If it matches any of them,
     * then the second test is performed, and it is
     * compared to the inclusion prefixes. If a class
     * matches an exclusion, and doesn't match any inclusion,
     * it is NOT allowed. Otherwise, it is allowed.
     *
     * @param clazz the class to test.
     * @return true if the class is allowed.
     */
    public boolean isClassAllowed(Class<?> clazz){
        boolean allowed = true;
        String name = clazz.getName();
        for(String exclude : exclusions){
            if(name.startsWith(exclude) && !isIncluded(name)){
                allowed = false;
                break;
            }
        }
        return allowed;
    }

    /**
     * Test if the provided class name matches any
     * of the inclusion prefixes.
     *
     * @param name the class name.
     * @return true if it matches any of the inclusion prefixes.
     */
    private boolean isIncluded(String name){
        boolean included = false;
        for(String include : inclusions){
            if(name.startsWith(include)){
                included = true;
            }
        }
        return included;
    }

}
