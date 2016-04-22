/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

    private static LocusStorage instance;

    private Map<String,ObjectAndMethod> modelPropSetters;
    private Map<String,ObjectAndMethod> modelPropGetters;
    private Map<String,ObjectAndMethod> modelPropAdders;
    private Map<String,ObjectAndMethod> modelPropRemovers;

    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private MultiValueMap<String,ClassAndMethod> viewPropAdders;
    private MultiValueMap<String,ClassAndMethod> viewPropRemovers;
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
        modelPropAdders = new HashMap<>();
        modelPropRemovers = new HashMap<>();

        viewPropSetters = new MultiValueMap<>();
        viewPropAdders = new MultiValueMap<>();
        viewPropRemovers = new MultiValueMap<>();
        viewInstances = new ViewObjectTracker();

        controllerSingletons = new HashMap<>();
        controllerTypes = new HashMap<>();
        controllerSingletonInstances = new HashMap<>();

        controllerCallbacks = new HashMap<>();
    }

    /**
     * Clear all values currently in this storage.
     *
     * IMPORTANT: Clearly all values breaks the Locus Framework.
     * This method should only be used if a re-initialization is
     * being performed, and the values are about to be re-populated.
     */
    public synchronized void clear(){
        modelPropSetters.clear();
        modelPropGetters.clear();
        modelPropAdders.clear();
        modelPropRemovers.clear();

        viewPropSetters.clear();
        viewPropAdders.clear();
        viewPropRemovers.clear();
        viewInstances.clear();

        controllerSingletons.clear();
        controllerTypes.clear();
        controllerSingletonInstances.clear();
        controllerCallbacks.clear();

        uiThreadExecutorType = null;
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
            allPropNames.addAll(modelPropAdders.keySet());
            allPropNames.addAll(modelPropRemovers.keySet());
        }

        return allPropNames;
    }

    public Set<String> getAllViewPropNames(){
        Set<String> allViewPropNames = new HashSet<>();
        synchronized (this){
            allViewPropNames.addAll(viewPropSetters.keySet());
            allViewPropNames.addAll(viewPropAdders.keySet());
            allViewPropNames.addAll(viewPropRemovers.keySet());
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
     * Model Adder Section
     */

    public synchronized void addModelPropAdder(String propName, ObjectAndMethod oam){
        modelPropAdders.put(propName, oam);
    }

    public synchronized void removeModelPropAdder(String propName){
        modelPropAdders.remove(propName);
    }

    public synchronized ObjectAndMethod getModelPropAdder(String propName){
        return modelPropAdders.get(propName);
    }

    public Collection<ObjectAndMethod> getAllModelPropAdders(){
        List<ObjectAndMethod> modelPropAdderValues = new ArrayList<>();
        synchronized (this){
            modelPropAdderValues.addAll(modelPropAdders.values());
        }
        return modelPropAdderValues;
    }

    public synchronized int getModelPropAdderCount(){
        return modelPropAdders.size();
    }

    /*
     * Model Remover Section
     */

    public synchronized void addModelPropRemover(String propName, ObjectAndMethod oam){
        modelPropRemovers.put(propName, oam);
    }

    public synchronized void removeModelPropRemover(String propName){
        modelPropRemovers.remove(propName);
    }

    public synchronized ObjectAndMethod getModelPropRemover(String propName){
        return modelPropRemovers.get(propName);
    }

    public Collection<ObjectAndMethod> getAllModelPropRemovers(){
        List<ObjectAndMethod> modelPropRemoverValues = new ArrayList<>();
        synchronized (this){
            modelPropRemoverValues.addAll(modelPropRemovers.values());
        }
        return modelPropRemoverValues;
    }

    public synchronized int getModelPropRemoverCount(){
        return modelPropRemovers.size();
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
     * View Adder Section
     */

    public synchronized void addViewPropAdder(String propName, ClassAndMethod cam){
        viewPropAdders.putValue(propName, cam);
    }

    public synchronized void removeViewPropAdder(ClassAndMethod cam){
        viewPropAdders.removeValue(cam);
    }

    public synchronized void removeAllAddersForViewProp(String propName){
        viewPropAdders.remove(propName);
    }

    public synchronized Collection<ClassAndMethod> getAddersForViewProp(String propName){
        Collection<ClassAndMethod> result = null;
        synchronized (this){
            Collection<ClassAndMethod> cams = viewPropAdders.get(propName);
            if(cams != null){
                result = new ArrayList<>(cams);
            }
        }

        return result;
    }

    public Collection<ClassAndMethod> getAllViewPropAdders(){
        Collection<ClassAndMethod> result = new ArrayList<>();
        synchronized (this){
            Collection<Collection<ClassAndMethod>> values = viewPropAdders.values();
            for(Collection<ClassAndMethod> value : values){
                result.addAll(value);
            }
        }

        return result;
    }

    public synchronized int getViewPropAdderCount(){
        return viewPropAdders.fullSize();
    }

    /*
     * View Remover Section
     */

    public synchronized void addViewPropRemover(String propName, ClassAndMethod cam){
        viewPropRemovers.putValue(propName, cam);
    }

    public synchronized void removeViewPropRemover(ClassAndMethod cam){
        viewPropRemovers.removeValue(cam);
    }

    public synchronized void removeAllRemoversForViewProp(String propName){
        viewPropRemovers.remove(propName);
    }

    public synchronized Collection<ClassAndMethod> getRemoversForViewProp(String propName){
        Collection<ClassAndMethod> result = null;
        synchronized (this){
            Collection<ClassAndMethod> cams = viewPropRemovers.get(propName);
            if(cams != null){
                result = new ArrayList<>(cams);
            }
        }

        return result;
    }

    public Collection<ClassAndMethod> getAllViewPropRemovers(){
        Collection<ClassAndMethod> result = new ArrayList<>();
        synchronized (this){
            Collection<Collection<ClassAndMethod>> values = viewPropRemovers.values();
            for(Collection<ClassAndMethod> value : values){
                result.addAll(value);
            }
        }

        return result;
    }

    public synchronized int getViewPropRemoverCount(){
        return viewPropRemovers.fullSize();
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
