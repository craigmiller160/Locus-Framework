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

import io.craigmiller160.locus.reflect.ClassAndMethod;
import io.craigmiller160.locus.reflect.ObjectAndMethod;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private ViewObjectTracker viewInstances;

    private Map<String,Boolean> controllerSingletons;
    private Map<String,Class<?>> controllerTypes;
    private Map<String,Object> controllerSingletonInstances;

    private Map<Object,Object> controllerCallbacks;

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

    public Set<String> getAllModelPropertyNames(){
        Set<String> allPropNames = new HashSet<>();
        allPropNames.addAll(modelPropGetters.keySet());
        allPropNames.addAll(modelPropSetters.keySet());
        return allPropNames;
    }

    public Set<String> getAllViewPropNames(){
        return viewPropSetters.keySet();
    }

    public Set<String> getAllControllerNames(){
        return controllerTypes.keySet();
    }

    /*
     * Model Setter Section
     */

    public void addModelPropSetter(String propName, ObjectAndMethod oam){
        modelPropSetters.put(propName, oam);
    }

    public void removeModelPropSetter(String propName){
        modelPropSetters.remove(propName);
    }

    public ObjectAndMethod getModelPropSetter(String propName){
        return modelPropSetters.get(propName);
    }

    public Map<String,ObjectAndMethod> getAllModelPropSetters(){
        return modelPropSetters;
    }

    public int getModelPropSetterCount(){
        return modelPropSetters.size();
    }

    /*
     * Model Getter Section
     */

    public void addModelPropGetter(String propName, ObjectAndMethod oam){
        modelPropGetters.put(propName, oam);
    }

    public void removeModelPropGetter(String propName){
        modelPropGetters.remove(propName);
    }

    public ObjectAndMethod getModelPropGetter(String propName){
        return modelPropGetters.get(propName);
    }

    public Map<String,ObjectAndMethod> getAllModelPropGetters(){
        return modelPropGetters;
    }

    public int getModelPropGetterCount(){
        return modelPropGetters.size();
    }

    /*
     * View Setter Section
     */

    public void addViewPropSetter(String propName, ClassAndMethod cam){
        viewPropSetters.putValue(propName, cam);
    }

    public void removeViewPropSetter(ClassAndMethod cam){
        viewPropSetters.removeValue(cam);
    }

    public void removeAllSettersForViewProp(String propName){
        viewPropSetters.remove(propName);
    }

    public Collection<ClassAndMethod> getSettersForViewProp(String propName){
        return viewPropSetters.get(propName);
    }

    public MultiValueMap<String,ClassAndMethod> getAllViewPropSetters(){
        return viewPropSetters;
    }

    public int getViewPropSetterCount(){
        return viewPropSetters.fullSize();
    }

    /*
     * View Instance Section
     */

    public void addViewInstance(Class<?> clazz, Object instance){
        WeakReference<?> weakRef = new WeakReference<>(instance);
        viewInstances.putValue(clazz, weakRef);
    }

    public void removeViewInstance(Object instance){
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

    public void removeAllViewInstancesForClass(Class<?> clazz){
        viewInstances.remove(clazz);
    }

    public Collection<WeakReference<?>> getViewInstancesForClass(Class<?> clazz){
        return viewInstances.get(clazz);
    }

    /*
     * Controller Section
     */

    public void addControllerType(String name, Class<?> clazz, boolean singleton){
        controllerTypes.put(name, clazz);
        controllerSingletons.put(name, singleton);
    }

    public void removeControllerType(String name){
        controllerTypes.remove(name);
        controllerSingletons.remove(name);
    }

    public Class<?> getControllerType(String name){
        return controllerTypes.get(name);
    }

    public boolean isControllerSingleton(String name){
        return controllerSingletons.get(name);
    }

    public Map<String,Class<?>> getAllControllerTypes(){
        return controllerTypes;
    }

    public Map<String,Boolean> getAllControllerSingletons(){
        return controllerSingletons;
    }

    public int getControllerCount(){
        return controllerTypes.size();
    }

    /*
     * Controller Singleton Instance methods
     */

    public void addControllerSingletonInstance(String name, Object controller){
        controllerSingletonInstances.put(name, controller);
    }

    public void remoteControllerSingletonIntance(String name){
        controllerSingletonInstances.remove(name);
    }

    public Object getControllerSingletonInstance(String name){
        return controllerSingletonInstances.get(name);
    }

    /*
     * Controller Callback Methods
     */

    public void addControllerCallback(Object controller, Object callback){
        controllerCallbacks.put(controller, callback);
    }

    public Object getControllerCallback(Object controller){
        return controllerCallbacks.get(controller);
    }

    public void removeControllerCallback(Object controller){
        controllerCallbacks.remove(controller);
    }

}
