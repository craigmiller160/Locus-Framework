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
import io.craigmiller160.utils.reflect.FindAndInvoke;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ParamUtils;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

import static io.craigmiller160.locus.util.LocusConstants.ADDER;
import static io.craigmiller160.locus.util.LocusConstants.GETTER;
import static io.craigmiller160.locus.util.LocusConstants.REMOVER;
import static io.craigmiller160.locus.util.LocusConstants.SETTER;

/**
 * <p>One of the core components of the Locus Framework. It uses reflection to set and retrieve values to and from model properties.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class is mostly thread-safe.
 * Its only mutable static is the LocusStorage field, and that class
 * is safely synchronized. However, interactions with the model
 * classes that will be reflectively called by this class have
 * no guarantee of thread safety. Those classes will need to use
 * proper synchronization to be safe to access in a multi-threaded
 * environment.</p>
 *
 * @author craigmiller
 * @version 1.4.2
 */
@ThreadSafe
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
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusModel.class);

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
     * Set a property in one of the model classes.
     * After being called, this method will update any
     * corresponding views with the same property.
     *
     * @param propName the name of the property to set.
     * @param values the value(s) to set to the property.
     * @throws ReflectiveException if an error occurs.
     */
    public void setValue(String propName, Object... values) throws ReflectiveException{
        FindAndInvoke.findInvokeOneMethod(getMethods(propName, SETTER), values);
        locusView.setValue(propName, values);
    }

    /**
     * Add a value to a collection property in
     * one of the model classes. After being called,
     * this method will updated any corresponding
     * views with the same property.
     *
     * @param propName the name of the model property.
     * @param values the value(s) to add.
     * @throws ReflectiveException if an error occurs.
     */
    public void addValue(String propName, Object...values) throws ReflectiveException{
        FindAndInvoke.findInvokeOneMethod(getMethods(propName, ADDER), values);
        locusView.addValue(propName, values);
    }

    /**
     * Remove a value from a collection property in
     * one of the model classes. After being called,
     * this method will updated any corresponding
     * views with the same property.
     *
     * @param propName the name of the model property.
     * @param values the value(s) to remove.
     * @throws ReflectiveException if an error occurs.
     */
    public void removeValue(String propName, Object...values) throws ReflectiveException{
        FindAndInvoke.findInvokeOneMethod(getMethods(propName, REMOVER), values);
        locusView.removeValue(propName, values);
    }

    /**
     * Get the value from a property in one of the model classes.
     *
     * @param propName the name of the property to get.
     * @param args any arguments to be passed to the method.
     * @return the value retrieved from the model class.
     * @throws LocusException if an error occurs.
     */
    public Object getValue(String propName, Object... args) throws LocusException{
        return FindAndInvoke.findInvokeOneMethod(getMethods(propName, GETTER), args);
    }

    /**
     * Get a value from one of the model classes. This method defines
     * the type of value that should be returned.
     *
     * @param propName the name of the property to get.
     * @param valueType the class type that the value should be returned as.
     * @param args any arguments to be passed to the method.
     * @param <T> the type of the value to return.
     * @return the value retrieved from the model class.
     * @throws LocusException if an error occurs.
     */
    public <T> T getValue(String propName, Class<T> valueType, Object...args) throws LocusException{
        Object result = getValue(propName, args);
        if(result == null){
            return null;
        }

        if(!valueType.isAssignableFrom(result.getClass()) && !ParamUtils.isAcceptablePrimitive(valueType, result.getClass())){
            throw new LocusInvalidTypeException(
                    String.format("Return value for getting \"%1$s\" doesn't match expected type. Expected: %2$s | Actual: %3$s",
                            propName, valueType.getName(), result.getClass().getName()));

        }
        return (T) result;
    }

    /**
     * Get the appropriate methods and their corresponding
     * object instances from the storage, to be reflectively
     * invoked by the caller.
     *
     * @param propName the name of the property to get the method for.
     * @param methodType the type of method (setter, getter, etc) to
     *                   retrieve.
     * @return the ObjectAndMethod of the specified type for the property.
     * @throws ReflectiveException if unable to find a method matching
     *                   the specifications.
     */
    private Collection<ObjectAndMethod> getMethods(String propName, int methodType) throws ReflectiveException{
        Collection<ObjectAndMethod> oams = null;
        String methodName = "";
        switch(methodType){
            case GETTER:
                oams = storage.getGettersForModelProp(propName);
                methodName = "getter";
                break;
            case SETTER:
                oams = storage.getSettersForModelProp(propName);
                methodName = "setter";
                break;
            case ADDER:
                oams = storage.getAddersForModelProp(propName);
                methodName = "adder";
                break;
            case REMOVER:
                oams = storage.getRemoversForModelProp(propName);
                methodName = "remover";
                break;
        }

        logger.trace(String.format("Found %1$d %2$s methods matching property name %3$s", oams != null ? oams.size() : 0, methodName, propName));

        if(oams == null || oams.size() == 0){
            throw new ReflectiveException("No model " + methodName + " found matching the property name \"" + propName + "\"");
        }

        return oams;
    }
}
