package io.craigmiller160.locus.reflect;

/**
 * A simple utility class to instantiate
 * objects, wrapping any exceptions that occur
 * in a LocusReflectiveException.
 *
 * Created by craigmiller on 3/19/16.
 */
public class ObjectCreator {

    public static <T>  T instantiateClass(Class<T> type) throws LocusReflectiveException{
        T result = null;
        try{
            result = type.newInstance();
        }
        catch(InstantiationException | IllegalAccessException ex){
            throw new LocusReflectiveException("Unable to instantiate class: " + type.getName(), ex);
        }
        return result;
    }

}
