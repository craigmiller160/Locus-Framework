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

import io.craigmiller160.locus.othermodel.ModelTwo;
import io.craigmiller160.locus.sample.ModelOne;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class to test the
 * functionality of ScannerExclusions.
 *
 * Created by craigmiller on 3/19/16.
 */
public class ScannerExclusionsTest {

    @Test
    public void testDefaultExclusions(){
        Class<?> javaClazz = String.class;
        Class<?> customClazz = ModelOne.class;

        ScannerExclusions scannerExclusions = new ScannerExclusions();
        assertFalse("Standard Java class passed the test", scannerExclusions.isClassAllowed(javaClazz));
        assertTrue("Custom class failed the test", scannerExclusions.isClassAllowed(customClazz));
    }

    @Test
    public void testAddedExclusion(){
        Class<?> javaClazz = String.class;
        Class<?> customClazz1 = ModelOne.class;
        Class<?> customClazz2 = ModelTwo.class;

        ScannerExclusions scannerExclusions = new ScannerExclusions();
        scannerExclusions.addExclusion("io.craigmiller160.locus.sample");
        assertFalse("Standard Java class passed the test", scannerExclusions.isClassAllowed(javaClazz));
        assertFalse("Custom class failed the test", scannerExclusions.isClassAllowed(customClazz1));
        assertTrue("Custom class passed the test", scannerExclusions.isClassAllowed(customClazz2));
    }

    @Test
    public void textInclusion(){
        Class<?> javaClazz = String.class;

        ScannerExclusions scannerExclusions = new ScannerExclusions();
        assertFalse("Standard Java class passed the test, pre-inclusion", scannerExclusions.isClassAllowed(javaClazz));

        scannerExclusions.addInclusion("java.lang");
        assertTrue("Standard Java class failed the test, post-inclusion", scannerExclusions.isClassAllowed(javaClazz));
    }

}
