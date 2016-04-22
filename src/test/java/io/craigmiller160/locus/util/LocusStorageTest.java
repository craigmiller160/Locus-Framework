/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.TestUtils;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test class for the LocusStorage class.
 *
 * Created by craig on 4/6/16.
 */
public class LocusStorageTest {

    private LocusStorage storage;

    /**
     * Setup before each test.
     */
    @Before
    public void preTest(){
        storage = TestUtils.setupStorage();
        TestUtils.setupModels(storage);
        TestUtils.setupViews(storage);
        TestUtils.setupControllers(storage);
    }

    /**
     * Test getting all model property names. This should
     * return a set of the unique property names for the models
     * registered with the storage, ie having a getter & setter
     * for a property would still only count as one.
     */
    @Test
    public void testGetAllModelPropsNames(){
        Set<String> propNames = storage.getAllModelPropertyNames();

        assertNotNull("Model PropNames Set is null", propNames);
        assertEquals("Model PropNames Set is wrong size", 14, propNames.size());
    }

    /**
     * Test getting all view property names. This should
     * return a set of the unique property names for all
     * the views registered with the storage.
     */
    @Test
    public void testGetAllViewPropNames(){
        Set<String> propNames = storage.getAllViewPropNames();

        assertNotNull("View PropNames Set is null", propNames);
        assertEquals("View PropNames Set is wrong size", 13, propNames.size());
    }

    /**
     * Test getting all controller names.
     */
    @Test
    public void testGetAllControllerNames(){
        Set<String> controllerNames = storage.getAllControllerNames();

        assertNotNull("ControllerNames Set is null", controllerNames);
        assertEquals("ControllerNames Set is wrong size", 2, controllerNames.size());
    }

    /**
     * Test that all model prop setters were added
     * correctly.
     */
    @Test
    public void testModelPropSetters(){
        assertEquals("Wrong number of model prop setters", 12, storage.getModelPropSetterCount());
    }

    /**
     * Test that all model prop getters were added correctly.
     */
    @Test
    public void testModelPropGetters(){
        assertEquals("Wrong number of model prop getters", 12, storage.getModelPropGetterCount());
    }

    /**
     * Test that all controllers were added correctly.
     */
    @Test
    public void testControllers(){
        assertEquals("Wrong number of controllers", 2, storage.getControllerCount());
    }

}
