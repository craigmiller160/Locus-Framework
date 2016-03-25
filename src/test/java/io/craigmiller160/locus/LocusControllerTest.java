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

import io.craigmiller160.locus.sample.ControllerOne;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

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

    /**
     * Get a LocusStorage instance for use in tests.
     *
     * @return the LocusStorage instance.
     * @throws RuntimeException if unable to create the LocusStorage.
     */
    private LocusStorage getStorage(){
        LocusStorage storage = null;
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
        }
        catch(Exception ex){
            throw new RuntimeException("Unable to reflectively create LocusStorage for test", ex);
        }

        return storage;
    }

    /**
     * Test getting a non-singleton controller.
     */
    @Test
    public void testGetControllerNonSingleton(){
        LocusStorage storage = getStorage();
        LocusController locusController = new LocusController(storage);
        storage.addControllerType("ControllerOne", ControllerOne.class, false);

        Object controller = locusController.getController("ControllerOne");
        assertNotNull("Controller is null", controller);
        assertEquals("Controller wrong type", controller.getClass(), ControllerOne.class);
    }

    /**
     * Test getting a controller with the wrong name, which should
     * fail.
     */
    @Test
    public void testGetControllerWrongName(){
        LocusStorage storage = getStorage();
        LocusController locusController = new LocusController(storage);
        storage.addControllerType("ControllerOne", ControllerOne.class, false);

        boolean exceptionThrown = false;
        try{
            Object controller = locusController.getController("ControlerOne"); //Deliberate misspelling
        }
        catch(LocusNoControllerException ex){
            exceptionThrown = true;
            logger.error("Wrong Name Stack Trace", ex);
        }

        assertTrue("Exception was not thrown for wrong name", exceptionThrown);
    }

    /**
     * Test getting a singleton controller.
     */
    @Test
    public void testControllerSingleton(){
        LocusStorage storage = getStorage();
        LocusController locusController = new LocusController(storage);
        storage.addControllerType("ControllerOne", ControllerOne.class, true);

        Object one = locusController.getController("ControllerOne");
        Object two = locusController.getController("ControllerOne");

        assertNotNull("First controller ref is null", one);
        assertNotNull("Second controller ref is null", two);
        assertEquals("First controller ref is wrong type", one.getClass(), ControllerOne.class);
        assertEquals("Second controller ref is wrong type", two.getClass(), ControllerOne.class);

        ControllerOne cOne = (ControllerOne) one;
        ControllerOne cTwo = (ControllerOne) two;

        cOne.setId("One ID");

        assertEquals("Controller refs don't have identical ID fields", cOne.getId(), cTwo.getId());
    }

    /**
     * Test getting a controller with a specific type.
     */
    @Test
    public void testControllerSpecificType(){
        LocusStorage storage = getStorage();
        LocusController locusController = new LocusController(storage);
        storage.addControllerType("ControllerOne", ControllerOne.class, false);

        ControllerOne cOne = locusController.getController("ControllerOne", ControllerOne.class);

        assertNotNull("Controller instance is null", cOne);
        assertEquals("Controller instance is wrong type", cOne.getClass(), ControllerOne.class);
    }

    /**
     * Test getting a controller with a specific type, but
     * with the wrong type being specified so it should fail.
     */
    @Test
    public void testControllerInvalidType(){
        LocusStorage storage = getStorage();
        LocusController locusController = new LocusController(storage);
        storage.addControllerType("ControllerOne", ControllerOne.class, false);

        boolean exceptionThrown = false;
        try{
            Object cOne = locusController.getController("ControllerOne", String.class);
        }
        catch(LocusInvalidTypeException ex){
            exceptionThrown = true;
            logger.error("Invalid Type Stack Trace", ex);
        }

        assertTrue("No exception was thrown", exceptionThrown);
    }

}
