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

import io.craigmiller160.locus.reflect.ClassAndMethod;
import io.craigmiller160.locus.reflect.LocusInvoke;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.util.LocusStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * Created by craig on 3/12/16.
 */
public class LocusView {

    private static final Logger logger = LoggerFactory.getLogger(LocusView.class);

    private final LocusStorage storage;

    LocusView(){
        this.storage = LocusStorage.getInstance();
    }

    LocusView(LocusStorage storage){
        this.storage = storage;
    }

    public void registerView(Object view){
        //TODO this is how the view instances are locked in
    }

    public void setInt(String propName, int value) throws LocusException{
        //TODO
    }

    public void setFloat(String propName, float value) throws LocusException{
        //TODO
    }

    public void setDouble(String propName, double value) throws LocusException{
        //TODO
    }

    public void setShort(String propName, short value) throws LocusException{
        //TODO
    }

    public void setByte(String propName, byte value) throws LocusException{
        //TODO
    }

    public void setLong(String propName, long value) throws LocusException{
        //TODO
    }

    public void setBoolean(String propName, boolean value) throws LocusException{
        //TODO
    }

    public void setCharacter(String propName, char value) throws LocusException{
        //TODO
    }

    public void setString(String propName, String value) throws LocusException{
        //TODO
    }

    public void setObject(String propName, Object value) throws LocusException{
        Collection<ClassAndMethod> setters = storage.getSettersForViewProp(propName);
        if(setters == null || setters.size() <= 0){
            throw new LocusReflectiveException("No setters available in registered views to invoke for property. Property Name: " + propName);
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
                            LocusInvoke.invokeMethod(oam, value);
                            success = true;
                        }
                        catch(LocusReflectiveException ex){
                            logger.trace("Failed to invoke view setter method. Method: {} | Param: {}", oam.getMethod(), value.toString());
                            logger.trace("Exception thrown during failed invocation.", ex);
                        }
                    }
                }
            }
        }

        if(!success){
            throw new LocusReflectiveException("Unable to successfully invoke any view setter for property. Check TRACE level logs for details");
        }
    }

    public <T> void setValue(String propName, T value) throws LocusException{
        //TODO
    }

    public int getInt(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public float getFloat(String propName) throws LocusException{
        //TODO
        return 0f;
    }

    public double getDouble(String propName) throws LocusException{
        //TODO
        return 0.0;
    }

    public short getShort(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public byte getByte(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public long getLong(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public boolean getBoolean(String propName) throws LocusException{
        //TODO
        return false;
    }

    public String getString(String propName) throws LocusException{
        //TODO
        return "";
    }

    public char getCharacter(String propName) throws LocusException{
        //TODO
        return 'a';
    }

    public Object getObject(String propName) throws LocusException{
        ClassAndMethod cam = getMethod(propName, Locus.GETTER);
        Collection<WeakReference<?>> instances = storage.getViewInstancesForClass(cam.getSource());

        ObjectAndMethod oam = null;
        if(instances != null){
            if(instances.size() != 1){
                throw new LocusReflectiveException("Cannot have more than 1 instance of a view class to invoke a getter on, found " + instances.size() +
                        "Class: " + cam.getSourceType() + " | Method: " + cam.getMethod().getName()

                );
            }
            else{
                oam = new ObjectAndMethod(instances.iterator().next(), cam.getMethod());
            }
        }

        return LocusInvoke.invokeMethod(oam);
    }

    public <T> T getValue(String propName, Class<T> valueType) throws LocusException{
//        Object result = getObject(propName);
//        if(!(valueType.isAssignableFrom(result.getClass()))){
//            throw new LocusInvalidTypeException("Return value for getting \"" + propName +
//                    "\" doesn't match expected type. Expected: " + valueType.getName() +
//                    " Actual: " + result.getClass().getName());
//        }
//
//        return (T) result;
        return null;
    }

    /**
     * Get the appropriate method and its corresponding
     * object instance from the storage, to be reflectively
     * invoked by the caller.
     *
     * @param propName the name of the property to get the method for.
     * @param methodType the type of method (setter, getter, etc) to
     *                   retrieve.
     * @return the ObjectAndMethod of the specified type for the property.
     * @throws LocusReflectiveException if unable to find a method matching
     *                   the specifications.
     */
    private ClassAndMethod getMethod(String propName, int methodType) throws LocusReflectiveException{
        ClassAndMethod cam = null;
        String typeName = "";
        if(methodType == Locus.GETTER){
            //TODO cam = storage.getViewPropGetter(propName);
            typeName = "getter";
        }
        else{
            //TODO oam = storage.getModelPropSetter(propName);
            typeName = "setter";
        }

        if(cam == null){
            throw new LocusReflectiveException("No view " + typeName + " found matching the property name \"" + propName + "\"");
        }
        return cam;
    }

}
