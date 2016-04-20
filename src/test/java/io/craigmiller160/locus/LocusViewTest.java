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
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;

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

    private void setupStorage(){
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
            storage.setUIThreadExecutorType(NoUIThreadExecutor.class);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }
    }

    private void setupUIThreadExecutor(){
        try{
            Constructor<UIThreadExecutorFactory> constructor = UIThreadExecutorFactory.class.getDeclaredConstructor(LocusStorage.class);
            constructor.setAccessible(true);
            factory = constructor.newInstance(storage);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }
    }

    private void setupViews(){
        try{
            viewOne = new ViewOne();
            viewThree = new ViewThree();

            Method m1 = viewOne.getClass().getMethod("setFirstField", String.class);
            Method m2 = viewThree.getClass().getMethod("setFirstField", String.class);
            Method m3 = viewOne.getClass().getMethod("setThreeFields", String.class, int.class, double.class);
            Method m4 = viewOne.getClass().getMethod("setObjectField", Object.class);

            ClassAndMethod cam1 = new ClassAndMethod(viewOne.getClass(), m1);
            ClassAndMethod cam2 = new ClassAndMethod(viewThree.getClass(), m2);
            ClassAndMethod cam3 = new ClassAndMethod(viewOne.getClass(), m3);
            ClassAndMethod cam4 = new ClassAndMethod(viewOne.getClass(), m4);

            storage.addViewPropSetter("FirstField", cam1);
            storage.addViewPropSetter("FirstField", cam2);
            storage.addViewPropSetter("ThreeFields", cam3);
            storage.addViewPropSetter("ObjectField", cam4);

            storage.addViewInstance(viewOne.getClass(), viewOne);
            storage.addViewInstance(viewThree.getClass(), viewThree);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to setup views for test", ex);
        }
    }

    private void setupLocusView(){
        this.locusView = new LocusView(storage, factory);
    }

    @Before
    public void initialize(){
        setupStorage();
        setupUIThreadExecutor();
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
     * Test invoking a method with the wrong parameters, so that
     * no invocation will be successful. An exception should be thrown.
     */
    @Test
    public void testFailedInvocation(){
        String value = "Value";

        boolean exceptionWasThrown = false;
        try{
            locusView.setValue("TwoFields", value);
        }
        catch(ReflectiveException ex){
            exceptionWasThrown = true;
            logger.error("testFailedInvocation exception stack trace", ex);
        }

        assertTrue("No exception was thrown for an invocation attempt that should've found no valid matches", exceptionWasThrown);
    }

    /**
     * Test invoking a method that doesn't exist, so that an
     * exception will be thrown.
     */
    @Test
    public void testNoMethod(){
        String value = "Value";

        boolean exceptionWasThrown = false;
        try{
            locusView.setValue("NoField", value);
        }
        catch(ReflectiveException ex){
            exceptionWasThrown = true;
            logger.error("testNoMethod exception stack trace", ex);
        }

        assertTrue("No exception was thrown for an attempt to invoke non-existent method", exceptionWasThrown);
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

}
