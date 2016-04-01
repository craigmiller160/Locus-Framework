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

import io.craigmiller160.locus.reflect.ClassAndMethod;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.sample.ModelOne;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class for the LocusScannerImpl
 * class.
 *
 * Created by craig on 3/16/16.
 */
public class LocusScannerImplTest {

    private static final Logger logger = LoggerFactory.getLogger(LocusScannerImplTest.class);

    /**
     * Test basic package scanning, and what classes it retrieves,
     * using the sample package.
     */
    @Test
    public void testScanPackage(){
        LocusScanner scanner = new LocusScannerImpl();
        String packageName = "io.craigmiller160.locus.sample";
        LocusStorage storage = new LocusStorage();
        ScannerExclusions scannerExclusions = new ScannerExclusions();

        scanner.scanPackage(packageName, storage, scannerExclusions);

        Map<String,ObjectAndMethod> modelPropSetters = storage.getAllModelPropSetters();
        Map<String,ObjectAndMethod> modelPropGetters = storage.getAllModelPropGetters();
        Map<String,Class<?>> controllerTypes = storage.getAllControllerTypes();
        Map<String,Boolean> controllerSingletons = storage.getAllControllerSingletons();
        MultiValueMap<String,ClassAndMethod> viewPropSetters = storage.getAllViewPropSetters();

        Set<String> modelSetterKeys = modelPropSetters.keySet();
        Set<String> modelGetterKeys = modelPropGetters.keySet();

        assertEquals("Wrong number of model setter props", modelSetterKeys.size(), 11);
        assertEquals("Wrong number of model getter props", modelGetterKeys.size(), 11);

        Set<String> controllerNames = controllerTypes.keySet();
        assertEquals("Wrong number of controller types", controllerNames.size(), 1);

        String name = controllerNames.iterator().next();
        assertFalse("Controller singleton value should be false", controllerSingletons.get(name));

        Set<String> viewSetterKeys = viewPropSetters.keySet();

        assertEquals("Wrong number of view setter props", viewSetterKeys.size(), 13);

        //TODO this will need to be revamped once all the new methods are added
        for(String key : viewSetterKeys){
            Collection<ClassAndMethod> viewSetters = viewPropSetters.get(key);
            if(key.equals("FirstField")){
                assertEquals("Wrong number of methods for view setter prop " + key, viewSetters.size(), 2);
            }
            else if(key.equals("SecondField")){
                assertEquals("Wrong number of methods for view setter prop " + key, viewSetters.size(), 1);
            }
        }
    }

    /**
     * Test package scanning with invalid model methods,
     * which should result in an exception.
     */
    @Test
    public void testScanInvalidModel(){
        LocusScanner scanner = new LocusScannerImpl();
        String package1 = "io.craigmiller160.locus.sample";
        String package2 = "io.craigmiller160.locus.othermodel";
        LocusStorage storage = new LocusStorage();

        ScannerExclusions scannerExclusions = new ScannerExclusions();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        //Scanning the first package should work with no exception, because everything is valid
        try{
            scanner.scanPackage(package1, storage, scannerExclusions);
        }
        catch(LocusReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        //Scanning the second package should find an invalid, duplicate method, and throw an exception
        try{
            scanner.scanPackage(package2, storage, scannerExclusions);
        }
        catch(LocusReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);

    }

    /**
     * Test scanning with invalid controller names, which
     * should result in an exception.
     */
    @Test
    public void testScanInvalidController(){
        LocusScanner scanner = new LocusScannerImpl();
        String package1 = "io.craigmiller160.locus.sample";
        String package2 = "io.craigmiller160.locus.othercontroller";
        LocusStorage storage = new LocusStorage();

        ScannerExclusions scannerExclusions = new ScannerExclusions();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        //Scanning the first package should work with no exception because there's no name conflict
        try{
            scanner.scanPackage(package1, storage, scannerExclusions);
        }
        catch(LocusReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        //Scanning the second package should throw an exception because of a duplicate name
        try{
            scanner.scanPackage(package2, storage, scannerExclusions);
        }
        catch(LocusReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);
    }

}
