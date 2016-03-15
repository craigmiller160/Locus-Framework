package io.craigmiller160.locus.util;

import io.craigmiller160.locus.reflect.ObjectAndMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton storage class configured at application
 * startup. It's used to store references to all the
 * items that Locus reflectively manages.
 *
 * Created by craig on 3/12/16.
 */
public class LocusStorage {

    private static LocusStorage instance;

    private Map<String,ObjectAndMethod> modelPropSetters; //TODO there cannot be two setters with the same name
    private Map<String,ObjectAndMethod> modelPropGetters; //TODO there cannot be two getters with the same name
    private MultiValueMap<String,ObjectAndMethod> viewPropSetters;
    private Map<String,ObjectAndMethod> viewPropGetters; //TODO there cannot be two getters with the same name
    private Map<String,Object> controllers; //TODO there cannot be two controllers with the same name

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
        modelPropSetters = new HashMap<>();
        modelPropGetters = new HashMap<>();
        viewPropSetters = new MultiValueMap<>();
        viewPropGetters = new HashMap<>();
        controllers = new HashMap<>();
    }

    public void addModelPropSetter(String propName, ObjectAndMethod oam){
        modelPropSetters.put(propName, oam);
    }

    public void removeModelPropSetter(String propName){
        modelPropSetters.remove(propName);
    }

    public ObjectAndMethod getModelPropSetter(String propName){
        return modelPropSetters.get(propName);
    }

    public void addModelPropGetter(String propName, ObjectAndMethod oam){
        modelPropGetters.put(propName, oam);
    }

    public void removeModelPropGetter(String propName){
        modelPropGetters.remove(propName);
    }

    public ObjectAndMethod getModelPropGetter(String propName){
        return modelPropGetters.get(propName);
    }

    public void addViewPropSetter(String propName, ObjectAndMethod oam){
        viewPropSetters.putValue(propName, oam);
    }

    public void removeViewPropSetter(ObjectAndMethod oam){
        viewPropSetters.removeValue(oam);
    }

    public void removeAllSettersForViewProp(String propName){
        viewPropSetters.remove(propName);
    }

    public Collection<ObjectAndMethod> getViewPropSetters(String propName){
        return viewPropSetters.get(propName);
    }

    public void addViewPropGetter(String propName, ObjectAndMethod oam){
        viewPropGetters.put(propName, oam);
    }

    public void removeViewPropGetter(String propName){
        viewPropGetters.remove(propName);
    }

    public ObjectAndMethod getViewPropGetter(String propName){
        return viewPropGetters.get(propName);
    }

    public void addController(String key, Object controller){
        controllers.put(key, controller);
    }

    public void removeController(String key){
        controllers.remove(key);
    }

    public Object getController(String key){
        return controllers.get(key);
    }

}
