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
 * <p>The Locus component that manages access to the view classes.</p>
 *
 * @author craigmiller
 * @version 1.2
 */
class LocusView {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusView.class);

    /**
     * The LocusStorage, with all the method references to reflectively invoke.
     */
    private final LocusStorage storage;

    /**
     * The UIThreadExecutor, which ensures that all
     * operations are safely executed on the appropriate
     * UI Thread.
     */
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

    /**
     * Register the provided view with the storage, so it
     * can be accessed later on.
     *
     * @param view the view to register.
     */
    public void registerView(Object view){
        storage.addViewInstance(view);
    }

    /**
     * Unregister the provided view. If it is registered
     * with the storage, it will be removed. Otherwise,
     * nothing will occur.
     *
     * @param view the view to unregister.
     */
    public void unregisterView(Object view){
        storage.removeViewInstance(view);
    }

    /**
     * Set a value in any view instances that display the
     * specified property.
     *
     * @param propName the name of the property.
     * @param values the value(s) of the property.
     * @throws ReflectiveException if an error occurs.
     */
    public void setValue(String propName, Object... values) throws ReflectiveException{
        uiThreadExecutor.executeOnUIThread(new SetValueTask(storage, propName, values));
    }

    /**
     * Add a value to a collection in any view instances
     * that display the specified property.
     *
     * @param propName the name of the property.
     * @param values the value(s) to add.
     * @throws ReflectiveException if an error occurs.
     */
    public void addValue(String propName, Object... values) throws ReflectiveException{
        uiThreadExecutor.executeOnUIThread(new AddValueTask(storage, propName, values));
    }

    /**
     * Remove a value from a collection in any view
     * instances that display the specified property.
     *
     * @param propName the name of the property.
     * @param values the value(s) to add.
     * @throws ReflectiveException if an error occurs.
     */
    public void removeValue(String propName, Object... values) throws ReflectiveException{
        uiThreadExecutor.executeOnUIThread(new RemoveValueTask(storage, propName, values));
    }

    /**
     * Execute the methods, if they match the provided arguments, in
     * any view instances that have them.
     *
     * @param storage the LocusStorage.
     * @param methods the methods to execute.
     * @param values the arguments for the methods.
     * @return true if the operation successfully invoked at least one method.
     * @throws ReflectiveException if an error occurs.
     */
    private static boolean executeMethods(LocusStorage storage, Collection<ClassAndMethod> methods, Object...values) throws ReflectiveException{
        boolean success = false;
        for(ClassAndMethod cam : methods) {
            Collection<WeakReference<?>> viewInstances = storage.getViewInstancesForClass(cam.getSourceType());
            if (viewInstances != null && viewInstances.size() > 0) {
                for (WeakReference<?> weakRef : viewInstances) {
                    Object ref = weakRef.get();
                    if (ref != null) {
                        ObjectAndMethod oam = new ObjectAndMethod(ref, cam.getMethod());
                        try {
                            RemoteInvoke.validateAndInvokeMethod(oam, values);
                            success = true;
                        } catch (InvocationException ex) {
                            //InvocationExceptions are when the method was successfully invoked, but during its operation an exception occurred
                            //This should NOT be swallowed, and should be propagated
                            throw ex;
                        } catch (ReflectiveException ex) {
                            logger.trace("Failed to invoke view method method. Note that certain invocations are expected to fail.\n" +
                                    "   Method: {} | Param: {}", oam.getMethod(), Arrays.toString(values), ex);
                        }
                    }
                }
            }

        }

        return success;
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

            boolean success = executeMethods(storage, setters, values);

            if(!success){
                throw new ReflectiveException("Unable to successfully invoke any view setter for property. Check TRACE level logs for details");
            }
        }
    }

    /**
     * The process of adding a value to a collection in
     * a View, wrapped in an implementation of Runnable
     * so it can be executed on the appropriate UI Thread.
     */
    private static class AddValueTask implements Runnable{

        private String propName;
        private Object[] values;
        private LocusStorage storage;

        public AddValueTask(LocusStorage storage, String propName, Object... values){
            this.storage = storage;
            this.propName = propName;
            this.values = values;
        }

        @Override
        public void run() {
            Collection<ClassAndMethod> adders = storage.getAddersForViewProp(propName);
            if(adders == null || adders.size() <= 0){
                throw new ReflectiveException("No adders available in registered views to invoke for property. Property Name: " + propName);
            }

            boolean success = executeMethods(storage, adders, values);

            if(!success){
                throw new ReflectiveException("Unable to successfully invoke any view adder for property. Check TRACE level logs for details");
            }
        }
    }

    /**
     * The process of removing a value from a collection
     * in a View, wrapped in an implementation of Runnable
     * so it can be executed on the appropriate UI Thread.
     */
    public static class RemoveValueTask implements Runnable{

        private String propName;
        private Object[] values;
        private LocusStorage storage;

        public RemoveValueTask(LocusStorage storage, String propName, Object... values){
            this.storage = storage;
            this.propName = propName;
            this.values = values;
        }

        @Override
        public void run() {
            Collection<ClassAndMethod> removers = storage.getRemoversForViewProp(propName);
            if(removers == null || removers.size() <= 0){
                throw new ReflectiveException("No removers available in registered views to invoke for property. Property Name: " + propName);
            }

            boolean success = executeMethods(storage, removers, values);

            if(!success){
                throw new ReflectiveException("Unable to successfully invoke any view remover for property. Check TRACE level logs for details");
            }
        }
    }

}
