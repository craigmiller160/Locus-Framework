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
import io.craigmiller160.locus.reflect.LocusInvocationException;
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
        setObject(propName, value);
    }

    public void setFloat(String propName, float value) throws LocusException{
        setObject(propName, value);
    }

    public void setDouble(String propName, double value) throws LocusException{
        setObject(propName, value);
    }

    public void setShort(String propName, short value) throws LocusException{
        setObject(propName, value);
    }

    public void setByte(String propName, byte value) throws LocusException{
        setObject(propName, value);
    }

    public void setLong(String propName, long value) throws LocusException{
        setObject(propName, value);
    }

    public void setBoolean(String propName, boolean value) throws LocusException{
        setObject(propName, value);
    }

    public void setCharacter(String propName, char value) throws LocusException{
        setObject(propName, value);
    }

    public void setString(String propName, String value) throws LocusException{
        setObject(propName, value);
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
                        catch(LocusInvocationException ex){
                            //InvocationExceptions are when the method was successfully invoked, but during its operation an exception occurred
                            //This should NOT be swallowed, and should be propagated
                            throw ex;
                        }
                        catch(LocusReflectiveException ex){
                            logger.trace("Failed to invoke view setter method. Note that certain invocations are expected to fail.\n" +
                                    "   Method: {} | Param: {}", oam.getMethod(), value.toString(), ex);
                        }
                    }
                }
            }
        }

        if(!success){
            throw new LocusReflectiveException("Unable to successfully invoke any view setter for property. Check TRACE level logs for details");
        }
    }

}
