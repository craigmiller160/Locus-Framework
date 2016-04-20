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

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.locus.util.ScannerExclusions;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
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

    private LocusStorage storage;

    /**
     * Get a LocusStorage instance for use in tests. It's created reflectively
     * because there's no access to its constructor normally.
     *
     * @throws RuntimeException if unable to create the LocusStorage.
     */
    private void setupStorage(){
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
            storage.setUIThreadExecutorType(NoUIThreadExecutor.class);
        }
        catch(Exception ex){
            throw new RuntimeException("Unable to reflectively create LocusStorage for test", ex);
        }
    }

    @Before
    public void before(){
        setupStorage();
    }

    /**
     * Test basic package scanning, and what classes it retrieves,
     * using the sample package.
     */
    @Test
    public void testScanPackage(){
        LocusScanner scanner = new LocusScannerImpl();
        String packageName = "io.craigmiller160.locus.sample";
        ScannerExclusions scannerExclusions = new ScannerExclusions();

        scanner.scan(packageName, storage, scannerExclusions);

        Collection<ObjectAndMethod> modelPropSetters = storage.getAllModelPropSetters();
        Collection<ObjectAndMethod> modelPropGetters = storage.getAllModelPropGetters();
        Collection<Class<?>> controllerTypes = storage.getAllControllerTypes();

        assertEquals("Wrong number of model setter props", 12, modelPropSetters.size());
        assertEquals("Wrong number of model getter props", 12, modelPropGetters.size());

        Set<String> controllerNames = storage.getAllControllerNames();
        assertEquals("Wrong number of controller types", 1, controllerTypes.size());

        String name = controllerNames.iterator().next();
        assertFalse("Controller singleton value should be false", storage.isControllerSingleton(name));

        Set<String> viewProps = storage.getAllViewPropNames();

        assertEquals("Wrong number of view setter props", 13, viewProps.size());

        //TODO this will need to be revamped once all the new methods are added
        for(String key : viewProps){
            Collection<ClassAndMethod> viewSetters = storage.getSettersForViewProp(key);
            if(key.equals("FirstField")){
                assertEquals("Wrong number of methods for view setter prop " + key, 2, viewSetters.size());
            }
            else if(key.equals("SecondField")){
                assertEquals("Wrong number of methods for view setter prop " + key, 1, viewSetters.size());
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

        ScannerExclusions scannerExclusions = new ScannerExclusions();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        //Scanning the first package should work with no exception, because everything is valid
        try{
            scanner.scan(package1, storage, scannerExclusions);
        }
        catch(ReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        //Scanning the second package should find an invalid, duplicate method, and throw an exception
        try{
            scanner.scan(package2, storage, scannerExclusions);
        }
        catch(ReflectiveException ex){
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

        ScannerExclusions scannerExclusions = new ScannerExclusions();

        //After the two scans, the first boolean should still be false, the second should be true
        boolean firstScanException = false;
        boolean secondScanException = false;

        //Scanning the first package should work with no exception because there's no name conflict
        try{
            scanner.scan(package1, storage, scannerExclusions);
        }
        catch(ReflectiveException ex){
            firstScanException = true;
            logger.error("First Scan Stack Trace", ex);
        }

        //Scanning the second package should throw an exception because of a duplicate name
        try{
            scanner.scan(package2, storage, scannerExclusions);
        }
        catch(ReflectiveException ex){
            secondScanException = true;
            logger.error("Second Scan Stack Trace", ex);
        }

        assertFalse("The first scan threw an exception, it shouldn't have", firstScanException);
        assertTrue("The second scan did not throw an exception, it should have", secondScanException);
    }

}