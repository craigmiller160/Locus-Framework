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

import io.craigmiller160.locus.reflect.ObjectCreator;
import io.craigmiller160.locus.util.LocusStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One of the core components of the Locus Framework.
 * This class is the component that abstractly links
 * the controllers to the views.
 *
 * Created by craig on 3/12/16.
 */
public class LocusController {

    private static final Logger logger = LoggerFactory.getLogger(LocusController.class);

    /**
     * The LocusStorage instance containing the classes managed
     * by this framework.
     */
    private final LocusStorage storage;

    /**
     * The default constructor for this class.
     */
    LocusController(){
        this.storage = LocusStorage.getInstance();
    }

    /**
     * A special constructor provided exclusively for
     * testing. It allows the LocusStorage to be set
     * externally a more controlled testing environment.
     *
     * @param storage the LocusStorage, passed this way
     *                primarily for more controlled testing.
     */
    LocusController(LocusStorage storage){
        this.storage = storage;
    }

    public LocusControllerCallback callback(Object controller) throws LocusException{
        Object callback = storage.getControllerCallback(controller);
        if(callback == null){
            throw new LocusException(String.format("No callback Object assigned to controller"));
        }

        return new LocusControllerCallback(callback);
    }

    public Object getController(String controllerName) throws LocusException{
        Object controller = null;

        Class<?> controllerType = storage.getControllerType(controllerName);
        if(controllerType == null){
            throw new LocusNoControllerException(String.format("No controller exists with the name \"%s\"", controllerName));
        }

        boolean singleton = storage.isControllerSingleton(controllerName);

        logger.trace("Retrieving controller. Name: {} | Singleton: {}", controllerName, singleton);

        if(singleton){
            controller = storage.getControllerSingletonInstance(controllerName);
            if(controller == null){
                controller = ObjectCreator.instantiateClass(controllerType);
                storage.addControllerSingletonInstance(controllerName, controller);
            }
        }
        else{
            controller = ObjectCreator.instantiateClass(controllerType);
        }

        if(controller == null){
            throw new LocusNoControllerException(String.format("Unable to get instance of controller named: %s", controllerName));
        }

        return controller;
    }

    public <T> T getController(String controllerName, Class<T> controllerType) throws LocusException{
        Object controller = getController(controllerName);

        if(!controllerType.isAssignableFrom(controller.getClass())){
            throw new LocusInvalidTypeException(
                    String.format("The type of the controller names\"%1$s\" doesn't match the expected type. Expected: %2$s | Actual: %3$s",
                            controllerName, controllerType.getName(), controller.getClass().getName()));
        }
        return (T) controller;
    }

    public Object getController(String controllerName, Object callback) throws LocusException{
        Object controller = getController(controllerName);
        storage.addControllerCallback(controller, callback);
        return controller;
    }

    public <T> T getController(String controllerName, Class<T> controllerType, Object callback){
        T controller = getController(controllerName, controllerType);
        storage.addControllerCallback(controller, callback);
        return controller;
    }

}
