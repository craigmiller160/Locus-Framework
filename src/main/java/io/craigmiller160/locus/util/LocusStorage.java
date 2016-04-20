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

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.utils.collection.MultiValueMap;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A special storage class where all the class types,
 * instance references, and methods to remotely invoke
 * are stored.
 *
 * Created by craig on 3/15/16.
 */
public class LocusStorage {

    //TODO document the thread safety policy of this class

    private static LocusStorage instance;

    private Map<String,ObjectAndMethod> modelPropSetters;
    private Map<String,ObjectAndMethod> modelPropGetters;

    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private ViewObjectTracker viewInstances;

    private Map<String,Boolean> controllerSingletons;
    private Map<String,Class<?>> controllerTypes;
    private Map<String,Object> controllerSingletonInstances;

    private Map<Object,Object> controllerCallbacks;

    private Class<? extends UIThreadExecutor> uiThreadExecutorType;

    public static LocusStorage getInstance(){
        if(instance == null){
            synchronized (LocusStorage.class){
                if(instance == null){
                    instance = new LocusStorage();
                }
            }
        }
        return instance;
    }

    LocusStorage(){
        modelPropSetters = new HashMap<>();
        modelPropGetters = new HashMap<>();

        viewPropSetters = new MultiValueMap<>();
        viewInstances = new ViewObjectTracker();

        controllerSingletons = new HashMap<>();
        controllerTypes = new HashMap<>();
        controllerSingletonInstances = new HashMap<>();

        controllerCallbacks = new HashMap<>();
    }

    /**
     * Clear all values currently in this storage.
     */
    public synchronized void clear(){
        modelPropSetters.clear();
        modelPropGetters.clear();
        viewPropSetters.clear();
        viewInstances.clear();
        controllerSingletons.clear();
        controllerTypes.clear();
        controllerSingletonInstances.clear();
        controllerCallbacks.clear();
        uiThreadExecutorType = null; //TODO this may cause errors, having this null at any point in time...
    }

    /*
     * UIThreadExecutor section
     */

    public synchronized void setUIThreadExecutorType(Class<? extends UIThreadExecutor> uiThreadExecutorType){
        this.uiThreadExecutorType = uiThreadExecutorType;
    }

    public synchronized Class<? extends UIThreadExecutor> getUIThreadExecutorType(){
        return uiThreadExecutorType;
    }

    /*
     * Get all names section
     */

    public Set<String> getAllModelPropertyNames(){
        Set<String> allPropNames = new HashSet<>();
        synchronized (this){
            allPropNames.addAll(modelPropGetters.keySet());
            allPropNames.addAll(modelPropSetters.keySet());
        }

        return allPropNames;
    }

    public Set<String> getAllViewPropNames(){
        Set<String> allViewPropNames = new HashSet<>();
        synchronized (this){
            allViewPropNames.addAll(viewPropSetters.keySet());
        }

        return allViewPropNames;
    }

    public Set<String> getAllControllerNames(){
        Set<String> allControllerNames = new HashSet<>();
        synchronized (this){
            allControllerNames.addAll(controllerTypes.keySet());
        }

        return allControllerNames;
    }

    /*
     * Model Setter Section
     */

    public synchronized void addModelPropSetter(String propName, ObjectAndMethod oam){
        modelPropSetters.put(propName, oam);
    }

    public synchronized void removeModelPropSetter(String propName){
        modelPropSetters.remove(propName);
    }

    public synchronized ObjectAndMethod getModelPropSetter(String propName){
        //ObjectAndMethod is immutable and a safe reference
        return modelPropSetters.get(propName);
    }

    public Collection<ObjectAndMethod> getAllModelPropSetters(){
        List<ObjectAndMethod> modelPropSetterValues = new ArrayList<>();
        synchronized (this){
            modelPropSetterValues.addAll(modelPropSetters.values());
        }

        return modelPropSetterValues;
    }

    public synchronized int getModelPropSetterCount(){
        return modelPropSetters.size();
    }

    /*
     * Model Getter Section
     */

    public synchronized void addModelPropGetter(String propName, ObjectAndMethod oam){
        modelPropGetters.put(propName, oam);
    }

    public synchronized void removeModelPropGetter(String propName){
        modelPropGetters.remove(propName);
    }

    public synchronized ObjectAndMethod getModelPropGetter(String propName){
        //ObjectAndMethod is immutable and a safe reference
        return modelPropGetters.get(propName);
    }

    public Collection<ObjectAndMethod> getAllModelPropGetters(){
        List<ObjectAndMethod> modelPropGetterValues = new ArrayList<>();
        synchronized (this){
            modelPropGetterValues.addAll(modelPropGetters.values());
        }

        return modelPropGetterValues;
    }

