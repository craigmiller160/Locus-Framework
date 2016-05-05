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

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ReflectiveException;
import io.craigmiller160.utils.reflect.RemoteInvoke;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>A special callback class for the LocusController.
 * It wraps around an instance of a view, which it
 * is then able to reflective invoke getters on.</p>
 *
 * <p>All operations interacting with the view callback class will be done via the
 * UIThreadExecutor implementation registered with this framework.
 * This ensures that all interactions are always done on the correct
 * UI Thread. If no implementation is registered, then the default
 * NoUIThreadExecutor will be used.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class is mostly thread-safe. It has
 * no mutable state, and all interactions with the view callback
 * object is done on the appropriate UI thread. However, the
 * safety of the interactions with the view class are only as good
 * as the provided UIThreadExecutor. If a bad one is provided, or
 * if one is not provided, then the thread safety of the interactions
 * with the callback object cannot be guaranteed.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
@ThreadSafe
public class LocusControllerCallback {

    /**
     * The Callback object.
     */
    private final Object callback;

    /**
     * The UIThreadExecutor for wrapping operations on
     * the appropriate UI Thread.
     */
    private final UIThreadExecutor uiThreadExecutor;

    /**
     * Create a new instance of LocusControllerCallback.
     *
     * @param callback the callback object.
     * @param uiThreadExecutor the UIThreadExecutor.
     */
    LocusControllerCallback(Object callback, UIThreadExecutor uiThreadExecutor){
        this.callback = callback;
        this.uiThreadExecutor = uiThreadExecutor;
    }

    /**
     * Get a value from the callback object, by reflectively
     * invoking a getter method with the optionally provided arguments.
     *
     * @param propName the property name.
     * @param args the optional arguments.
     * @return the return value of the method.
     * @throws LocusException if an error occurs.
     */
    public Object getValue(String propName, Object...args) throws LocusException{
        return uiThreadExecutor.executeOnUIThreadWithResult(new GetValueTask(callback, propName, args));
    }

    /**
     * Package private method exists
     * only for use in testing.
     *
     * @return the callback object.
     */
    Object getCallback(){
        return callback;
    }

    /**
     * Get a value from the callback object, by reflectively invoking
     * a getter method with the optionally provided arguments. The
     * value will be returned in the specified class type, unless the
     * types do not match, in which case an exception will be thrown.
     *
     * @param propName the name of the property.
     * @param resultType the type of the result.
     * @param args the optional argument.
     * @param <T> the type of the result.
     * @return the return value.
     * @throws LocusException if an error occurs.
     */
    public <T> T getValue(String propName, Class<?> resultType, Object...args) throws LocusException{
        Object result = getValue(propName, args);
        if(result == null){
            return null;
        }

        if(!resultType.isAssignableFrom(result.getClass())){
            throw new LocusInvalidTypeException(
                    String.format("Return value for getting \"%1$s\" doesn't match expected type. Expected: %2$s | Actual: %3$s",
                            propName, resultType.getName(), result.getClass().getName()));
        }

        return (T) result;
    }

    /**
     * <p>The get value operation for this class, wrapped in
     * an implementation of the Callable interface. This
     * allows for the operation to be executed synchronously
     * on the UI Thread, using a provided implementation of
     * UIThreadExecutor.</p>
     */
    private static class GetValueTask implements Callable<Object>{

        private Object callback;
        private String propName;
        private Object[] args;

        public GetValueTask(Object callback, String propName, Object... args){
            this.callback = callback;
            this.propName = propName;
            this.args = args;
        }

        @Override
        public Object call() throws Exception {
            Object result = null;

            //Get all the parameter types for the method
            List<Class<?>> paramTypes = new ArrayList<>();
            for(Object o : args){
                paramTypes.add(o.getClass());
            }

            Class<?>[] types = new Class<?>[paramTypes.size()];
            paramTypes.toArray(types);

            //Try getting the method with a "get" prefix. Swallow exception because a separate one gets thrown if the method is ultimately null
            Method method = null;
            try{
                method = callback.getClass().getMethod("get" + propName, types);
            }
            catch(NoSuchMethodException ex){}

            //Try getting the method with an "is" prefix. Swallow exception because a separate one gets thrown if the method is ultimately null
            if(method == null){
                try{
                    method = callback.getClass().getMethod("is" + propName, types);
                }
                catch(NoSuchMethodException ex){}
            }

            if(method == null){
                throw new ReflectiveException(String.format("Callback object has no method named either get%1$s or is%1$s", propName));
            }

            //Attempt to invoke the method and get the result
            ObjectAndMethod oam = new ObjectAndMethod(callback, method);
            result = RemoteInvoke.validateAndInvokeMethod(oam, args);

            return result;
        }
    }

}
