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
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class to test the LocusView class.
 * This is one of the core components of the Locus
 * Framework.
 *
 * Created by craigmiller on 3/26/16.
 */
public class LocusViewTest {

    private static final Logger logger = LoggerFactory.getLogger(LocusViewTest.class);

    private LocusStorage storage;
    private UIThreadExecutorFactory factory;
    private ViewThree viewThree;
    private ViewOne viewOne;
    private LocusView locusView;

    private void setupViews(){
        Map<Class<?>,Object> views = TestUtils.setupViews(storage);
        viewOne = (ViewOne) views.get(ViewOne.class);
        viewThree = (ViewThree) views.get(ViewThree.class);
    }

    private void setupLocusView(){
        this.locusView = new LocusView(storage, factory);
    }

    @Before
    public void initialize(){
        storage = TestUtils.setupStorage();
        factory = TestUtils.setupUIThreadExecutor(storage);
        setupViews();
        setupLocusView();
    }

    /**
     * Test registering a new view, that its
     * instance is saved in the storage.
     */
    @Test
    public void testRegisterView(){
        ViewOne anotherViewOne = new ViewOne();
        locusView.registerView(anotherViewOne);

        Collection<WeakReference<?>> instances = storage.getViewInstancesForClass(ViewOne.class);
        assertEquals("Wrong number of view instances", instances.size(), 2);
    }

    /**
     * Test setting a single value in a view.
     */
    @Test
    public void testSetValue(){
        double value = 22.33;
        locusView.setValue("DoubleField", value);

        double result = viewOne.getDoubleField();
        assertTrue("DoubleField value is incorrect", value == result);
    }

    /**
     * Test setting a value that should call
     * multiple setters.
     */
    @Test
    public void testInvokeMultipleSetters(){
        String value = "Value";
        locusView.setValue("FirstField", value);

        String val1 = viewOne.getFirstField();
        String val2 = viewThree.getViewThreeFirstField();

        //Test that neither value is null. They start out as null
        assertNotNull("ViewOne FirstField value is null", val1);
        assertNotNull("ViewThree FirstField value is null", val2);

        //Test that both values are set properly, and are the same
        assertEquals("ViewOne FirstField value invalid", val1, value);
        assertEquals("ViewThree FirstField value invalid", val2, value);
    }

    /**
     * Test invoking a setter with multiple arguments.
     */
    @Test
    public void testSetMultipleValues(){
        String value1 = "Value1";
        int value2 = 2;
        double value3 = 3.3;
        locusView.setValue("ThreeFields", value1, value2, value3);

        assertEquals("ViewOne FirstField invalid value", value1, viewOne.getFirstField());
        assertEquals("ViewOne IntField invalid value", value2, viewOne.getIntField());
        assertTrue("ViewOne DoubleField invalid value", value3 == viewOne.getDoubleField());
    }

    /**
     * Test that a weak reference becomes null
     * at the appropriate time.
     */
    @Test
    public void testWeakRefNull() throws Exception{
        //Any object can be a view, using a BigDecimal provides a specific value to test
        BigDecimal value = new BigDecimal(33.23);
        locusView.registerView(value);

        Collection<WeakReference<?>> instances = storage.getViewInstancesForClass(BigDecimal.class);
        assertNotNull("Pre-Nulling Instances collection is null, it shouldn't be", instances);
        assertEquals("Pre-Nulling Instances collection wrong size", 1, instances.size());

        WeakReference<?> instance = instances.iterator().next();
        assertNotNull("Pre-Nulling Instance object is null, it should not be yet", instance.get());
        assertEquals("Pre-Nulling Instance object wrong value", value, instance.get());

        //Null the value and run the garbage collector
        value = null;
        Runtime.getRuntime().gc();

        instances = storage.getViewInstancesForClass(BigDecimal.class);
        assertNotNull("Post-Nulling Instances collection is null, it shouldn't be", instances);
        assertEquals("Post-Nulling Instances collection wrong size", 0, instances.size());

    }

    /**
     * Test adding a value to a collection property in
     * a view.
     */
    @Test
    public void testAddValue(){
        String value = "Value";
        locusView.addValue("String", value);

        String result = viewOne.getString(0);
        assertNotNull("ViewOne result is null", result);
        assertEquals("ViewOne result is wrong value", value, result);
    }

    /**
     * Test removing a value from a collection property
     * in a view.
     */
    @Test
    public void testRemoveValue(){
        String value = "Value";
        viewOne.addString(value);
        locusView.removeValue("String", value);

        boolean exception = false;
        try{
            viewOne.getString(0);
        }
        catch(IndexOutOfBoundsException ex){
            exception = true;
            logger.error("LocusViewTest testRemoveValue() exception stack trace", ex);
        }

        assertTrue("No exception was thrown when there should've been one", exception);
    }

}