    public synchronized int getModelPropGetterCount(){
        return modelPropGetters.size();
    }

    /*
     * View Setter Section
     */

    public synchronized void addViewPropSetter(String propName, ClassAndMethod cam){
        viewPropSetters.putValue(propName, cam);
    }

    public synchronized void removeViewPropSetter(ClassAndMethod cam){
        viewPropSetters.removeValue(cam);
    }

    public synchronized void removeAllSettersForViewProp(String propName){
        viewPropSetters.remove(propName);
    }

    public Collection<ClassAndMethod> getSettersForViewProp(String propName){
        Collection<ClassAndMethod> result = null;
        synchronized (this){
            Collection<ClassAndMethod> cams = viewPropSetters.get(propName);
            if(cams != null){
                result = new ArrayList<>(cams);
            }
        }

        return result;
    }

    public Collection<ClassAndMethod> getAllViewPropSetters(){
        Collection<ClassAndMethod> result = new ArrayList<>();
        synchronized (this){
            Collection<Collection<ClassAndMethod>> values = viewPropSetters.values();
            for(Collection<ClassAndMethod> value : values){
                result.addAll(value);
            }
        }

        return result;
    }

    public synchronized int getViewPropSetterCount(){
        return viewPropSetters.fullSize();
    }

    /*
     * View Instance Section
     */

    public synchronized void addViewInstance(Class<?> clazz, Object instance){
        WeakReference<?> weakRef = new WeakReference<>(instance);
        viewInstances.putValue(clazz, weakRef);
    }

    public synchronized void removeViewInstance(Object instance){
        Collection<WeakReference<?>> weakRefs = viewInstances.get(instance.getClass());
        if(weakRefs != null){
            for(WeakReference<?> weakRef : weakRefs){
                if(weakRef.get() != null && weakRef.get().equals(instance)){
                    viewInstances.removeValue(weakRef);
                    break;
                }
            }
        }
    }

    public synchronized void removeAllViewInstancesForClass(Class<?> clazz){
        viewInstances.remove(clazz);
    }

    public Collection<WeakReference<?>> getViewInstancesForClass(Class<?> clazz){
        Collection<WeakReference<?>> result = new ArrayList<>();
        synchronized (this){
            Collection<WeakReference<?>> value = viewInstances.get(clazz);
            if(value != null){
                result.addAll(value);
            }
        }

        return result;
    }

    /*
     * Controller Section
     */

    public synchronized void addControllerType(String name, Class<?> clazz, boolean singleton){
        controllerTypes.put(name, clazz);
        controllerSingletons.put(name, singleton);
    }

    public synchronized void removeControllerType(String name){
        controllerTypes.remove(name);
        controllerSingletons.remove(name);
    }

    public synchronized Class<?> getControllerType(String name){
        return controllerTypes.get(name);
    }

    public synchronized boolean isControllerSingleton(String name){
        return controllerSingletons.get(name);
    }

    public Collection<Class<?>> getAllControllerTypes(){
        Collection<Class<?>> result = new ArrayList<>();
        synchronized (this){
            result.addAll(controllerTypes.values());
        }

        return result;
    }

    //TODO consider ultimately deleting this method as unnecessary, it returns an unqualified collection of booleans
    public Collection<Boolean> getAllControllerSingletons(){
        Collection<Boolean> result = new ArrayList<>();
        synchronized (this){
            result.addAll(controllerSingletons.values());
        }

        return result;
    }

    public synchronized int getControllerCount(){
        return controllerTypes.size();
    }

    /*
     * Controller Singleton Instance methods
     */

    public synchronized void addControllerSingletonInstance(String name, Object controller){
        controllerSingletonInstances.put(name, controller);
    }

    public synchronized void remoteControllerSingletonIntance(String name){
        controllerSingletonInstances.remove(name);
    }

    public synchronized Object getControllerSingletonInstance(String name){
        //The Object retrieved may not be thread-safe, but a copy should NOT be returned because the whole point of this is a shared reference
        return controllerSingletonInstances.get(name);
    }

    /*
     * Controller Callback Methods
     */

    public synchronized void addControllerCallback(Object controller, Object callback){
        controllerCallbacks.put(controller, callback);
    }

    public synchronized Object getControllerCallback(Object controller){
        //The Object retrieved may not be thread-safe, but a copy should NOT be returned because the whole point of this is a shared reference
        return controllerCallbacks.get(controller);
    }

    public synchronized void removeControllerCallback(Object controller){
        controllerCallbacks.remove(controller);
    }

}
