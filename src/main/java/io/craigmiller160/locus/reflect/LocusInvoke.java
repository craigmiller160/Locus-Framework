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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Special class for reflectively invoking methods.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvoke {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusInvoke.class);

    /**
     * Reflectively invoke any method contained in the holder
     * argument, passing to it the provided parameters.
     *
     * @param oam the holder of the method to invoke and its source.
     * @param params the parameters to use to invoke the method.
     * @return the result of the method invocation, or null if there
     *          was none.
     * @throws LocusReflectiveException if unable to reflectively invoke the method.
     */
    public static Object invokeMethod(ObjectAndMethod oam, Object...params) throws LocusReflectiveException{
        Object result = null;
        if(MethodUtils.isValidInvocation(oam.getMethod(), params)){
            if(oam.isMethodVarArgs()){
                params = MethodUtils.convertParamsForVarArgsMethod(oam.getMethod(), params);
            }

            try{
                result = oam.getMethod().invoke(oam.getSource(), params);
                logger.trace("Successfully invoked method. Method: {} | Params: {}", oam.getMethod(), Arrays.toString(params));
            }
            catch(InvocationTargetException ex){
                InvocationExceptionHandler.rethrowInvocationTargetExceptionCause(ex);
            }
            catch(ReflectiveOperationException ex){
                throw new LocusReflectiveException("Unable to reflectively invoke method " + oam.getMethod().getName() +
                        " on " + oam.getSource().getClass().getName(), ex);
            }
        }
        else{
            StringBuilder builder = new StringBuilder("[");
            for(Object o : params){
                builder.append(o.getClass() + ", ");
            }
            if(builder.length() > 2 && builder.substring(builder.length() - 2).equals(", ")){
                builder.delete(builder.length() - 2, builder.length());
                builder.append("]");
            }

            throw new LocusReflectiveException("Parameters provided for method " + oam.getMethod().getName() + " do not match what is expected.\n" +
                    "   Expected: " + Arrays.toString(oam.getMethod().getParameterTypes()) + " | Actual: " + builder.toString());
        }

        return result;
    }

}
