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

package io.craigmiller160.locus.reflect;

import io.craigmiller160.locus.othermodel.ModelTwo;
import io.craigmiller160.locus.sample.ModelOne;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Craig on 2/14/2016.
 */
public class MethodUtilsTest {

    /**
     * Test a simple method with a single argument, and
     * the correct values supplied to MethodUtils.
     */
    @Test
    public void testSimpleMethodSuccess(){
        Method method = getMethod("method1");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "One"));
    }

    /**
     * Test a method with multiple arguments,
     * and the correct values are all supplied
     * to MethodUtils.
     */
    @Test
    public void testLargerMethodSuccess(){
        Method method = getMethod("method2");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "One", 12, false));
    }

    /**
     * Test a varargs method, passing a
     * single argument to the varargs parameter.
     * All supplied values are correct.
     */
    @Test
    public void testVarArgsSingleArgSuccess(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "One", "Two"));
    }

    /**
     * Test a varargs method, passing
     * multiple arguments to the varargs parameter.
     * All supplied values are correct.
     */
    @Test
    public void testVarArgsMultipleArgSuccess(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "One", "Two", "Three", "Four", "Five"));
    }

    /**
     * Test a varargs method, passing an array to
     * the varargs parameter. All supplied values
     * are correct.
     */
    @Test
    public void testVarArgsWithArraySuccess(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);

        String[] args = new String[] {"Two", "Three", "Four", "Five"};

        assertTrue(MethodUtils.isValidInvocation(method, "One", args));
    }

    /**
     * Test a varargs method, but supply no varargs parameters.
     * All values that are supplied are correct.
     */
    @Test
    public void testEmptyVarArgsSuccess(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "Only"));
    }

    /**
     * Test a simple method with a single argument, but
     * supply arguments that should cause it to fail.
     */
    @Test
    public void testSimpleMethodWrongArg(){
        Method method = getMethod("method1");
        assertNotNull("Method for test wasn't retrieved", method);
        assertFalse(MethodUtils.isValidInvocation(method, 22));
    }

    /**
     * Test a method with multiple arguments,
     * but the values supplied are incorrect,
     * causing it to return false.
     */
    @Test
    public void testLargerMethodWrongArgs(){
        Method method = getMethod("method2");
        assertNotNull("Method for test wasn't retrieved", method);
        assertFalse(MethodUtils.isValidInvocation(method, 22, "Hi there", "What"));
    }

    /**
     * Test a method with multiple arguments, but
     * too few arguments are supplied, causing it to return false.
     */
    @Test
    public void testLargerMethodTooFewArgs(){
        Method method = getMethod("method2");
        assertNotNull("Method for test wasn't retrieved", method);
        assertFalse(MethodUtils.isValidInvocation(method, "Hi there", 22));
    }

    /**
     * Test a method with multiple arguments,
     * but too many arguments are supplied, causing
     * it to fail.
     */
    @Test
    public void testLargerMethodTooManyArgs(){
        Method method = getMethod("method2");
        assertNotNull("Method for test wasn't retrieved", method);
        assertFalse(MethodUtils.isValidInvocation(method, "Hi there", 22, false, 22, 22, 22, 22));
    }

    /**
     * Test validating a method with polymorphic values. That
     * is, the values provided are subclasses of what is expected.
     */
    @Test
    public void testPolymorphicValues(){
        Method method = getMethod("method4");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, "Hi there", 22));
    }

    /**
     * Test validating a method with primitive parameters.
     * The primitive params of the method and the ones passed
     * to it are identical.
     */
    @Test
    public void testPrimitivesIdenticalParams(){
        Method method = getMethod("method6");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, 22));
    }

    /**
     * Test validating a method with polymorphic values.
     * That is, th values provided are subclasses of what is
     * expected. This test also tests this with a VarArgs
     * parameter as well.
     */
    @Test
    public void testPolymorphicValuesVarArgs(){
        Method method = getMethod("method5");
        assertNotNull("Method for test wasn't retrieved", method);
        assertTrue(MethodUtils.isValidInvocation(method, true, 23, 27.56));
    }

    /**
     * Test validating a method with a varArgs
     * that is passed an array that is a subclass
     * of what the method is expecting.
     */
    @Test
    public void testPolymorphicArrayValuesVarArgs(){
        Method method = getMethod("method5");
        assertNotNull("Method for test wasn't retrieved", method);

        Double[] args = new Double[]{23.72, 27.56};

        assertTrue(MethodUtils.isValidInvocation(method, true, args));
    }

    /**
     * Test converting the params for use in a varArgs
     * method. This test is done using multiple VarArgs values.
     */
    @Test
    public void testConvertForVarArgsMultiValue(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message", "One", "Two", "Three");
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a String[]", params[1].getClass(), String[].class);
        String[] arr = (String[]) params[1];
        assertEquals("String Array Wrong Size", arr.length, 3);
        assertEquals("String Array First Element Wrong Value", arr[0], "One");
        assertEquals("String Array Second Element Wrong Value", arr[1], "Two");
        assertEquals("String Array Third Element Wrong Value", arr[2], "Three");
    }

    /**
     * Test converting the params for use in a varArgs
     * method. This test is done using a single value.
     */
    @Test
    public void testConvertForVarArgsSingleValue(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message", "One");
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a String[]", params[1].getClass(), String[].class);
        String[] arr = (String[]) params[1];
        assertEquals("String Array Wrong Size", arr.length, 1);
        assertEquals("String Array First Element Wrong Value", arr[0], "One");
    }

    /**
     * Test converting the params for use in a varArgs
     * method, if the invocation needs to provide
     * an empty value for varArgs.
     */
    @Test
    public void testConvertForVarArgsEmpty(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);
        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message");
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a String[]", params[1].getClass(), String[].class);
        String[] arr = (String[]) params[1];
        assertEquals("String Array Wrong Size", arr.length, 0);
    }

    /**
     * Test converting for varArgs if an array is passed
     * as the varArgs parameter.
     */
    @Test
    public void testConvertForVarArgsArray(){
        Method method = getMethod("method3");
        assertNotNull("Method for test wasn't retrieved", method);

        String[] args = new String[]{"One", "Two", "Three"};

        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message", args);
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a String[]", params[1].getClass(), String[].class);
        String[] arr = (String[]) params[1];
        assertEquals("String Array Wrong Size", arr.length, 3);
        assertEquals("String Array First Element Wrong Value", arr[0], "One");
        assertEquals("String Array Second Element Wrong Value", arr[1], "Two");
        assertEquals("String Array Third Element Wrong Value", arr[2], "Three");
    }

    @Test
    public void testConvertForVarArgsPolymorphicArray(){
        Method method = getMethod("method5");
        assertNotNull("Method for test wasn't retrieved", method);

        Integer[] args = new Integer[]{1, 2, 3};

        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message", args);
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a Integer[]", params[1].getClass(), Integer[].class);
        Integer[] arr = (Integer[]) params[1];
        assertEquals("Integer Array Wrong Size", arr.length, 3);
        assertEquals("Integer Array First Element Wrong Value", arr[0], new Integer(1));
        assertEquals("Integer Array Second Element Wrong Value", arr[1], new Integer(2));
        assertEquals("Integer Array Third Element Wrong Value", arr[2], new Integer(3));
    }

    @Test
    public void testConvertForVarArgsPolymorphic(){
        Method method = getMethod("method5");
        assertNotNull("Method for test wasn't retrieved", method);
        Object[] params = MethodUtils.convertParamsForVarArgsMethod(method, "Message", 22, 33.781, 46);
        assertEquals("Params wrong size", params.length, 2);
        assertEquals("First param isn't a String", params[0].getClass(), String.class);
        assertEquals("First param has wrong content", params[0], "Message");
        assertEquals("Second param isn't a Number[]", params[1].getClass(), Number[].class);
        Number[] numArr = (Number[]) params[1];
        assertEquals("Number Array Wrong Size", numArr.length, 3);
        assertEquals("Number Array First Element Wrong Type", numArr[0].getClass(), Integer.class);
        assertEquals("Number Array First Element Wrong Value", numArr[0], 22);
        assertEquals("Number Array Second Element Wrong Type", numArr[1].getClass(), Double.class);
        assertEquals("Number Array Second Element Wrong Value", numArr[1], 33.781);
        assertEquals("Number Array Third Element Wrong Type", numArr[2].getClass(), Integer.class);
        assertEquals("Number Array Third Element Wrong Value", numArr[2], 46);
    }

    /**
     * This test pulls three methods and tests them for being
     * duplicates. The first and third should be duplicates, the
     * second is unique.
     *
     * @throws Exception if an error occurs outside the scope
     * of the test.
     */
    @Test
    public void testIsDuplicateMethod() throws Exception{
        Method m1 = ModelOne.class.getMethod("setFirstField", String.class);
        Method m2 = ModelTwo.class.getMethod("setFieldThree", String.class);
        Method m3 = ModelTwo.class.getMethod("setFirstField", String.class);

        assertTrue("Duplicate methods weren't recognized", MethodUtils.isDuplicateMethod(m1, m3));
        assertFalse("Non-Duplicate methods recognized as duplicate", MethodUtils.isDuplicateMethod(m1, m2));
    }

    /**
     * Because of how important the new isPrimitive method is
     * for MethodUtils, it is reflectively retrieved and accessed
     * here to test it directly.
     *
     * @throws Exception if an error occurs outside the scope
     * of the test.
     */
    @Test
    public void testIsAcceptablePrimitive() throws Exception{
        Method m = MethodUtils.class.getDeclaredMethod("isAcceptablePrimitive", Class.class, Class.class);
        m.setAccessible(true);

        MethodUtils mu = new MethodUtils();
        boolean result = (Boolean) m.invoke(mu, int.class, int.class);
        assertTrue("The primitive int was able to accept the other primitive int", result);

        result = (Boolean) m.invoke(mu, int.class, Integer.class);
        assertTrue("The primitive int was able to accept the wrapper Integer", result);

        result = (Boolean) m.invoke(mu, Integer.class, int.class);
        assertTrue("The wrapper Integer was able to accept the primitive int", result);

        result = (Boolean) m.invoke(mu, int.class, Boolean.class);
        assertFalse("The primitive int was able to accept the wrapper Boolean", result);

        result = (Boolean) m.invoke(mu, long.class, int.class);
        assertTrue("The primitive long wouldn't accept the primitive int", result);
    }

    /**
     * Simple utility method for the regularly used operation
     * to get the method to test in the test methods.
     *
     * @param methodName the name of the method to retrieve.
     * @return the found test method, or null if none are found.
     */
    private Method getMethod(String methodName){
        TestClass tc = new TestClass();

        Method[] methods = tc.getClass().getMethods();
        for(Method m : methods){
            if(m.getName().equals(methodName)){
                return m;
            }
        }
        return null;
    }

    /**
     * Dummy TestClass to use for testing the reflective methods.
     *
     */
    private class TestClass{

        /**
         * A simple method with a single argument.
         *
         * @param message a String argument.
         * @return the String argument.
         */
        public String method1(String message){
            return message;
        }

        /**
         * A more complex method with multiple
         * arguments.
         *
         * @param message a String argument.
         * @param i an Integer argument.
         * @param b a Boolean argument.
         * @return all three arguments concatenated together.
         */
        public String method2(String message, Integer i, Boolean b){
            return message + " " + i + " " + b;
        }

        public String method3(String message, String...more){
            return message + " " + Arrays.toString(more);
        }

        public String method4(Object o1, Number o2){
            return o1.toString() + " " + o2.toString();
        }

        public String method5(Object o1, Number...nums){
            return o1.toString() + " " + Arrays.toString(nums);
        }

        public String method6(int i){
            return "Param: " + i;
        }

    }

}
