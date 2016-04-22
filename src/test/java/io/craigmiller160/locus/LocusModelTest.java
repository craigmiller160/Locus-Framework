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

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class for the LocusModel class.
 *
 * Created by craig on 3/24/16.
 */
public class LocusModelTest {

    private static LocusStorage storage;
    private static LocusModel locusModel;
    private static ModelOne modelOne;
    private UIThreadExecutorFactory factory;

    private static LocusView locusView;

    private static final Logger logger = LoggerFactory.getLogger(LocusModelTest.class);

    private void setupLocusView(){
        locusView = new LocusView(storage, factory){
            @Override
            public void setValue(String propName, Object... values) throws LocusException{
                //Do nothing, this is just killing the behavior of this object so
                //this test class can run in a more controlled way
            }

            @Override
            public void addValue(String propName, Object... values) throws LocusException{
                //Do nothing, this is just killing the behavior of this object so
                //this test class can run in a more controlled way
            }

            @Override
            public void removeValue(String propName, Object... values) throws LocusException{
                //Do nothing, this is just killing the behavior of this object so
                //this test class can run in a more controlled way
            }
        };
    }

    private void setupLocusModel(){
        locusModel = new LocusModel(storage, locusView);
    }

    private void setupModels(){
        Map<Class<?>,Object> models = TestUtils.setupModels(storage);
        modelOne = (ModelOne) models.get(ModelOne.class);
    }

    @Before
    public void beforeTest(){
        storage = TestUtils.setupStorage();
        factory = TestUtils.setupUIThreadExecutor(storage);
        setupModels();
        setupLocusView();
        setupLocusModel();
    }

    /**
     * Test invoking a getter with arguments.
     */
    @Test
    public void testGetWithArgs(){
        String value1 = "String";
        int value2 = 1;
        modelOne.setStringField(value1);
        modelOne.setIntField(value2);

        Object o1 = locusModel.getValue("Field", 1);
        Object o2 = locusModel.getValue("Field", 2);

        assertNotNull("Result for Field with arg 1 is null", o1);
        assertNotNull("Result for Field with arg 2 is null", o2);

        assertEquals("Result for Field with arg 1 is invalid", value1, o1);
        assertEquals("Result for Field with arg 2 is invalid", value2, o2);
    }

    /**
     * Test invoking a getter with multiple arguments.
     */
    @Test
    public void testGetWithMultipleArgs(){
        String value1 = "String";
        int value2 = 1;
        double value3 = 2.2;
        modelOne.setStringField(value1);
        modelOne.setIntField(value2);
        modelOne.setDoubleField(value3);

        Object[] results = locusModel.getValue("MultipleFields", Object[].class, ModelOne.STRING_FIELD, ModelOne.INT_FIELD, ModelOne.DOUBLE_FIELD);

        assertNotNull("Results array is null", results);
        assertEquals("Results array is the wrong size", 3, results.length);

        assertEquals("Results[0] has wrong value", value1, results[0]);
        assertEquals("Results[1] has the wrong value", value2, results[1]);
        assertEquals("Results[2] has the wrong value", value3, results[2]);
    }

    /**
     * Test invoking a setter with multiple arguments.
     */
    @Test
    public void testSetterWithMultipleArgs(){
        String value1 = "String22";
        int value2 = 5;
        double value3 = 3.2;
        locusModel.setValue("ThreeFields", value1, value2, value3);

        assertEquals("ModelOne StringField invalid value", value1, modelOne.getStringField());
        assertEquals("ModelOne IntField invalid value", value2, modelOne.getIntField());
        assertTrue("ModelOne DoubleField invalid value", value3 == modelOne.getDoubleField());
    }

    /**
     * Test setting an object.
     */
    @Test
    public void testSetObject(){
        locusModel.setValue("ObjectField", new Object());
        assertNotNull("Invalid ObjectField value", modelOne.getObjectField());
    }

    /**
     * Test setting a value using setValue(...),
     * but with a different object type to test
     * inheritance.
     */
    @Test
    public void testSetWithInheritance(){
        BigDecimal bigDecimal = new BigDecimal(5.12345);
        locusModel.setValue("ObjectField", bigDecimal);
        assertEquals("Invalid ObjectField value", bigDecimal, modelOne.getObjectField());
    }

    /**
     * Test getting an Object value.
     */
    @Test
    public void testGetObject(){
        //If testSetObject has run before this, then the field won't be null.
        //If nothing has run, it'll be null by default
        if(modelOne.getObjectField() == null){
            modelOne.setObjectField(new Object());
        }

        assertNotNull("Invalid ObjectField value", locusModel.getValue("ObjectField"));
    }

    /**
     * Test getting an Object, where the value returned
     * is a subclass of object.
     */
    @Test
    public void testGetWithInheritance(){
        BigDecimal bigDecimal = new BigDecimal(567890.1234);
        modelOne.setObjectField(bigDecimal);
        assertEquals("Invalid ObjectField value", bigDecimal, locusModel.getValue("ObjectField"));
    }

    /**
     * Test getting a value with the generic method.
     */
    @Test
    public void testGenericGet(){
        BigDecimal bigDecimal = new BigDecimal(543210.6789);
        modelOne.setObjectField(bigDecimal);
        assertEquals("Invalid class type returned", BigDecimal.class,
                locusModel.getValue("ObjectField", BigDecimal.class).getClass());
        assertEquals("Invalid ObjectField value", bigDecimal, locusModel.getValue("ObjectField", BigDecimal.class));
    }

    /**
     * Test adding a value to a collection
     */
    @Test
    public void testAdd(){
        String value = "Value";
        locusModel.addValue("String", value);

        String result = modelOne.getString(0);
        assertEquals("First list element is not correct value", value, result);
    }

    @Test
    public void testRemove(){
        String value = "Value";
        modelOne.addString(value);

        locusModel.removeValue("String", value);

        boolean exception = false;
        try{
            modelOne.getString(0);
        }
        catch(IndexOutOfBoundsException ex){
            exception = true;
            logger.error("LocusModelTest testRemove() stack trace", ex);
        }

        assertTrue("No exception was thrown when trying to get a value at an index that shouldn't exist", exception);
    }

}
