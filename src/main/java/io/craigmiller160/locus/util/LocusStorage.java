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

    private MultiValueMap<String,ObjectAndMethod<?>> modelPropSetters;
    private MultiValueMap<String,ObjectAndMethod<?>> modelPropGetters;

    private MultiValueMap<String,ClassAndMethod> viewPropSetters;
    private MultiValueMap<String,ClassAndMethod> viewPropGetters;
    private MultiValueMap<Class<?>, WeakReference<?>> viewInstances;

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

    private LocusStorage(){
        modelPropSetters = new MultiValueMap<>();
        modelPropGetters = new MultiValueMap<>();

        viewPropSetters = new MultiValueMap<>();
        viewPropGetters = new MultiValueMap<>();
        viewInstances = new ViewObjectTracker<>();

        controllerSingletons = new HashMap<>();
        controllerTypes = new HashMap<>();
    }

    /*
     * Model Setter Section
     */

    public void addModelPropSetter(String propName, ObjectAndMethod<?> oam){
        modelPropSetters.putValue(propName, oam);
    }

    public void removeModelPropSetter(ObjectAndMethod<?> oam){
        modelPropSetters.removeValue(oam);
    }

    public void removeAllSettersForModelProp(String propName){
        modelPropSetters.remove(propName);
    }

    public Collection<ObjectAndMethod<?>> getSettersForModelProp(String propName){
        return modelPropSetters.get(propName);
    }

    /*
     * Model Getter Section
     */

    public void addModelPropGetter(String propName, ObjectAndMethod<?> oam){
        modelPropGetters.putValue(propName, oam);
    }

    public void removeModelPropGetter(ObjectAndMethod<?> oam){
        modelPropGetters.removeValue(oam);
    }

    public void removeAllGettersForModelProp(String propName){
        modelPropGetters.remove(propName);
    }

    public Collection<ObjectAndMethod<?>> getGettersForModelProp(String propName){
        return modelPropGetters.get(propName);
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

}
