package io.craigmiller160.locus;

import io.craigmiller160.locus.util.LocusStorage;

/**
 * Created by craig on 3/12/16.
 */
public class LocusController {

    private static final LocusStorage storage = LocusStorage.getInstance();

    LocusController(){}

    public Object getController(String controllerName) throws LocusException{
        Object controller = storage.getController(controllerName);
        if(controller == null){
            throw new LocusNoControllerException("No controller exists with the name \"" + controllerName + "\"");
        }
        return controller;
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
