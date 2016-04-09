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
 * Created by craig on 3/15/16.
 */
public class ClassAndMethod extends ReflectiveMethodHolder<Class<?>> {

    public ClassAndMethod(Class<?> obj, Method m) {
        super(obj, m);
    }

    public ClassAndMethod(ClassAndMethod cam){
        super(cam.getSource(), cam.getMethod());
    }

    public boolean isAssignableFrom(Class<?> clazz){
        return getSource().isAssignableFrom(clazz);
    }

    @Override
    public Class<?> getSourceType(){
        return getSource();
    }

    @Override
    public String toString(){
        String className = getSource().getName();
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
