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

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.othercontroller.ControllerTwo;
import io.craigmiller160.locus.sample.ControllerOne;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class for the LocusController.
 *
 * Created by craigmiller on 3/19/16.
 */
public class LocusControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(LocusControllerTest.class);

    private UIThreadExecutorFactory factory;
    private LocusStorage storage;
    private LocusController locusController;

    /**
     * Setup the LocusController.
     */
    private void setupLocusController(){
        locusController = new LocusController(storage, factory);
    }

    /**
     * Setup each test before executing it.
     */
    @Before
    public void before(){
        storage = TestUtils.setupStorage();
        TestUtils.setupControllers(storage);
        factory = TestUtils.setupUIThreadExecutor(storage);
        setupLocusController();
    }

    /**
     * Test getting a non-singleton controller.
     */
    @Test
    public void testGetControllerNonSingleton(){
        Object controller = locusController.getController(TestUtils.CONTROLLER_ONE_NAME);
        assertNotNull("Controller is null", controller);
        assertEquals("Controller wrong type", ControllerOne.class, controller.getClass());
    }

    /**
     * Test getting a controller with the wrong name, which should
     * fail.
     */
    @Test
    public void testGetControllerWrongName(){
        boolean exceptionThrown = false;
        try{
            Object controller = locusController.getController("Comptroller"); //Deliberate misspelling
        }
        catch(LocusNoControllerException ex){
            exceptionThrown = true;
            logger.error("Wrong Name Stack Trace", ex);
        }

        assertTrue("Exception was not thrown for wrong name", exceptionThrown);
    }

    /**
     * Test getting a controller with a specific type.
     */
    @Test
    public void testControllerSpecificType(){
        ControllerOne cOne = locusController.getController(TestUtils.CONTROLLER_ONE_NAME, ControllerOne.class);

        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller instance is wrong type", ControllerOne.class, cOne.getClass());
    }

    /**
     * Test getting a controller with a specific type, but
     * with the wrong type being specified so it should fail.
     */
    @Test
    public void testControllerInvalidType(){
        boolean exceptionThrown = false;
        try{
            Object cOne = locusController.getController(TestUtils.CONTROLLER_ONE_NAME, String.class);
        }
        catch(LocusInvalidTypeException ex){
            exceptionThrown = true;
            logger.error("Invalid Type Stack Trace", ex);
        }

        assertTrue("No exception was thrown", exceptionThrown);
    }

    /**
     * Test adding and retrieving a callback object for
     * a Controller.
     */
    @Test
    public void testAddingAndRetrievingCallback(){
        //Using BigDecimal for the callback because it has a specific value to be tested for
        BigDecimal callback = new BigDecimal(33.3);

        Object cOne = locusController.getControllerWithCallback(callback, TestUtils.CONTROLLER_ONE_NAME);

        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller is wrong type", ControllerOne.class, cOne.getClass());

        LocusControllerCallback lcc = locusController.callback(cOne);
        assertNotNull("ControllerCallback is null", lcc);
        assertEquals("ControllerCallback is wrapping wrong object", callback, lcc.getCallback());
    }

    /**
     * Test using the Controller callback to
     * retrieve a value from the underlying object.
     */
    @Test
    public void testUsingControllerCallback(){
        Object callback = new Object();

        Object cOne = locusController.getControllerWithCallback(callback, TestUtils.CONTROLLER_ONE_NAME);
        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller is wrong type", ControllerOne.class, cOne.getClass());

        LocusControllerCallback lcc = locusController.callback(cOne);
        assertNotNull("ControllerCallback is null", lcc);
        assertEquals("ControllerCallback is wrapping wrong object", callback, lcc.getCallback());

        Object result = lcc.getValue("Class");
        assertNotNull("Result retrieved from ControllerCallback is null", result);
        assertEquals("Result retrieved from ControllerCallback is not correct", Object.class, result);
    }

    /**
     * Test using the Controller callback to
     * retrieve a value from the underlying object.
     * This tests the generic version of the method
     * that it returns the correct type.
     */
    @Test
    public void testUsingControllerCallbackGeneric(){
        //I know, odd choice for a callback, first thing I could think of with a standard getter
        Thread callback = new Thread();
        long value = callback.getId();

        Object cOne = locusController.getControllerWithCallback(callback, TestUtils.CONTROLLER_ONE_NAME);
        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller is wrong type", ControllerOne.class, cOne.getClass());

        LocusControllerCallback lcc = locusController.callback(cOne);
        assertNotNull("ControllerCallback is null", lcc);
        assertEquals("ControllerCallback is wrapping wrong object", callback, lcc.getCallback());

        long result = lcc.getValue("Id", Long.class);
        assertNotNull("Result retrieved from ControllerCallback is null", result);
        assertEquals("Result retrieved from ControllerCallback is not correct", value, result);
    }

    /**
     * Test retrieving a controller with instantiation
     * arguments being provided.
     */
    @Test
    public void testGetControllerWithArgs(){
        Object controller = locusController.getController(TestUtils.CONTROLLER_ONE_NAME, "ID");
        assertNotNull("Controller is null", controller);
        assertEquals("Controller wrong type", ControllerOne.class, controller.getClass());
        ControllerOne c1 = (ControllerOne) controller;
        assertEquals("Controller value wasn't set by constructor arg", "ID", c1.getId());
    }

    /**
     * Test retrieving a controller with instantiation
     * arguments being provided. This tests the generic
     * return type version of this method.
     */
    @Test
    public void testGetControllerWithArgsGeneric(){
        ControllerOne cOne = locusController.getController(TestUtils.CONTROLLER_ONE_NAME, ControllerOne.class, "ID");

        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller instance is wrong type", ControllerOne.class, cOne.getClass());
        assertEquals("Controller value wasn't set by constructor arg", "ID", cOne.getId());
    }

    /**
     * Test getting a controller with a callback, with
     * instantiation arguments.
     */
    @Test
    public void testGetControllerWithArgsCallback(){
        //I know, odd choice for a callback, first thing I could think of with a standard getter
        Thread callback = new Thread();
        long value = callback.getId();

        Object cOne = locusController.getControllerWithCallback(callback, TestUtils.CONTROLLER_ONE_NAME, "ID");
        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller is wrong type", ControllerOne.class, cOne.getClass());
        ControllerOne c1 = (ControllerOne) cOne;
        assertEquals("Controller value wasn't set by constructor arg", "ID", c1.getId());

        LocusControllerCallback lcc = locusController.callback(cOne);
        assertNotNull("ControllerCallback is null", lcc);
        assertEquals("ControllerCallback is wrapping wrong object", callback, lcc.getCallback());

        long result = lcc.getValue("Id", Long.class);
        assertNotNull("Result retrieved from ControllerCallback is null", result);
        assertEquals("Result retrieved from ControllerCallback is not correct", value, result);
    }

}
