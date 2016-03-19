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
import java.util.Map;

/**
 * Created by craig on 3/15/16.
 */
public class LocusStorage {

    private static LocusStorage instance;

    private MultiValueMap<String,ObjectAndMethod> modelPropSetters;
    private MultiValueMap<String,ObjectAndMethod> modelPropGetters;

    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private MultiValueMap<String,ClassAndMethod> viewPropGetters;
    private ViewObjectTracker viewInstances;

    private Map<String,Boolean> controllerSingletons;
    private Map<String,Class<?>> controllerTypes;

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
        modelPropSetters = new MultiValueMap<>();
        modelPropGetters = new MultiValueMap<>();

        viewPropSetters = new MultiValueMap<>();
        viewPropGetters = new MultiValueMap<>();
        viewInstances = new ViewObjectTracker();

        controllerSingletons = new HashMap<>();
        controllerTypes = new HashMap<>();
    }

    /*
     * Model Setter Section
     */

    public void addModelPropSetter(String propName, ObjectAndMethod oam){
        modelPropSetters.putValue(propName, oam);
    }

    public void removeModelPropSetter(ObjectAndMethod oam){
        modelPropSetters.removeValue(oam);
    }

    public void removeAllSettersForModelProp(String propName){
        modelPropSetters.remove(propName);
    }

    public Collection<ObjectAndMethod> getSettersForModelProp(String propName){
        return modelPropSetters.get(propName);
    }

    public MultiValueMap<String,ObjectAndMethod> getAllModelPropSetters(){
        return modelPropSetters;
    }

    /*
     * Model Getter Section
     */

    public void addModelPropGetter(String propName, ObjectAndMethod oam){
        modelPropGetters.putValue(propName, oam);
    }

    public void removeModelPropGetter(ObjectAndMethod oam){
        modelPropGetters.removeValue(oam);
    }

    public void removeAllGettersForModelProp(String propName){
        modelPropGetters.remove(propName);
    }

    public Collection<ObjectAndMethod> getGettersForModelProp(String propName){
        return modelPropGetters.get(propName);
    }

    public MultiValueMap<String,ObjectAndMethod> getAllModelPropGetters(){
        return modelPropGetters;
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

    /*
     * View Getter Section
     */

    public void addViewPropGetter(String propName, ClassAndMethod cam){
        viewPropGetters.putValue(propName, cam);
    }

    public void removeViewPropGetter(ClassAndMethod cam){
        viewPropGetters.removeValue(cam);
    }

    public void removeAllGettersForViewProp(String propName){
        viewPropGetters.remove(propName);
    }

    public Collection<ClassAndMethod> getGettersForViewProp(String propName){
        return viewPropGetters.get(propName);
    }

    public MultiValueMap<String,ClassAndMethod> getAllViewPropGetters(){
        return viewPropGetters;
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

}
