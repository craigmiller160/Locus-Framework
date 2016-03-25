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

package io.craigmiller160.locus.reflect;

import io.craigmiller160.locus.sample.ModelOne;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test class to test the
 * LocusInvoke class.
 *
 * Created by craig on 3/20/16.
 */
public class LocusInvokeTest {

    @Test
    public void testValidSingleParamNoReturn() throws Exception{
        ModelOne modelOne = new ModelOne();
        Method m = modelOne.getClass().getMethod("setStringField", String.class);
        ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);

        //Storing the value in a variable for easier testing
        String newValue = "NewValue";

        LocusInvoke.invokeMethod(oam, newValue);

        assertEquals("Incorrect value for property StringField in ModelOne", modelOne.getStringField(), newValue);
    }

    @Test
    public void testValidMultipleParamNoReturn() throws Exception{
        ModelOne modelOne = new ModelOne();
        Method m = modelOne.getClass().getMethod("setFields", Object[].class);
        ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);

        //Storing the values in a variable for easier testing
        String newValue1 = "NewValue1";
        int newValue2 = 101;

        LocusInvoke.invokeMethod(oam, newValue1, newValue2);

        assertEquals("Incorrect value for property StringField in ModelOne", modelOne.getStringField(), newValue1);
        assertEquals("Incorrect value for property IntField in ModelOne", modelOne.getIntField(), newValue2);
    }

    @Test
    public void testValidNoParamWithReturn() throws Exception{
        ModelOne modelOne = new ModelOne();
        Method m = modelOne.getClass().getMethod("getStringField");
        ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);

        //Storing the value in a variable for easier testing
        String newValue = "NewValue";
        modelOne.setStringField(newValue);

        Object result = LocusInvoke.invokeMethod(oam);

        assertNotNull("Result of LocusInvoke is null", result);
        assertEquals("Result type is not String", result.getClass(), String.class);
        String resultS = (String) result;
        assertEquals("Result value does not match expected value", resultS, newValue);
    }

    @Test
    public void testValidOneParamWithReturn() throws Exception{
        ModelOne modelOne = new ModelOne();
        Method m = modelOne.getClass().getMethod("getField", int.class);
        ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);

        //Storing the value in a variable for easier testing
        String newValue1 = "NewValue1";
        int newValue2 = 101;
        modelOne.setStringField(newValue1);
        modelOne.setIntField(newValue2);

        Object result1 = LocusInvoke.invokeMethod(oam, 1);

        assertNotNull("Result of LocusInvoke is null", result1);
        assertEquals("Result type is not String", result1.getClass(), String.class);
        String result1S = (String) result1;
        assertEquals("Result value does not match expected value", result1S, newValue1);
    }

}
