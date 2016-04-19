/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

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

    private void setupLocusView(){
        locusView = new LocusView(storage, factory){
            @Override
            public void setValue(String propName, Object... value) throws LocusException{
                //Do nothing, this is just killing the behavior of this object so
                //this test class can run in a more controlled way
            }
        };
    }

    private void setupLocusModel(){
        locusModel = new LocusModel(storage, locusView);
    }

    private void setupModels(){
        modelOne = new ModelOne();
        Method[] methods = ModelOne.class.getDeclaredMethods();
        for(Method m : methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                storage.addModelPropSetter(propName, new ObjectAndMethod(modelOne, m));
            }
            else if(m.getName().startsWith("get")){
                String propName = m.getName().substring(3);
                storage.addModelPropGetter(propName, new ObjectAndMethod(modelOne, m));
            }
        }
    }

    @Before
    public void beforeTest(){
        setupStorage();
        setupModels();
        setupUIThreadExecutor();
        setupLocusView();
        setupLocusModel();
    }

    /**
     * Static initializer to set up the LocusStorage properly for
     * use in these tests. It's created reflectively because
     * there's no access to its constructor normally.
     */
//    static{
//        try{
//            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
//            constructor.setAccessible(true);
//            storage = constructor.newInstance();
//
//            modelOne = new ModelOne();
//            Method[] methods = ModelOne.class.getDeclaredMethods();
//            for(Method m : methods){
//                if(m.getName().startsWith("set")){
//                    String propName = m.getName().substring(3);
//                    storage.addModelPropSetter(propName, new ObjectAndMethod(modelOne, m));
//                }
//                else if(m.getName().startsWith("get")){
//                    String propName = m.getName().substring(3);
//                    storage.addModelPropGetter(propName, new ObjectAndMethod(modelOne, m));
//                }
//            }
//
//            locusModel = new LocusModel(storage, locusView);
//        }
//        catch(Exception ex){
//            throw new RuntimeException("Fatal exception at initialization", ex);
//        }
//    }

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
        assertEquals("Invalid ObjectField value", modelOne.getObjectField(), bigDecimal);
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
        assertEquals("Invalid ObjectField value", locusModel.getValue("ObjectField"), bigDecimal);
    }

    /**
     * Test getting a value with the generic method.
     */
    @Test
    public void testGenericGet(){
        BigDecimal bigDecimal = new BigDecimal(543210.6789);
        modelOne.setObjectField(bigDecimal);
        assertEquals("Invalid class type returned",
                locusModel.getValue("ObjectField", BigDecimal.class).getClass(),
                BigDecimal.class);
        assertEquals("Invalid ObjectField value", locusModel.getValue("ObjectField", BigDecimal.class), bigDecimal);
    }

}
