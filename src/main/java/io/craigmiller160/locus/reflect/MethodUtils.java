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

import io.craigmiller160.locus.LocusInvalidTypeException;
import io.craigmiller160.locus.util.MultiValueMap;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for operations involving methods that
 * need to be performed before they can be reflectively
 * invoked.
 *
 * Created by Craig on 2/14/2016.
 */
public class MethodUtils {

    /**
     * A special map for comparing primitive types to wrapper types.
     * Its keys are primitive class types, and its values are wrapper
     * class types. Each key-value pair is obviously matched by being
     * the same core type, for example, int & Integer.
     */
    private static final Map<Class<?>,Class<?>> primitiveWrapperMap = new HashMap<Class<?>,Class<?>>(){{
        put(int.class, Integer.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
        put(short.class, Short.class);
        put(byte.class, Byte.class);
        put(long.class, Long.class);
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
    }};

    /**
     * Because primitive values have their own assignability rules, this
     * map contains them. It stores which primitive types can be safely passed
     * to a method accepting a particular primitive.
     */
    private static final MultiValueMap<Class<?>,Class<?>> primitiveAssignableMap = new MultiValueMap<Class<?>,Class<?>>(){{
        //int
        putValue(int.class, int.class);
        putValue(int.class, short.class);
        putValue(int.class, byte.class);
        putValue(int.class, char.class);

        //float
        putValue(float.class, float.class);
        putValue(float.class, int.class);
        putValue(float.class, short.class);
        putValue(float.class, byte.class);
        putValue(float.class, long.class);
        putValue(float.class, char.class);

        //double
        putValue(double.class, double.class);
        putValue(double.class, char.class);
        putValue(double.class, int.class);
        putValue(double.class, float.class);
        putValue(double.class, short.class);
        putValue(double.class, long.class);

        //short
        putValue(short.class, short.class);
        putValue(short.class, byte.class);

        //byte
        putValue(byte.class, byte.class);

        //long
        putValue(long.class, long.class);
        putValue(long.class, int.class);
        putValue(long.class, short.class);
        putValue(long.class, byte.class);
        putValue(long.class, char.class);

        //boolean
        putValue(boolean.class, boolean.class);

        //char
        putValue(char.class, char.class);
    }};

    MethodUtils(){}

    /**
     * Convert the parameters passed to this method for a varargs invocation
     * on the Method object provided. This is done by determining which of
     * the newParams objects would match up with the varargs position in
     * the method, and then adding them to an array of the appropriate type.
     * Finally, this method creates a new array, consisting of the non-varargs
     * params plus the new varargs array, so that this new parameter array
     * can be safely used to reflectively invoke a varargs method.
     *
     * If the newParams array has no varargs values, and this is instead
     * intended to invoke the method with 0 optional varargs params, then
     * this method instead returns an empty array to fill the space the method
     * expects.
     *
     * IMPORTANT: This method does NO validation about whether or not the method
     * provided is actually a varargs method. It simply assumes that it is. To
     * do such validation, the method's isVarArgs() should be called before using
     * this method.
     *
     * It also does NO validation about whether or not this method can be invoked
     * with the provided params. To do that, use the separate isValidInvocation(...)
     * method in this class.
     *
     * @param method the varargs method to convert the params for.
     * @param newParams the params to convert for the varargs method.
     * @return an array of parameters all set to be used to reflectively invoke the varargs method.
     */
    public static Object[] convertParamsForVarArgsMethod(Method method, Object...newParams){
        if(!method.isVarArgs()){
            return newParams;
        }

        int methodParamCount = method.getParameterTypes().length;

        //If there are parameters provided, convert them
        if(newParams.length > 0){
            //If there are more params than there are extra ones to be converted into a varargs array
            if(newParams.length > methodParamCount){
                int varArgsLength = newParams.length - methodParamCount + 1;
                newParams = convertParams(method, varArgsLength, newParams); //TODO what if there is an array and individual params???
            }
            //If there is exactly the right amount of params, ensure that the last one is either an array or put it into one
            else if(newParams.length == methodParamCount){
                int varArgsIndex = methodParamCount - 1;
                if(method.getParameterTypes()[varArgsIndex].equals(newParams[varArgsIndex].getClass()) ||
                        method.getParameterTypes()[varArgsIndex].isAssignableFrom(newParams[varArgsIndex].getClass())){
                    //This is if an array is already provided
                    return newParams;
                }
                newParams = convertParams(method, 1, newParams);
            }
            //If the param count is one less then expected, then convert the params with an empty varargs array provided
            else if(newParams.length == methodParamCount - 1){
                newParams = convertParams(method, 0, newParams);
            }
            //It should never need to check anything else, because this method doesn't validate invalid invocations
            //Anything else would be an invalid invocation.
        }
        else{
            //If no new params are provided, an empty array needs to be returned.
            return (Object[]) Array.newInstance(method.getParameterTypes()[0], 0);
        }
        return newParams;
    }

    /**
     * Convert the parameters provided for a varargs invocation. It takes
     * the parameters from the varargs index to the end of the newParams array
     * and puts them into a separate varargs array, with the size specified
     * as an argument. If the size specified is 0, an empty varargs array
     * will be created.
     *
     * After the conversion is done, everything is put into a new array
     * and returned.
     *
     * @param method the varargs method to convert the parameters for.
     * @param varArgsSize the number of arguments that are being passed
     *                    to the varargs part of the method.
     * @param newParams the parameters to convert for varargs.
     * @return the converted parameters.
     */
    private static Object[] convertParams(Method method, int varArgsSize, Object...newParams){
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] resultArr = new Object[paramTypes.length];
        //Add every argument up until the index of the varargs to the resultArr
        int varArgsIndex = paramTypes.length - 1;
        for(int i = 0; i < paramTypes.length - 1; i++){
            resultArr[i] = newParams[i];
        }
        //Create a varargs array of the specified size. If 0, an empty array will be created
        Object varArgs = Array.newInstance(paramTypes[varArgsIndex].getComponentType(), varArgsSize);

        //The components are assigned reflectively to accommodate for primitive arrays
        for(int i = 0; i < varArgsSize; i++){
            if(!paramTypes[varArgsIndex].getComponentType().isAssignableFrom(newParams[varArgsIndex + i].getClass()) &&
                    !isAcceptablePrimitive(paramTypes[varArgsIndex].getComponentType(), newParams[varArgsIndex + i].getClass())){
                throw new LocusInvalidTypeException(String.format("An object with type %1$s cannot be assigned to an array of component type %2$s",
                        newParams[varArgsIndex].getClass().getName(), paramTypes[varArgsIndex].getComponentType()));
            }
            Array.set(varArgs, i, newParams[varArgsIndex + i]);
        }

        //Assign the varArgs array to the varArgsIndex in the resultArr, aka the last position in the array
        resultArr[varArgsIndex] = varArgs;

        return resultArr;
    }

