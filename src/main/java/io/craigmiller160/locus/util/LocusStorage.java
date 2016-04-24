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

import javax.annotation.concurrent.ThreadSafe;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>A special storage class where all the class types,
 * instance references, and methods to remotely invoke
 * are stored.</p>
 *
 * <p>The methods stored here are organized in four types,
 * based on the standard POJO design pattern:</p>
 *
 * <p><b>GETTER:</b> Any method that begins with "get" or "is".
 * These methods return a value.</p>
 *
 * <p><b>SETTER:</b> Any method that begins with "set". These
 * methods update a value.</p>
 *
 * <p><b>ADDER:</b> Any method that begins with "add". These
 * methods are used to add a value to a collection.</p>
 *
 * <p><b>REMOVER:</b> Any method that begins with "remove". These
 * methods are used to remove a value from a collection.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class is thread-safe. All its fields are synchronized
 * on the intrinsic lock of this class. Any methods returning
 * a collection return a copy of the underlying collection, to
 * protect the collection reference. The contents of the collections
 * within this class are also MOSTLY immutable objects, making them
 * safe as well. However, not all of them are, and this should be
 * watched carefully to avoid compromising thread safety.</p>
 *
 * @author craigmiller
 * @version 1.2
 */
@ThreadSafe
public class LocusStorage {

    /**
     * The singleton instance of this class.
     */
    private static LocusStorage instance;

    /*
     * The collections of methods of models for reflective invocation.
     */
    private MultiValueMap<String,ObjectAndMethod> modelPropSetters;
    private MultiValueMap<String,ObjectAndMethod> modelPropGetters;
    private MultiValueMap<String,ObjectAndMethod> modelPropAdders;
    private MultiValueMap<String,ObjectAndMethod> modelPropRemovers;

    /*
     * The collections of methods of views for reflective invocation.
     */
    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private MultiValueMap<String,ClassAndMethod> viewPropAdders;
    private MultiValueMap<String,ClassAndMethod> viewPropRemovers;
    private ViewObjectTracker viewInstances;

    /*
     * The collections of values for working with controllers.
     */
    private Map<String,Boolean> controllerSingletons;
    private Map<String,Class<?>> controllerTypes;
    private Map<String,Object> controllerSingletonInstances;
    private Map<Object,Object> controllerCallbacks;

    /**
     * The UIThreadExecutor class type.
     */
    private Class<? extends UIThreadExecutor> uiThreadExecutorType;

    /**
     * Get the instance of the LocusStorage.
     *
     * @return the LocusStorage.
     */
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

