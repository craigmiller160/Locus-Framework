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

/**
 * A simple utility class to instantiate
 * objects, wrapping any exceptions that occur
 * in a LocusReflectiveException.
 *
 * Created by craigmiller on 3/19/16.
 */
public class ObjectCreator {

    public static <T>  T instantiateClass(Class<T> type) throws LocusReflectiveException{
        T result = null;
        try{
            result = type.newInstance();
        }
        catch(InstantiationException | IllegalAccessException ex){
            throw new LocusReflectiveException("Unable to instantiate class: " + type.getName(), ex);
        }
        return result;
    }

}
