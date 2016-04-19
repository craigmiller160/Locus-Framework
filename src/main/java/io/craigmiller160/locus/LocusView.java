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
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.InvocationException;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ReflectiveException;
import io.craigmiller160.utils.reflect.RemoteInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by craig on 3/12/16.
 */
class LocusView {

    private static final Logger logger = LoggerFactory.getLogger(LocusView.class);

    private final LocusStorage storage;

    private UIThreadExecutor uiThreadExecutor;

    /**
     * This is the main constructor for this class.
     * It should be used in all cases, except for when
     * testing this class.
     */
    LocusView(){
        this.storage = LocusStorage.getInstance();
        this.uiThreadExecutor = UIThreadExecutorFactory.newInstance().getUIThreadExecutor();
    }

    /**
     * This is a special constructor for use
     * in testing this class. It allows a mocked
     * LocusStorage to be passed to it externally,
     * plus a mocked UIThreadExecutorFactory.
     *
     * @param storage the LocusStorage.
     * @param factory the UIThreadExecutorFactory.
     */
    LocusView(LocusStorage storage, UIThreadExecutorFactory factory){
        this.storage = storage;
        this.uiThreadExecutor = factory.getUIThreadExecutor();
    }

    public void registerView(Object view){
        storage.addViewInstance(view.getClass(), view);
    }

    /**
     * Set a value in any view instances that display the
     * specified property.
     *
     * @param propName the name of the property.
     * @param value the value(s) of the property.
     * @throws LocusException if an error occurs.
     */
    public void setValue(String propName, Object... values) throws LocusException{
        uiThreadExecutor.executeOnUIThread(new SetValueTask(storage, propName, values));
    }

    /**
     * The process of setting a value in a View, wrapped in
     * an implementation of Runnable so it can be executed
     * on the appropriate UI Thread.
     */
    private static class SetValueTask implements Runnable{

        private String propName;
        private Object[] values;
        private LocusStorage storage;

        public SetValueTask(LocusStorage storage, String propName, Object... values){
            this.storage = storage;
            this.propName = propName;
            this.values = values;
        }

        @Override
        public void run() {
            Collection<ClassAndMethod> setters = storage.getSettersForViewProp(propName);
            if(setters == null || setters.size() <= 0){
                throw new ReflectiveException("No setters available in registered views to invoke for property. Property Name: " + propName);
            }

            boolean success = false;
            for(ClassAndMethod cam : setters){
                Collection<WeakReference<?>> viewInstances = storage.getViewInstancesForClass(cam.getSourceType());
                if(viewInstances != null && viewInstances.size() > 0){
                    for(WeakReference<?> weakRef : viewInstances){
                        Object ref = weakRef.get();
                        if(ref != null){
                            ObjectAndMethod oam = new ObjectAndMethod(ref, cam.getMethod());
                            try{
                                RemoteInvoke.invokeMethod(oam, values);
                                success = true;
                            }
                            catch(InvocationException ex){
                                //InvocationExceptions are when the method was successfully invoked, but during its operation an exception occurred
                                //This should NOT be swallowed, and should be propagated
                                throw ex;
                            }
                            catch(ReflectiveException ex){
                                logger.trace("Failed to invoke view setter method. Note that certain invocations are expected to fail.\n" +
                                        "   Method: {} | Param: {}", oam.getMethod(), Arrays.toString(values), ex);
                            }
                        }
                    }
                }
            }

            if(!success){
                throw new ReflectiveException("Unable to successfully invoke any view setter for property. Check TRACE level logs for details");
            }
        }
    }

}