    /**
     * Create a new LocusStorage. As a singleton,
     * LocusStorage should only be accessed via
     * the getInstance() method. This package-private
     * controller exists for testing purposes.
     */
    LocusStorage(){
        modelPropSetters = new MultiValueMap<>();
        modelPropGetters = new MultiValueMap<>();
        modelPropAdders = new MultiValueMap<>();
        modelPropRemovers = new MultiValueMap<>();

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

    /**
     * Set the class type of the UIThreadExecutor for Locus to use.
     *
     * @param uiThreadExecutorType the class type of the UIThreadExecutor.
     */
    public synchronized void setUIThreadExecutorType(Class<? extends UIThreadExecutor> uiThreadExecutorType){
        this.uiThreadExecutorType = uiThreadExecutorType;
    }

    /**
     * Get the class type of the UIThreadExecutor.
     *
     * @return the class type of the UIThreadExecutor.
     */
    public synchronized Class<? extends UIThreadExecutor> getUIThreadExecutorType(){
        return uiThreadExecutorType;
    }

    /*
     * Get all names section
     */

    /**
     * Get the names of all unique model properties.
     *
     * @return the model property names.
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

    /**
     * Get the names of all unique view properties.
     *
     * @return the view property names.
     */
    public Set<String> getAllViewPropNames(){
        Set<String> allViewPropNames = new HashSet<>();
        synchronized (this){
            allViewPropNames.addAll(viewPropSetters.keySet());
            allViewPropNames.addAll(viewPropAdders.keySet());
            allViewPropNames.addAll(viewPropRemovers.keySet());
        }

        return allViewPropNames;
    }

    /**
     * Get the names of all controllers.
     *
     * @return the controller names.
     */
    public synchronized Set<String> getAllControllerNames(){
        return Collections.unmodifiableSet(controllerTypes.keySet());
    }

    /*
     * Model Setter Section
     */

    /**
     * Add a new model property setter method.
     *
     * @param propName the name of the property.
     * @param oam the ObjectAndMethod.
     */
    public synchronized void addModelPropSetter(String propName, ObjectAndMethod oam){
        modelPropSetters.putValue(propName, oam);
    }

    /**
     * Remove a model property setter method.
     *
     * @param oam the ObjectAndMethod.
     */
    public synchronized void removeModelPropSetter(ObjectAndMethod oam){
        modelPropSetters.removeValue(oam);
    }

    /**
     * Remove all model property setter methods for that property.
     *
     * @param propName the property to remove the setters for.
     */
    public synchronized void removeSettersForModelProp(String propName){
        modelPropSetters.remove(propName);
    }

    /**
     * Get all model property setter methods for the provided property.
     *
     * @param propName the name of the property.
     * @return the setter methods for that property.
     */
    public synchronized Collection<ObjectAndMethod> getSettersForModelProp(String propName){
        return Collections.unmodifiableCollection(modelPropSetters.get(propName));
    }

    /**
     * Get all model property setter methods.
     *
     * @return all model property setter methods.
     */
    public synchronized Collection<ObjectAndMethod> getAllModelPropSetters(){
        return Collections.unmodifiableCollection(modelPropSetters.allValues());
    }

    /**
     * Get a count of all model property setter methods.
     *
     * @return a count of model property setter methods.
     */
    public synchronized int getModelPropSetterCount(){
        return modelPropSetters.fullSize();
    }

    /*
     * Model Getter Section
     */

    /**
     * Add a model property getter method.
     *
     * @param propName the name of the property.
     * @param oam the ObjectAndMethod.
     */
    public synchronized void addModelPropGetter(String propName, ObjectAndMethod oam){
        modelPropGetters.putValue(propName, oam);
    }

    /**
     * Remove a model property getter method.
     *
     * @param oam the ObjectAndMethod.
     */
    public synchronized void removeModelPropGetter(ObjectAndMethod oam){
        modelPropGetters.removeValue(oam);
    }

    /**
     * Remove all model property getter methods for the specified model property.
     *
     * @param propName the name of the property.
     */
    public synchronized void removeGettersForModelProp(String propName){
        modelPropGetters.remove(propName);
    }

    /**
     * Get all model property getter methods for the specified model property.
     *
     * @param propName the name of the property.
     * @return all getter methods for the property.
     */
    public synchronized Collection<ObjectAndMethod> getGettersForModelProp(String propName){
        return Collections.unmodifiableCollection(modelPropGetters.get(propName));
    }

    /**
     * Get all model property getter methods.
     *
     * @return all model getter methods.
     */
    public synchronized Collection<ObjectAndMethod> getAllModelPropGetters(){
        return Collections.unmodifiableCollection(modelPropGetters.allValues());
    }

    /**
     * Get the count of all model property getter methods.
     *
     * @return the count of model getter methods.
     */
    public synchronized int getModelPropGetterCount(){
        return modelPropGetters.fullSize();
    }

    /*
     * Model Adder Section
     */

    /**
     * Add a new model property adder method.
     *
     * @param propName the name of the property.
     * @param oam the ObjectAndMethod.
     */
    public synchronized void addModelPropAdder(String propName, ObjectAndMethod oam){
        modelPropAdders.putValue(propName, oam);
    }

    /**
     * Remove a model property adder method.
     *
     * @param oam the ObjectAndMethod.
     */
    public synchronized void removeModelPropAdder(ObjectAndMethod oam){
        modelPropAdders.removeValue(oam);
    }

    /**
     * Remove all model property adder methods for the specified property.
     *
     * @param propName the name of the property.
     */
    public synchronized void removeAddersForModelProp(String propName){
        modelPropAdders.remove(propName);
    }

    /**
     * Get all model adder methods for the property.
     *
     * @param propName the name of the property.
     * @return the model adder methods for that property.
     */
    public synchronized Collection<ObjectAndMethod> getAddersForModelProp(String propName){
        return Collections.unmodifiableCollection(modelPropAdders.get(propName));
    }

    /**
     * Get all model property adder methods.
     *
     * @return all model property adder methods.
     */
    public synchronized Collection<ObjectAndMethod> getAllModelPropAdders(){
        return Collections.unmodifiableCollection(modelPropAdders.allValues());
    }

    /**
     * Get a count of all model property adder methods.
     *
     * @return a count of all model property adder methods.
     */
    public synchronized int getModelPropAdderCount(){
        return modelPropAdders.fullSize();
    }

    /*
     * Model Remover Section
     */

    /**
     * Add a new model property remover method.
     *
     * @param propName the name of the property.
     * @param oam the ObjectAndMethod.
     */
    public synchronized void addModelPropRemover(String propName, ObjectAndMethod oam){
        modelPropRemovers.putValue(propName, oam);
    }

    /**
     * Remove a model property remover method.
     *
     * @param oam the ObjectAndMethod.
     */
    public synchronized void removeModelPropRemover(ObjectAndMethod oam){
        modelPropRemovers.removeValue(oam);
    }

    /**
     * Remove all remover methods for a model property.
     *
     * @param propName the name of the property.
     */
    public synchronized void removeRemoversForModelProp(String propName){
        modelPropRemovers.remove(propName);
    }

    /**
     * Get all remover methods for a model property.
     *
     * @param propName the name of the property.
     * @return all remover methods for the property.
     */
    public synchronized Collection<ObjectAndMethod> getRemoversForModelProp(String propName){
        return Collections.unmodifiableCollection(modelPropRemovers.get(propName));
    }

    /**
     * Get all model property remover methods.
     *
     * @return all model property remover methods.
     */
    public synchronized Collection<ObjectAndMethod> getAllModelPropRemovers(){
        return Collections.unmodifiableCollection(modelPropRemovers.allValues());
    }

    public synchronized int getModelPropRemoverCount(){
        return modelPropRemovers.fullSize();
    }

    /*
     * View Setter Section
     */

    /**
     * Add a new view property setter method.
     *
     * @param propName the name of the property.
     * @param cam the ClassAndMethod.
     */
    public synchronized void addViewPropSetter(String propName, ClassAndMethod cam){
        viewPropSetters.putValue(propName, cam);
    }

    /**
     * Remove a view property setter method.
     *
     * @param cam the ClassAndMethod.
     */
    public synchronized void removeViewPropSetter(ClassAndMethod cam){
        viewPropSetters.removeValue(cam);
    }

    /**
     * Remove all setter methods for a view property.
     *
     * @param propName the name of the property.
     */
    public synchronized void removeAllSettersForViewProp(String propName){
        viewPropSetters.remove(propName);
    }

    /**
     * Get all setter methods for a view property.
     *
     * @param propName the name of the property.
     * @return all setter methods for the property.
     */
    public synchronized Collection<ClassAndMethod> getSettersForViewProp(String propName){
        return Collections.unmodifiableCollection(viewPropSetters.get(propName));
    }

    /**
     * Get all view property setter methods.
     *
     * @return all view property setter methods.
     */
    public synchronized Collection<ClassAndMethod> getAllViewPropSetters(){
        return Collections.unmodifiableCollection(viewPropSetters.allValues());
    }

    /**
     * Get a count of all view property setter methods.
     *
     * @return a count of all view property setter methods.
     */
    public synchronized int getViewPropSetterCount(){
        return viewPropSetters.fullSize();
    }

    /*
     * View Instance Section
     */

    /**
     * Add a new instance of a view class. The object is wrapped
     * in a WeakReference, so being included in this storage
     * won't stop it from being garbage collected when it is
     * no longer in use elsewhere.
     *
     * @param instance the view instance to add.
     * @see java.lang.ref.WeakReference
     */
    public synchronized void addViewInstance(Object instance){
        WeakReference<?> weakRef = new WeakReference<>(instance);
        viewInstances.putValue(instance.getClass(), weakRef);
    }

    /**
     * Remove an instance of a view class.
     *
     * @param instance the view instance to remove.
     */
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

    /**
     * Remove all instances of the specified view class.
     *
     * @param clazz the view class to remove instances of.
     */
    public synchronized void removeAllViewInstancesForClass(Class<?> clazz){
        viewInstances.remove(clazz);
    }

    /**
     * Get all instances for the specified view class.
     *
     * @param clazz the view class to get instances of.
     * @return a collection of WeakReferences to all instances of the view class.
     */
    public synchronized Collection<WeakReference<?>> getViewInstancesForClass(Class<?> clazz){
        return Collections.unmodifiableCollection(viewInstances.get(clazz));
    }

    /*
     * View Adder Section
     */

    /**
     * Add a new view property adder method.
     *
     * @param propName the name of the property.
     * @param cam the ClassAndMethod.
     */
    public synchronized void addViewPropAdder(String propName, ClassAndMethod cam){
        viewPropAdders.putValue(propName, cam);
    }

    /**
     * Remove a view property adder method.
     *
     * @param cam the ClassAndMethod.
     */
    public synchronized void removeViewPropAdder(ClassAndMethod cam){
        viewPropAdders.removeValue(cam);
    }

    /**
     * Remove all view adder methods for the specified property.
     *
     * @param propName the name of the property.
     */
    public synchronized void removeAllAddersForViewProp(String propName){
        viewPropAdders.remove(propName);
    }

    /**
     * Get all view adder methods for the specified property.
     *
     * @param propName the name of the property.
     * @return all view adder methods for the property.
     */
    public synchronized Collection<ClassAndMethod> getAddersForViewProp(String propName){
        return Collections.unmodifiableCollection(viewPropAdders.get(propName));
    }

    /**
     * Get all view property adder methods.
     *
     * @return all view property adder methods.
     */
    public synchronized Collection<ClassAndMethod> getAllViewPropAdders(){
        return Collections.unmodifiableCollection(viewPropAdders.allValues());
    }

    /**
     * Get a count of all view property adder methods.
     *
     * @return a count of all view property adder methods.
     */
    public synchronized int getViewPropAdderCount(){
        return viewPropAdders.fullSize();
    }

    /*
     * View Remover Section
     */

    /**
     * Add a new view property remover method.
     *
     * @param propName the name of the property.
     * @param cam the ClassAndMethod.
     */
    public synchronized void addViewPropRemover(String propName, ClassAndMethod cam){
        viewPropRemovers.putValue(propName, cam);
    }

    /**
     * Remove a view property remover method.
     *
     * @param cam the ClassAndMethod.
     */
    public synchronized void removeViewPropRemover(ClassAndMethod cam){
        viewPropRemovers.removeValue(cam);
    }

    /**
     * Remove all view remover methods for the specified property.
     *
     * @param propName the property name.
     */
    public synchronized void removeAllRemoversForViewProp(String propName){
        viewPropRemovers.remove(propName);
    }

    /**
     * Get all view remover methods for the specified property.
     *
     * @param propName the name of the property.
     * @return all view remover methods for the property.
     */
    public synchronized Collection<ClassAndMethod> getRemoversForViewProp(String propName){
//        Collection<ClassAndMethod> result = null;
//        synchronized (this){
//            Collection<ClassAndMethod> cams = viewPropRemovers.get(propName);
//            if(cams != null){
//                result = new ArrayList<>(cams);
//            }
//        }

        return Collections.unmodifiableCollection(viewPropRemovers.get(propName));
    }

    /**
     * Get all view property remover methods.
     *
     * @return all view property remover methods.
     */
    public synchronized Collection<ClassAndMethod> getAllViewPropRemovers(){
//        Collection<ClassAndMethod> result = new ArrayList<>();
//        synchronized (this){
//            Collection<Collection<ClassAndMethod>> values = viewPropRemovers.values();
//            for(Collection<ClassAndMethod> value : values){
//                result.addAll(value);
//            }
//        }

        return Collections.unmodifiableCollection(viewPropRemovers.allValues());
    }

    /**
     * Get a count of all view property remover methods.
     *
     * @return a count of all view property remover methods.
     */
    public synchronized int getViewPropRemoverCount(){
        return viewPropRemovers.fullSize();
    }

    /*
     * Controller Section
     */

    /**
     * Add a new controller class type. Its name and whether or not it is
     * a singleton must also be provided.
     *
     * @param name the name of the controller.
     * @param clazz the class type of the controller.
     * @param singleton if the controller is a singleton.
     */
    public synchronized void addControllerType(String name, Class<?> clazz, boolean singleton){
        controllerTypes.put(name, clazz);
        controllerSingletons.put(name, singleton);
    }

    /**
     * Remove the named controller type. All references to the controller
     * with this name will be removed, including the class type and any
     * currently registered singleton instances.
     *
     * @param name the name of the controller to remove.
     */
    public synchronized void removeControllerType(String name){
        controllerTypes.remove(name);
        controllerSingletons.remove(name);
        controllerSingletonInstances.remove(name);
    }

    /**
     * Get the controller class type with the specified name.
     *
     * @param name the name of the controller.
     * @return the controller class type.
     */
    public synchronized Class<?> getControllerType(String name){
        return controllerTypes.get(name);
    }

    /**
     * Get if the controller with the specified name is a singleton.
     *
     * @param name the name of the controller.
     * @return true if the controller is a singleton.
     */
    public synchronized boolean isControllerSingleton(String name){
        return controllerSingletons.get(name);
    }

    /**
     * Get all controller class types.
     *
     * @return all controller class types.
     */
    public synchronized Collection<Class<?>> getAllControllerTypes(){
//        Collection<Class<?>> result = new ArrayList<>();
//        synchronized (this){
//            result.addAll(controllerTypes.values());
//        }

        return Collections.unmodifiableCollection(controllerTypes.values());
    }

    /**
     * Get a count of all controller class types.
     *
     * @return a count of all controller class types.
     */
    public synchronized int getControllerCount(){
        return controllerTypes.size();
    }

    /*
     * Controller Singleton Instance methods
     */

    /**
     * Add a new controller singleton instance.
     *
     * @param name the name of the controller.
     * @param controller the instance of the controller.
     */
    public synchronized void addControllerSingletonInstance(String name, Object controller){
        controllerSingletonInstances.put(name, controller);
    }

    /**
     * Remove a controller singleton instance with the provided name.
     *
     * @param name the name of the controller.
     */
    public synchronized void remoteControllerSingletonIntance(String name){
        controllerSingletonInstances.remove(name);
    }

    /**
     * Get the controller singleton instance with the specified
     * name, or null if no controller instance with that name
     * exists.
     *
     * @param name the name of the controller.
     * @return the singleton instance of the controller.
     */
    public synchronized Object getControllerSingletonInstance(String name){
        //The Object retrieved may not be thread-safe, but a copy should NOT be returned because the whole point of this is a shared reference
        return controllerSingletonInstances.get(name);
    }

    /*
     * Controller Callback Methods
     */

    //TODO since these are views that are the callbacks, they should be WeakRefs!!!!

    /**
     * Add a new controller callback object reference, tied to the instance
     * of the controller it is associated with.
     *
     * @param controller the controller.
     * @param callback the callback object, generally a view.
     */
    public synchronized void addControllerCallback(Object controller, Object callback){
        controllerCallbacks.put(controller, callback);
    }

    /**
     * Get the controller callback object reference for the provided controller
     * instance.
     *
     * @param controller the controller.
     * @return the callback object, generally a view.
     */
    public synchronized Object getControllerCallback(Object controller){
        //The Object retrieved may not be thread-safe, but a copy should NOT be returned because the whole point of this is a shared reference
        return controllerCallbacks.get(controller);
    }

    /**
     * Remove the controller callback object reference for the provided controller instance.
     *
     * @param controller the controller.
     */
    public synchronized void removeControllerCallback(Object controller){
        controllerCallbacks.remove(controller);
    }

}
