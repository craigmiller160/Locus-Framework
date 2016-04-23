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
 * A special class to store exclusion
 * prefixes for the Scanner. It also
 * has a method to determine if a
 * class falls into one of those
 * exclusions.
 *
 * Created by craigmiller on 3/19/16.
 */
public class ScannerExclusions {

    /*
     * List of default exclusions
     * List of all exclusions
     * List of inclusions
     * Utility method to test if a class is excluded
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

    private Set<String> exclusions = new TreeSet<>();
    private Set<String> inclusions = new TreeSet<>();

    public ScannerExclusions(){
        exclusions.addAll(defaultExclusions);
    }

    public void addExclusion(String exclusion){
        exclusions.add(exclusion);
    }

    public void addInclusion(String inclusion){
        inclusions.add(inclusion);
    }

    public Set<String> getAllExclusions(){
        return exclusions;
    }

    public Set<String> getAllInclusions(){
        return inclusions;
    }

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
