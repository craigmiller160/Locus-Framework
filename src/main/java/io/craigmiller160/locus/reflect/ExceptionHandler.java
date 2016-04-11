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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A special class that parses Exceptions where
 * the exact nature (Checked, unchecked, error, etc)
 * is unknown. It parses them and properly rethrows them.
 *
 * Created by craigmiller on 4/8/16.
 */
public class ExceptionHandler {

    /**
     * This method parses and rethrows the Exception in
     * the appropriate manner based on its type.
     *
     * Errors are rethrown as Errors.
     * RuntimeExceptions are rethrown as RuntimeExceptions.
     * Exceptions are wrapped in LocusInvocationException and rethrown.
     *
     * @param ex the Exception to parse and rethrow.
     * @throws LocusInvocationException if a checked exception is the cause.
     * @throws RuntimeException if an unchecked exception is the cause.
     * @throws Error if an error is the cause.
     */
    public static void parseAndRethrowException(Throwable ex){
        if(ex instanceof Error){
            throw (Error) ex;
        }
        else if(ex instanceof RuntimeException){
            throw (RuntimeException) ex;
        }
        else{
            throw new LocusInvocationException(ex);
        }
    }

}
