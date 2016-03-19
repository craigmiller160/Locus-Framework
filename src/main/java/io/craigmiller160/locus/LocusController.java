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

/**
 * Created by craig on 3/12/16.
 */
public class LocusController {

    private static final LocusStorage storage = LocusStorage.getInstance();

    LocusController(){}

    public Object getController(String controllerName) throws LocusException{
        //TODO Object controller = storage.getController(controllerName);
//        if(controller == null){
//            throw new LocusNoControllerException("No controller exists with the name \"" + controllerName + "\"");
//        }
        return null;
    }

    public <T> T getControllerWithType(String controllerName, Class<T> controllerType) throws LocusException{
        Object controller = getController(controllerName);
        if(!controllerType.isAssignableFrom(controller.getClass())){
            throw new LocusInvalidTypeException("The type of the controller named \"" + controllerName +
                    "\" doesn't match the expected type. Expected: " + controllerType.getName() +
                    " Actual: " + controller.getClass().getName());
        }
        return (T) controller;
    }

}
