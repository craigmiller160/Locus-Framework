package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.locus.util.ScannerExclusions;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import io.craigmiller160.utils.reflect.ObjectCreator;
import io.craigmiller160.utils.reflect.ReflectiveException;
import io.craigmiller160.utils.reflect.ReflectiveMethodHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import static io.craigmiller160.locus.util.LocusConstants.MODEL_TYPE;

/**
 * A common class to encapsulate the logic
 * of scanning individual classes and adding
 * appropriate references to the LocusStorage.
 *
 * Created by craig on 4/20/16.
 */
class ScanParser {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ScanParser.class);

    /**
     * Instantiate the model class type and parse it for methods that
     * Locus can remotely invoke. A reference to the model and each
     * method will be bundled together and placed in the LocusStorage
     * for later reference.
     *
     * @param modelType the class type of the model.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     * @throws ReflectiveException if unable to parse the model class.
     */
    void parseModelClass(Class<?> modelType, LocusStorage storage, ScannerExclusions exclusions){
        Object model = ObjectCreator.instantiateClass(modelType);
        Method[] publicMethods = modelType.getMethods();
        for(Method m : publicMethods){
            if(m.getName().startsWith("set") && isClassAllowed(m.getDeclaringClass(), exclusions)){
                String propName = m.getName().substring(3);
                ObjectAndMethod oam = new ObjectAndMethod(model, m);
                validateUniqueMethod(propName, MODEL_TYPE, oam, storage.getAllModelPropSetters());
                logger.trace("Adding model property setter to storage. Property: {} | Setter: {}", propName, oam.toString());
                storage.addModelPropSetter(propName, oam);
            }
            else if((m.getName().startsWith("get") || m.getName().startsWith("is")) &&
                    isClassAllowed(m.getDeclaringClass(), exclusions)){
                //Set the propName differently for either a "get" or "is" prefix, based on their different lengths
                String propName = m.getName().startsWith("get") ? m.getName().substring(3) : m.getName().substring(2);
                ObjectAndMethod oam = new ObjectAndMethod(model, m);
                validateUniqueMethod(propName, MODEL_TYPE, oam, storage.getAllModelPropGetters());
                logger.trace("Adding model property getter to storage. Property: {} | Getter: {}", propName, oam.toString());
                storage.addModelPropGetter(propName, oam);
            }
        }
    }

    /**
     * Parse the controller class type, validate it and store it
     * properly in the LocusStorage.
     *
     * @param controllerType the class type of the controller.
     * @param storage the LocusStorage.
     * @param name the name of the controller.
     * @param singleton whether or not the controller is a singleton.
     * @throws ReflectiveException if unable to parse the controller class.
     */
    void parseControllerClass(Class<?> controllerType, LocusStorage storage, String name, boolean singleton){
        validateUniqueController(name, controllerType, storage);
        logger.trace("Adding controller type to storage. Name: {} | Class: {}", name, controllerType);
        storage.addControllerType(name, controllerType, singleton);
    }

    /**
     * Parse the view class type, identifying methods that Locus can
     * reflectively invoke, and wrapping a reference to the method
     * and to the enclosing class together before placing them
     * in the LocusStorage.
     *
     * @param viewType the class type of the view.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     * @throws ReflectiveException if unable to parse the view class.
     */
    void parseViewClass(Class<?> viewType, LocusStorage storage, ScannerExclusions exclusions){
        Method[] publicMethods = viewType.getMethods();
        for(Method m : publicMethods){
            if(m.getName().startsWith("set") && isClassAllowed(m.getDeclaringClass(), exclusions)){
                String propName = m.getName().substring(3);
                ClassAndMethod cam = new ClassAndMethod(viewType, m);
                logger.trace("Adding view property setter to storage. Property: {} | Setter: {}", propName, cam.toString());
                storage.addViewPropSetter(propName, cam);
            }
        }
    }

    /**
     * Use the ScannerExclusions to determine if the class type provided is
     * allowed. If the ScannerExclusions is null, the class is allowed
     * by default.
     *
     * @param clazz the class to determine if it is allowed.
     * @param scannerExclusions the ScannerExclusions.
     * @return true if the class is allowed.
     */
    private boolean isClassAllowed(Class<?> clazz, ScannerExclusions scannerExclusions){
        return scannerExclusions == null || scannerExclusions.isClassAllowed(clazz);
    }

    /**
     * Validate that the specified controller is unique, meaning there
     * are no other controllers with the same name in the storage.
     *
     * @param controllerName the name of the controller.
     * @param controllerType the type of the controller.
     * @param storage the LocusStorage.
     * @throws ReflectiveException if the controller is invalid.
     */
    private void validateUniqueController(String controllerName, Class<?> controllerType, LocusStorage storage) throws ReflectiveException{
        Set<String> controllerNames = storage.getAllControllerNames();

        //If no map provided, no validation can occur. This is probably due to no controllers existing yet
        if(controllerNames == null){
            return;
        }

        //Test the controller name to ensure it is unique
        for(String otherName : controllerNames){
            if(controllerName.equals(otherName)){
                throw new ReflectiveException(String.format(
                                "Identically named controllers are not allowed.%n" +
                                "  Name: %1$s | Type: %2$s%%n" +
                                "  Name: %3$s | Type: %4$s",
                                controllerName, controllerType.getName(),
                                otherName, storage.getControllerType(otherName)));
            }
        }
    }

    /**
     * Validate that the method is unique. This means that the property name
     * part of it (for example, everything after set... or get...) has no
     * match in any other model class.
     *
     * @param propName the property name of the method.
     * @param category the type of method (ie, getter vs setter).
     * @param rmh the ReflectiveMethodHolder containing the method to test.
     * @param otherOams the other ReflectiveMethodHolders to compare against.
     * @throws ReflectiveException if the method is invalid.
     */
    private void validateUniqueMethod(String propName, String category, ReflectiveMethodHolder<?> rmh,
                                      Collection<? extends ReflectiveMethodHolder<?>> otherOams) throws ReflectiveException {
        //If no collection provided, no validation can occur. This is probably due to no methods for that property existing
        if(otherOams == null){
            return;
        }

        Method m1 = rmh.getMethod();
        for(ReflectiveMethodHolder<?> rmh2 : otherOams){
            Method m2= rmh2.getMethod();
            if(m1.getName().equals(m2.getName())){
                throw new ReflectiveException(String.format(
                        "Identical methods for single property in single category not allowed.%n" +
                        "   Category: %1$s | Property: %2$s%n" +
                        "   Class: %3$s | Method: %4$s%n" +
                        "   Class: %5$s | Method: %6$s",
                        category, propName, rmh.getSourceType().getName(), m1.getName(),
                        rmh2.getSourceType().getName(), m2.getName()));
            }
        }
    }

}