    /**
     * Test if the method provided can be invoked with the provided
     * parameters.
     *
     * @param method the method to test for valid invocation.
     * @param newParams the parameters to test that they are valid for invoking the method.
     * @return true if the method can be invoked with the provided parameters.
     */
    public static boolean isValidInvocation(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = false;
        if(newParams.length > 0){
            if(newParams.length > methodParamCount){
                //If more params are provided than are contained in the method, the method MUST be varArgs.
                if(method.isVarArgs()){
                    result = validateParamsWithVarArgs(method, newParams);
                }
            }
            else if(newParams.length == methodParamCount){
                //If their lengths are equal, may or may not be varargs.
                if(method.isVarArgs()){
                    result = validateParamsWithVarArgs(method, newParams);
                }
                else{
                    result = validateParamsNoVarArgs(method, newParams);
                }
            }
            else if(newParams.length == methodParamCount - 1){
                //If provided params are one less than expected, the method MUST be varArgs
                if(method.isVarArgs()){
                    result = validateParamsWithEmptyVarArgs(method, newParams);
                }
            }
            //If none of the above conditions are met, than the required number of params was not submitted and the method is not a match
        }
        else{
            //If no newParams are provided, the method must either have no params, or 1 param that is a varArg.
            result = methodParamCount == 0 || (methodParamCount == 1 && method.isVarArgs());
        }

        return result;
    }

     /**
     * Compare the two provided methods and test if they are duplicates.
     * This is different from Method.equals(...), because this method
     * only takes into account method name and argument types, not
     * the owning class. This is designed to weed out methods from
     * separate classes that are otherwise identical.
     *
     * @param m1 the first method to test.
     * @param m2 the second method to test.
     * @return true if the two methods are duplicates.
     */
    public static boolean isDuplicateMethod(Method m1, Method m2){
        boolean duplicate = false;
        if(m1.getName().equals(m2.getName())){
            Class<?>[] m1ParamTypes = m1.getParameterTypes();
            Class<?>[] m2ParamTypes = m2.getParameterTypes();
            if(m1ParamTypes.length == m2ParamTypes.length){
                if(Arrays.equals(m1ParamTypes, m2ParamTypes)){
                    duplicate = true;
                }
            }
        }

        return duplicate;
    }

