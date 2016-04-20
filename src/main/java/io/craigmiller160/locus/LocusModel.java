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

import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ReflectiveException;
import io.craigmiller160.utils.reflect.RemoteInvoke;

import static io.craigmiller160.locus.util.LocusConstants.*;

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
class LocusModel {

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
     * Set an Object value in one of the model classes.
     * After being called, this method will update any
     * corresponding views that have been configured
     * to accept this behavior.
     *
     * @param propName the name of the property to set.
     * @param value the value(s) to set to the property.
     * @throws LocusException if an error occurs.
     */
    public void setValue(String propName, Object... value) throws LocusException{
        RemoteInvoke.invokeMethod(getMethod(propName, SETTER), value);
        locusView.setValue(propName, value);
    }

    /**
     * Get an Object value from one of the model classes.
     *
     * @param propName the name of the property to get.
     * @param args any arguments to be passed to the method.
     * @throws LocusException if an error occurs.
     */
    public Object getValue(String propName, Object... args) throws LocusException{
        return RemoteInvoke.invokeMethod(getMethod(propName, GETTER), args);
    }

    /**
     * Get a value from one of the model classes. This method defines
     * the type of value that should be returned.
     *
     * @param propName the name of the property to get.
     * @param valueType the class type that the value should be returned as.
     * @param args any arguments to be passed to the method.
     * @throws LocusException if an error occurs.
     */
    public <T> T getValue(String propName, Class<T> valueType, Object...args) throws LocusException{
        Object result = getValue(propName, args);
        if(!(valueType.isAssignableFrom(result.getClass()))){
            throw new LocusInvalidTypeException(
                    String.format("Return value for getting \"%1$s\" doesn't match expected type. Expected: %2$s | Actual: %3$s",
                            propName, valueType.getName(), result.getClass().getName()));

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
     * @throws ReflectiveException if unable to find a method matching
     *                   the specifications.
     */
    private ObjectAndMethod getMethod(String propName, int methodType) throws ReflectiveException{
        ObjectAndMethod oam = null;
        String typeName = "";
        if(methodType == GETTER){
            oam = storage.getModelPropGetter(propName);
            typeName = "getter";
        }
        else{
            oam = storage.getModelPropSetter(propName);
            typeName = "setter";
        }

        if(oam == null){
            throw new ReflectiveException("No model " + typeName + " found matching the property name \"" + propName + "\"");
        }
        return oam;
    }
}
