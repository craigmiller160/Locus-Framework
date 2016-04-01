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

import io.craigmiller160.locus.reflect.LocusInvoke;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.util.LocusStorage;

/**
 * One of the core components of the Locus Framework.
 * This class handles manipulating data in the models
 * registered with it. Using reflection, it sets & retrieves
 * property values in those model classes.
 *
 * A wide range of convenience methods are provided here for
 * working with the full range of Java data types.
 *
 * Created by craig on 3/12/16.
 */
public class LocusModel {

    /**
     * The LocusStorage instance containing the classes managed
     * by this framework.
     */
    private final LocusStorage storage;

    /**
     * The LocusView instance, used for updating views
     * after changes to the models.
     */
    private final LocusView locusView;

    /**
     * The default constructor for this class.
     */
    LocusModel(){
        this.storage = LocusStorage.getInstance();
        this.locusView = new LocusView();
    }

    /**
     * A special constructor provided exclusively for
     * testing. It allows the LocusStorage and LocusView to be set
     * externally a more controlled testing environment.
     *
     * @param storage the LocusStorage, passed this way
     *                primarily for more controlled testing.
     * @param locusView the LocusView, passed this way
     *                  primarily for more controlled testing.
     */
    LocusModel(LocusStorage storage, LocusView locusView){
        this.storage = storage;
        this.locusView = locusView;
    }

    /**
     * Set an Integer value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setInt(String propName, int value) throws LocusException{
        setObject(propName, value);
        locusView.setInt(propName, value);
    }

    /**
     * Set a Float value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setFloat(String propName, float value) throws LocusException{
        setObject(propName, value);
        locusView.setFloat(propName, value);
    }

    /**
     * Set a Double value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setDouble(String propName, double value) throws LocusException{
        setObject(propName, value);
        locusView.setDouble(propName, value);
    }

    /**
     * Set a Short value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setShort(String propName, short value) throws LocusException{
        setObject(propName, value);
        locusView.setShort(propName, value);
    }

    /**
     * Set a Byte value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setByte(String propName, byte value) throws LocusException{
        setObject(propName, value);
        locusView.setByte(propName, value);
    }

    /**
     * Set a Long value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setLong(String propName, long value) throws LocusException{
        setObject(propName, value);
        locusView.setLong(propName, value);
    }

    /**
     * Set a Boolean value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setBoolean(String propName, boolean value) throws LocusException{
        setObject(propName, value);
        locusView.setBoolean(propName, value);
    }

    /**
     * Set a String value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setString(String propName, String value) throws LocusException{
        setObject(propName, value);
        locusView.setString(propName, value);
    }

    /**
     * Set a Character value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setCharacter(String propName, char value) throws LocusException{
        setObject(propName, value);
        locusView.setCharacter(propName, value);
    }

    /**
     * Set an Object value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setObject(String propName, Object value) throws LocusException{
        LocusInvoke.invokeMethod(getMethod(propName, Locus.SETTER), value);
        locusView.setObject(propName, value);
    }

    /**
     * Get an Integer value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public int getInt(String propName) throws LocusException{
        return getValue(propName, Integer.class);
    }

    /**
     * Get a Float value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public float getFloat(String propName) throws LocusException{
        return getValue(propName, Float.class);
    }

    /**
     * Get a Double value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public double getDouble(String propName) throws LocusException{
        return getValue(propName, Double.class);
    }

    /**
     * Get a Short value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public short getShort(String propName) throws LocusException{
        return getValue(propName, Short.class);
    }

    /**
     * Get a Byte value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public byte getByte(String propName) throws LocusException{
        return getValue(propName, Byte.class);
    }

    /**
     * Get a Long value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public long getLong(String propName) throws LocusException{
        return getValue(propName, Long.class);
    }

    /**
     * Get a Boolean value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public boolean getBoolean(String propName) throws LocusException{
        return getValue(propName, Boolean.class);
    }

    /**
     * Get a String value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public String getString(String propName) throws LocusException{
        return getValue(propName, String.class);
    }

    /**
     * Get a Character value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public char getCharacter(String propName) throws LocusException{
        return getValue(propName, Character.class);
    }

    /**
     * Get an Object value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public Object getObject(String propName) throws LocusException{
        return LocusInvoke.invokeMethod(getMethod(propName, Locus.GETTER));
    }

    /**
     * Get a value from one of the model classes. This method defines
     * the type of value that should be returned.
     *
     * @param propName the name of the property to get.
     * @throws LocusException if an error occurs.
     */
    public <T> T getValue(String propName, Class<T> valueType) throws LocusException{
        Object result = getObject(propName);
        if(!(valueType.isAssignableFrom(result.getClass()))){
            throw new LocusInvalidTypeException("Return value for getting \"" + propName +
                    "\" doesn't match expected type. Expected: " + valueType.getName() +
                    " Actual: " + result.getClass().getName());
        }
        return (T) result;
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
    private ObjectAndMethod getMethod(String propName, int methodType) throws LocusReflectiveException{
        ObjectAndMethod oam = null;
        String typeName = "";
        if(methodType == Locus.GETTER){
            oam = storage.getModelPropGetter(propName);
            typeName = "getter";
        }
        else{
            oam = storage.getModelPropSetter(propName);
            typeName = "setter";
        }

        if(oam == null){
            throw new LocusReflectiveException("No model " + typeName + " found matching the property name \"" + propName + "\"");
        }
        return oam;
    }
}