    /**
     * Validate that the provided params will all work to
     * reflectively invoke the provided method.
     *
     * This specific method is intended for use with a method that
     * is NOT varargs.
     *
     * @param method the method.
     * @param newParams the params to test that they can be used for invocation.
     * @return true if the parameters pass validation.
     */
    private static boolean validateParamsNoVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        for(int i = 0; i < methodParamCount; i++){
            if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                if(!isAcceptablePrimitive(method.getParameterTypes()[i], newParams[i].getClass())){
                    //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Validate that the provided params will all work to reflectively
     * invoke the provided method.
     *
     * This particular method is intended for use with a method that is
     * varargs and will have parameters passed to that varargs position.
     *
     * @param method the method.
     * @param newParams the parameters to test that they can be used for a
     *                  varargs invocation.
     * @return true if the params pass validation.
     */
    private static boolean validateParamsWithVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        for(int i = 0; i < methodParamCount; i++){
            if(i < methodParamCount - 1){
                //If it's not the last parameter, then it's not the varargs parameter yet
                if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                    if(!isAcceptablePrimitive(method.getParameterTypes()[i], newParams[i].getClass())){
                        //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                        result = false;
                        break;
                    }
                }
            }
            else{
                if(!isValidVarArgs(method.getParameterTypes()[i], Arrays.copyOfRange(newParams, i, newParams.length))){
                    //If the final param, and the newParams provided for that position, won't work as valid varArgs, this is not a mach
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Validate that the provided params will all work to reflectively
     * invoke the provided method.
     *
     * This specific method is intended to be used for reflectively invoking
     * a varargs method with an empty varargs parameter.
     *
     * @param method the method.
     * @param newParams the params to test that they can be used for an empty
     *                  varargs invocation.
     * @return true if the parameters pass validation.
     */
    private static boolean validateParamsWithEmptyVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        //Only loop through all but the last argument to validate
        for(int i = 0; i < methodParamCount - 1; i++){
            if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                if(!isAcceptablePrimitive(method.getParameterTypes()[i], newParams[i].getClass())){
                    //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Validate that the provides parameters are all valid for use with
     * the varArg type provided.
     *
     * IMPORTANT: This method is NOT intended for use with an empty-varargs
     * invocation.
     *
     * @param varArgType the type of vararg.
     * @param varArgParams the parameters to test that they can be assigned to the vararg type.
     * @return true if the parameters are valid for the vararg type.
     */
    private static boolean isValidVarArgs(Class<?> varArgType, Object...varArgParams){
        boolean result = true;
        //If there is a single varArgParam, and it's an array already, simply compare their types and return
        if(varArgParams.length == 1 && varArgParams[0].getClass().isArray()){
            return varArgType.equals(varArgParams[0].getClass()) || varArgType.isAssignableFrom(varArgParams[0].getClass());
        }

        //Get the type of component the varArg array expects
        Class<?> arrayComponentType = varArgType.getComponentType();
        if(arrayComponentType != null){
            for(Object o : varArgParams){
                if(!arrayComponentType.isAssignableFrom(o.getClass())){
                    if(!isAcceptablePrimitive(arrayComponentType, o.getClass())){
                        //If the type of array component cannot accept the varArgParam type, end the loop and this is not a match
                        result = false;
                        break;
                    }
                }
            }
        }
        else{
            //If the arrayComponetType is null, then this method was improperly called. Meaning something is broken
            throw new RuntimeException("isValidVarArgs(...) called on a non-varArg type, check the invoking code for errors");
        }
        return result;
    }

    /**
     * A special method to test if the two class types passed
     * to it are matching primitive types. It has the first Class
     * argument as being from the method itself, and the second
     * is the parameter trying to be passed to it. This method returns
     * true one one of several conditions:
     *
     * 1) Both types are primitives, and both are the same primitive.
     * 2) One type is a primitive, and the other is the matching wrapper type.
     * 3) Both types are primitives, but they are assignable to each other.
     *    For example, an int being passed to a method with a long.
     *
     * @param clazz1 the param type of the method itself
     * @param clazz2 the param type of the object trying to be passed to the method.
     * @return true if they pass one of the specified conditions.
     */
    private static boolean isAcceptablePrimitive(Class<?> clazz1, Class<?> clazz2){
        boolean result = false;
        if(clazz1.isPrimitive() && clazz2.isPrimitive()){
            if(clazz1.equals(clazz2)){
                result = true;
            }
            else{
                Collection<Class<?>> assignableTypes = primitiveAssignableMap.get(clazz1);
                result = assignableTypes.contains(clazz2);
            }
        }
        else if(clazz1.isPrimitive()){
            Class<?> matchingWrapper = primitiveWrapperMap.get(clazz1);
            result = matchingWrapper.equals(clazz2);
        }
        else if(clazz2.isPrimitive()){
            Class<?> matchingWrapper = primitiveWrapperMap.get(clazz2);
            result = matchingWrapper.equals(clazz1);
        }

        return result;
    }
}
