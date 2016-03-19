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

import java.lang.reflect.Method;

/**
 * A special container class for a Method and a single
 * instance of the Class it is from. This is a helper
 * class for parsing methods for a reflective invocation.
 * It includes several methods to provide information
 * about the Method, and has the Object as well so,
 * after the parsing is done, the method can be quickly
 * invoked on its owning object.
 *
 * Created by Craig on 2/14/2016.
 */
public class ObjectAndMethod extends ReflectiveMethodHolder<Object>{

    public ObjectAndMethod(Object obj, Method m){
        super(obj, m);
    }

    @Override
    public Class<?> getSourceType(){
        return getSource().getClass();
    }

    @Override
    public String toString(){
        String className = getSource().getClass().getName();
        String methodName = getMethod().getName();
        String[] paramTypeNames = getParamTypeNames();

        StringBuilder builder = new StringBuilder()
                .append(className)
                .append(".")
                .append(methodName)
                .append("(");

        for(int i = 0; i < paramTypeNames.length; i++){
            builder.append(paramTypeNames[i]);
            if(i < paramTypeNames.length - 1){
                builder.append(",");
            }
        }
        builder.append(")");

        return builder.toString();
    }

    private String[] getParamTypeNames(){
        String[] paramTypeNames = new String[getMethodParamTypes().length];
        for(int i = 0; i < paramTypeNames.length; i++){
            paramTypeNames[i] = getMethodParamTypes()[i].getName();
        }
        return paramTypeNames;
    }

}
