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

import io.craigmiller160.locus.TestUtils;
import io.craigmiller160.locus.annotations.LController;
import io.craigmiller160.locus.sample.ControllerOne;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A JUnit test class to test the LocusClassScanner
 * class.
 *
 * Created by craigmiller on 4/21/16.
 */
public class LocusClassScannerTest {

    private static final String MODEL_ONE_PATH = "io.craigmiller160.locus.sample.ModelOne";
    private static final String VIEW_ONE_PATH = "io.craigmiller160.locus.sample.ViewOne";
    private static final String VIEW_THREE_PATH = "io.craigmiller160.locus.sample.ViewThree";
    private static final String CONTROLLER_ONE_PATH = "io.craigmiller160.locus.sample.ControllerOne";

    private LocusStorage storage;
    private LocusClassScanner scanner;
    private ScannerExclusions exclusions = new ScannerExclusions();

    @Before
    public void before(){
        storage = TestUtils.setupStorage();
        scanner = new LocusClassScanner();
    }

    /**
     * A test of the class scanner performance. This is
     * generally commented out because it doesn't provide
     * any valuable JUnit testing results, and instead
     * is just used at times to test how long the process
     * takes to run.
     */
//    @Test
//    public void performanceTest(){
//        long start = System.currentTimeMillis();
//
//        scanner.scan(MODEL_ONE_PATH, storage, exclusions);
//        scanner.scan(VIEW_ONE_PATH, storage, exclusions);
//        scanner.scan(VIEW_THREE_PATH, storage, exclusions);
//        scanner.scan(CONTROLLER_ONE_PATH, storage, exclusions);
//
//        long end = System.currentTimeMillis();
//
//        System.out.println("Time: " + (end - start));
//    }

    @Test
    public void testScanModelClass(){
        scanner.scan(MODEL_ONE_PATH, storage, exclusions);
        int modelPropSetterCount = storage.getModelPropSetterCount();
        int modelPropGetterCount = storage.getModelPropSetterCount();
        int modelPropAdderCount = storage.getModelPropAdderCount();
        int modelPropRemoverCount = storage.getModelPropRemoverCount();

        assertEquals("Wrong number of model prop setters", 13, modelPropSetterCount);
        assertEquals("Wrong number of model prop getters", 13, modelPropGetterCount);
        assertEquals("Wrong number of model prop removers", 1, modelPropRemoverCount);
        assertEquals("Wrong number of model prop adders", 1, modelPropAdderCount);
    }

    @Test
    public void testScanViewClass(){
        scanner.scan(VIEW_ONE_PATH, storage, exclusions);
        int viewPropSetterCount = storage.getViewPropSetterCount();
        int viewPropAdderCount = storage.getViewPropAdderCount();
        int viewPropRemoverCount = storage.getViewPropRemoverCount();

        assertEquals("Wrong number of view prop setters", 13, viewPropSetterCount);
        assertEquals("Wrong number of view prop adders", 1, viewPropAdderCount);
        assertEquals("Wrong number of view prop removers", 1, viewPropRemoverCount);
    }

    @Test
    public void testScanControllerClass(){
        scanner.scan(CONTROLLER_ONE_PATH, storage, exclusions);
        int controllerTypeCount = storage.getControllerCount();
        String name = ControllerOne.class.getAnnotation(LController.class).name();

        assertEquals("Wrong number of controller types", 1, controllerTypeCount);
        assertEquals("Controller type doesn't match its name", ControllerOne.class, storage.getControllerType(name));
    }

}
