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
    private static final String CONTROLLER_ONE_PATH = "io.craigmiller160.locus.sample.ControllerOne";

    private LocusStorage storage;
    private LocusClassScanner scanner;

    @Before
    public void before(){
        storage = TestUtils.setupStorage();
        scanner = new LocusClassScanner();
    }

    @Test
    public void testScanModelClass(){
        scanner.scan(MODEL_ONE_PATH, storage);
        int modelPropSetterCount = storage.getModelPropSetterCount();
        int modelPropGetterCount = storage.getModelPropSetterCount();

        assertEquals("Wrong number of model prop setters", 12, modelPropSetterCount);
        assertEquals("Wrong number of model prop getters", 12, modelPropGetterCount);
    }

    @Test
    public void testScanViewClass(){
        scanner.scan(VIEW_ONE_PATH, storage);
        int viewPropSetterCount = storage.getViewPropSetterCount();

        assertEquals("Wrong number of view prop setters", 13, viewPropSetterCount);
    }

    @Test
    public void testScanControllerClass(){
        scanner.scan(CONTROLLER_ONE_PATH, storage);
        int controllerTypeCount = storage.getControllerCount();
        String name = ControllerOne.class.getAnnotation(LController.class).name();

        assertEquals("Wrong number of controller types", 1, controllerTypeCount);
        assertEquals("Controller type doesn't match its name", ControllerOne.class, storage.getControllerType(name));
    }

}
