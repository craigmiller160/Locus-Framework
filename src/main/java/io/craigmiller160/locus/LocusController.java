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

import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectCreator;
import io.craigmiller160.utils.reflect.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;

/**
 * <p>One of the core components of the Locus Framework.
 * This class is the component that abstractly links
 * the controllers to the views.</p>
 *
 * <p><b>THREAD SAFETY: This class is completely thread-safe.
 * Its only mutable state is the LocusStorage field, and that is properly
 * synchronized.</b></p>
 *
 * @author craigmiller
 * @version 1.4.2
 */
@ThreadSafe
public class LocusController {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusController.class);

    /**
     * The LocusStorage instance containing the classes managed
     * by this framework.
     */
    private final LocusStorage storage;

    /**
     * The UIThreadExecutorFactory, used primarily for testing.
     */
    private final UIThreadExecutorFactory factory;

    /**
     * The default constructor for this class.
     */
    LocusController(){
        this.storage = LocusStorage.getInstance();
        this.factory = null;
    }

    /**
     * A special constructor provided exclusively for
     * testing. It allows the LocusStorage to be set
     * externally a more controlled testing environment.
     * It also accepts the UIThreadExecutor, for use in
     * testing.
     *
     * @param storage the LocusStorage, passed this way
     *                primarily for more controlled testing.
     * @param factory the UIThreadExecutorFactory for testing.
     */
    LocusController(LocusStorage storage, UIThreadExecutorFactory factory){
        this.storage = storage;
        this.factory = factory;
    }

    /**
     * Get the LocusControllerCallback wrapper to perform
     * operations on the callback object.
     *
     * @param controller the controller to get the callback for.
     * @return the LocusControllerCallback.
     * @throws LocusException if there's no callback for that controller, or if another error occurs.
     */
    public LocusControllerCallback callback(Object controller) throws LocusException{
        Object callback = storage.getControllerCallback(controller);
        if(callback == null){
            throw new LocusException(String.format("No callback Object assigned to controller"));
        }

        UIThreadExecutor uiThreadExecutor = factory != null ? factory.getUIThreadExecutor() : UIThreadExecutorFactory.newInstance().getUIThreadExecutor();

        return new LocusControllerCallback(callback, uiThreadExecutor);
    }

    /**
     * Get the controller matching the provided name.
     * Instantiation parameters can be provided to instantiate a controller
     * that has a constructor requiring arguments.
     *
     *
     * @param controllerName the name of the controller.
     * @param instantiationParams the parameters to use to instantiate the controller.
     * @return the controller.
     * @throws LocusException if no controller exists with that name or type, unable to instantiate, or another error occurs.
     */
    public Object getController(String controllerName, Object...instantiationParams) throws LocusException{
        Object controller = null;

        Class<?> controllerType = storage.getControllerType(controllerName);
        if(controllerType == null){
            throw new LocusNoControllerException(String.format("No controller exists with the name \"%s\"", controllerName));
        }

        logger.trace("Retrieving controller. Name: {} | Params: {}", controllerName, Arrays.toString(instantiationParams));

        //If no params are provided, the no arg constructor will be used here
        controller = ObjectCreator.instantiateClassWithParams(controllerType, instantiationParams);

        if(controller == null){
            throw new LocusNoControllerException(String.format("Unable to get instance of controller named: %s", controllerName));
        }

        return controller;
    }

    /**
     * Get the controller matching the provided name, and ensure that the
     * return value is of the specified class type, so no casting is needed.
     * Instantiation parameters can be provided to instantiate a controller
     * that has a constructor requiring arguments.
     *
     * @param controllerName the name of the controller.
     * @param controllerType the class type the controller should be returned as.
     * @param instantiationParams the parameters to use to instantiate the controller.
     * @param <T> the type of class of the controller.
     * @return the controller.
     * @throws LocusException if no controller exists with that name or type, unable to instantiate, or another error occurs.
     */
    public <T> T getController(String controllerName, Class<T> controllerType, Object...instantiationParams) throws LocusException{
        Object controller = getController(controllerName, instantiationParams);
        if(controller == null){
            return null;
        }

        if(!controllerType.isAssignableFrom(controller.getClass()) && !ParamUtils.isAcceptablePrimitive(controllerType, controller.getClass())){
            throw new LocusInvalidTypeException(
                    String.format("The type of the controller names\"%1$s\" doesn't match the expected type. Expected: %2$s | Actual: %3$s",
                            controllerName, controllerType.getName(), controller.getClass().getName()));
        }
        return (T) controller;
    }

    /**
     * Get the controller matching the provided name.
     * Instantiation parameters can be provided to instantiate a controller
     * that has a constructor requiring arguments.
     *
     * A special callback object is included in this call. The callback
     * will be stored for later access, and will be linked to the
     * controller returned by this method.
     *
     *
     * @param controllerName the name of the controller.
     * @param callback the callback object.
     * @param instantiationParams the parameters to use to instantiate the controller.
     * @return the controller.
     * @throws LocusException if no controller exists with that name or type, unable to instantiate, or another error occurs.
     */
    public Object getControllerWithCallback(Object callback, String controllerName, Object...instantiationParams) throws LocusException{
        Object controller = getController(controllerName, instantiationParams);
        if(controller != null){
            storage.addControllerCallback(controller, callback);
        }
        return controller;
    }

    /**
     * Get the controller matching the provided name, and ensure that the
     * return value is of the specified class type, so no casting is needed.
     * Instantiation parameters can be provided to instantiate a controller
     * that has a constructor requiring arguments.
     *
     * A special callback object is included in this call. The callback
     * will be stored for later access, and will be linked to the
     * controller returned by this method.
     *
     *
     * @param controllerName the name of the controller.
     * @param controllerType the class type the controller should be returned as.
     * @param callback the callback object.
     * @param instantiationParams the parameters to use to instantiate the controller.
     * @param <T> the type of class of the controller.
     * @return the controller.
     * @throws LocusException if no controller exists with that name or type, unable to instantiate, or another error occurs.
     */
    public <T> T getControllerWithCallback(Object callback, String controllerName, Class<T> controllerType, Object...instantiationParams) throws LocusException{
        T controller = getController(controllerName, controllerType, instantiationParams);
        if(controller != null){
            storage.addControllerCallback(controller, callback);
        }
        return controller;
    }

}
