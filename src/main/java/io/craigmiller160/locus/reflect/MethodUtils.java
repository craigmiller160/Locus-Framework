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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Craig on 2/14/2016.
 */
public class MethodUtils {

    //TODO document how this method converts without validation, and isValidInvocation() should be used first
    public static Object[] convertParamsForVarArgsMethod(Method method, Object...newParams){
        if(!method.isVarArgs()){
            return newParams;
        }

        int methodParamCount = method.getParameterTypes().length;

        if(newParams.length > 0){
            if(newParams.length > methodParamCount){
                int varArgsLength = newParams.length - methodParamCount + 1;
                newParams = convertParams(method, varArgsLength, newParams);
            }
            else if(newParams.length == methodParamCount){
                int varArgsIndex = methodParamCount - 1;
                if(method.getParameterTypes()[varArgsIndex].equals(newParams[varArgsIndex].getClass()) ||
                        method.getParameterTypes()[varArgsIndex].isAssignableFrom(newParams[varArgsIndex].getClass())){
                    //This is if an array is already provided
                    return newParams;
                }
                newParams = convertParams(method, 1, newParams);
            }
            else if(newParams.length == methodParamCount - 1){
                newParams = convertParams(method, 0, newParams);
            }
        }
        else{
            //If no new params are provided, an empty array needs to be returned.
            return (Object[]) Array.newInstance(method.getParameterTypes()[0], 0);
        }
        return newParams;
    }

    private static Object[] convertParams(Method method, int varArgsSize, Object...newParams){
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] resultArr = new Object[paramTypes.length];
        int varArgsIndex = paramTypes.length - 1;
        for(int i = 0; i < paramTypes.length - 1; i++){
            resultArr[i] = newParams[i];
        }

        Object[] varArgs = (Object[]) Array.newInstance(paramTypes[varArgsIndex].getComponentType(), varArgsSize);
        for(int i = 0; i < varArgsSize; i++){
            varArgs[i] = newParams[varArgsIndex + i];
        }
        resultArr[varArgsIndex] = varArgs;

        return resultArr;
    }

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

    private static boolean validateParamsNoVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        for(int i = 0; i < methodParamCount; i++){
            if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                result = false;
                break;
            }
        }

        return result;
    }

    private static boolean validateParamsWithVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        for(int i = 0; i < methodParamCount; i++){
            if(i < methodParamCount - 1){
                //If it's not the last parameter, then it's not the varargs parameter yet
                if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                    //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                    result = false;
                    break;
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

    private static boolean validateParamsWithEmptyVarArgs(Method method, Object...newParams){
        int methodParamCount = method.getParameterTypes().length;

        boolean result = true;
        //Only loop through all but the last argument to validate
        for(int i = 0; i < methodParamCount - 1; i++){
            if(!method.getParameterTypes()[i].isAssignableFrom(newParams[i].getClass())) {
                //If any parameter type is not assignable, the loop should end and method should return false, this is not a match
                result = false;
                break;
            }
        }
        return result;
    }

    //TODO document how this is not meant for empty varArgs
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
                    //If the type of array component cannot accept the varArgParam type, end the loop and this is not a match
                    result = false;
                    break;
                }
            }
        }
        else{
            //If the arrayComponetType is null, then this method was improperly called. Meaning something is broken
            throw new RuntimeException("isValidVarArgs(...) called on a non-varArg type, check the invoking code for errors");
        }
        return result;
    }
}
