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

    @Test
    public void testScanPackage(){
        LocusScanner scanner = new LocusScannerImpl();
        String packageName = "io.craigmiller160.locus.sample";
        LocusStorage storage = new LocusStorage();

        scanner.scanPackage(packageName, storage);

        MultiValueMap<String,ObjectAndMethod> modelPropSetters = storage.getAllModelPropSetters();
        MultiValueMap<String,ObjectAndMethod> modelPropGetters = storage.getAllModelPropGetters();
        Map<String,Class<?>> controllerTypes = storage.getAllControllerTypes();
        Map<String,Boolean> controllerSingletons = storage.getAllControllerSingletons();
        MultiValueMap<String,ClassAndMethod> viewPropSetters = storage.getAllViewPropSetters();
        MultiValueMap<String,ClassAndMethod> viewPropGetters = storage.getAllViewPropGetters();

        Set<String> modelSetterKeys = modelPropSetters.keySet();
        Set<String> modelGetterKeys = modelPropGetters.keySet();

        assertEquals("Wrong number of model setter props", modelSetterKeys.size(), 2);
        assertEquals("Wrong number of model getter props", modelGetterKeys.size(), 3);

        for(String key : modelSetterKeys){
            Collection<ObjectAndMethod> modelSetters = modelPropSetters.get(key);
            assertEquals("Wrong number of methods for model setter prop " + key, modelSetters.size(), 1);
        }

        for(String key : modelGetterKeys){
            Collection<ObjectAndMethod> modelGetters = modelPropGetters.get(key);
            assertEquals("Wrong number of methods for model getter prop " + key, modelGetters.size(), 1);
        }

        Set<String> controllerNames = controllerTypes.keySet();
        assertEquals("Wrong number of controller types", controllerNames.size(), 1);

        String name = controllerNames.iterator().next();
        assertFalse("Controller singleton value should be false", controllerSingletons.get(name));

        Set<String> viewSetterKeys = viewPropSetters.keySet();
        Set<String> viewGetterKeys = viewPropGetters.keySet();

        assertEquals("Wrong number of view setter props", viewSetterKeys.size(), 2);
        assertEquals("Wrong number of view getter props", viewGetterKeys.size(), 3);

        for(String key : viewSetterKeys){
            Collection<ClassAndMethod> viewSetters = viewPropSetters.get(key);
            assertEquals("Wrong number of methods for view setter prop " + key, viewSetters.size(), 1);
        }

        for(String key : viewGetterKeys){
            Collection<ClassAndMethod> viewGetters = viewPropGetters.get(key);
            assertEquals("Wrong number of methods for view getter prop " + key, viewGetters.size(), 1);
        }
    }

    @Test
    public void testScanInvalidModel(){
        LocusScanner scanner = new LocusScannerImpl();
        String package1 = "io.craigmiller160.locus.sample";
        String package2 = "io.craigmiller160.locus.othermodel";
        LocusStorage storage = new LocusStorage();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        try{
            scanner.scanPackage(package1, storage);
        }
        catch(LocusReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        try{
            scanner.scanPackage(package2, storage);
        }
        catch(LocusReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);

    }

    @Test
    public void testScanInvalidView(){
        LocusScanner scanner = new LocusScannerImpl();
        String package1 = "io.craigmiller160.locus.sample";
        String package2 = "io.craigmiller160.locus.otherview";
        LocusStorage storage = new LocusStorage();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        try{
            scanner.scanPackage(package1, storage);
        }
        catch(LocusReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        try{
            scanner.scanPackage(package2, storage);
        }
        catch(LocusReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);
    }

    @Test
    public void testScanInvalidController(){
        LocusScanner scanner = new LocusScannerImpl();
        String package1 = "io.craigmiller160.locus.sample";
        String package2 = "io.craigmiller160.locus.othercontroller";
        LocusStorage storage = new LocusStorage();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        try{
            scanner.scanPackage(package1, storage);
        }
        catch(LocusReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        try{
            scanner.scanPackage(package2, storage);
        }
        catch(LocusReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);
    }

}
