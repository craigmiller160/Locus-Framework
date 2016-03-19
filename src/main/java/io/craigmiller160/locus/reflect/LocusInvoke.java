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

/**
 * Special class for reflectively invoking methods.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvoke {

    public static Object invokeMethod(ObjectAndMethod oam, Object...params) throws LocusReflectiveException{
        Object result = null;
        if(MethodUtils.isValidInvocation(oam.getMethod(), params)){
            if(oam.isMethodVarArgs()){
                params = MethodUtils.convertParamsForVarArgsMethod(oam.getMethod(), params);
            }

            try{
                result = oam.getMethod().invoke(oam.getSource(), params);
            }
            catch(InvocationTargetException ex){
                //TODO logging?
                throw new LocusInvocationException("Method invoked threw exception", ex.getCause());
            }
            catch(ReflectiveOperationException ex){
                //TODO logging?
                throw new LocusReflectiveException("Unable to reflectively invoke method " + oam.getMethod().getName() +
                        " on " + oam.getSource().getClass().getName(), ex);
            }
        }
        else{
            throw new LocusReflectiveException("Parameters provided for method " + oam.getMethod().getName() + " do not match what is expected.");
        }

        return result;
    }

}
