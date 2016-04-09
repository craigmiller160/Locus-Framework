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

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.annotations.Controller;
import io.craigmiller160.locus.annotations.Model;
import io.craigmiller160.locus.annotations.View;
import io.craigmiller160.locus.reflect.ClassAndMethod;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.MethodUtils;
import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.reflect.ObjectCreator;
import io.craigmiller160.locus.reflect.ReflectiveMethodHolder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The current default implementation of the
 * LocusScanner API.
 *
 * Created by craig on 3/12/16.
 */
public class LocusScannerImpl implements LocusScanner{

    private static final Logger logger = LoggerFactory.getLogger(LocusScannerImpl.class);

    private static final String MODEL_CATEGORY = "Model";
    private static final String VIEW_CATEGORY = "View";
    private static final String CONTROLLER_CATEGORY = "Controller";

    LocusScannerImpl(){}

    @Override
    public void scanPackage(String packageName, LocusStorage storage, ScannerExclusions scannerExclusions) throws LocusReflectiveException{
        logger.debug("Beginning scan of package \"{}\" for annotated classes", packageName);
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
        );

        parseModelClasses(reflections, storage, scannerExclusions);
        parseControllerClasses(reflections, storage);
        parseViewClasses(reflections, storage, scannerExclusions);

        logger.info("Package {} scanned.", packageName);
        logger.debug("Total view property setters registered: {}", storage.getViewPropSetterCount());
        logger.debug("Total model property setters registered: {}", storage.getModelPropSetterCount());
        logger.debug("Total model property getters registered: {}", storage.getModelPropGetterCount());
        logger.debug("Total controllers registered: {}", storage.getControllerCount());
    }

    @Override
    public void scanPackage(String packageName, LocusStorage storage) throws LocusReflectiveException{
        scanPackage(packageName, storage, null);
    }

    private void parseViewClasses(Reflections reflections, LocusStorage storage, ScannerExclusions scannerExclusions) throws LocusReflectiveException{
        Set<Class<?>> views = reflections.getTypesAnnotatedWith(View.class);
        for(Class<?> viewType : views){
            Method[] publicMethods = viewType.getMethods();
            for(Method m : publicMethods){
                if(m.getName().startsWith("set") && isClassAllowed(m.getDeclaringClass(), scannerExclusions)){
                    String propName = m.getName().substring(3);
                    ClassAndMethod cam = new ClassAndMethod(viewType, m);
                    logger.trace("Adding view property setter to storage. Property: {} | Setter: {}", propName, cam.toString());
                    storage.addViewPropSetter(propName, cam);
                }
            }
        }
    }

    private void parseControllerClasses(Reflections reflections, LocusStorage storage) throws LocusReflectiveException{
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for(Class<?> controllerType : controllers){
            Controller con = controllerType.getAnnotation(Controller.class);
            String name = con.name();
            boolean singleton = con.singleton();
            validateUniqueController(name, controllerType, storage);
            logger.trace("Adding controller type to storage. Name: {} | Class: {}", name, controllerType);
            storage.addControllerType(name, controllerType, singleton);
        }
    }

    private void parseModelClasses(Reflections reflections, LocusStorage storage, ScannerExclusions scannerExclusions) throws LocusReflectiveException{
        Set<Class<?>> models = reflections.getTypesAnnotatedWith(Model.class);
        for(Class<?> modelType : models){
            Object model = ObjectCreator.instantiateClass(modelType);
            Method[] publicMethods = modelType.getMethods();
            for(Method m : publicMethods){
                if(m.getName().startsWith("set") && isClassAllowed(m.getDeclaringClass(), scannerExclusions)){
                    String propName = m.getName().substring(3);
                    ObjectAndMethod oam = new ObjectAndMethod(model, m);
                    validateUniqueMethod(propName, MODEL_CATEGORY, oam, storage.getAllModelPropSetters());
                    logger.trace("Adding model property setter to storage. Property: {} | Setter: {}", propName, oam.toString());
                    storage.addModelPropSetter(propName, oam);
                }
                else if((m.getName().startsWith("get") || m.getName().startsWith("is")) &&
                        isClassAllowed(m.getDeclaringClass(), scannerExclusions)){
                    //Set the propName differently for either a "get" or "is" prefix, based on their different lengths
                    String propName = m.getName().startsWith("get") ? m.getName().substring(3) : m.getName().substring(2);
                    ObjectAndMethod oam = new ObjectAndMethod(model, m);
                    validateUniqueMethod(propName, MODEL_CATEGORY, oam, storage.getAllModelPropGetters());
                    logger.trace("Adding model property getter to storage. Property: {} | Getter: {}", propName, oam.toString());
                    storage.addModelPropGetter(propName, oam);
                }
            }
        }
    }

    private boolean isClassAllowed(Class<?> clazz, ScannerExclusions scannerExclusions){
        return scannerExclusions == null || scannerExclusions.isClassAllowed(clazz);
    }

    private void validateUniqueController(String controllerName, Class<?> controllerType, LocusStorage storage) throws LocusReflectiveException{
        Set<String> controllerNames = storage.getAllControllerNames();

        //If no map provided, no validation can occur. This is probably due to no controllers existing yet
        if(controllerNames == null){
            return;
        }

        //Test the controller name to ensure it is unique
        for(String otherName : controllerNames){
            if(controllerName.equals(otherName)){
                throw new LocusReflectiveException(
                        String.format("Identically named controllers are not allowed.%1$s" +
                                "  Name: %2$s | Type: %3$s%1$s  Name: %4$s | Type: %5$s",
                                System.lineSeparator(), controllerName, controllerType.getName(),
                                otherName, storage.getControllerType(otherName))
                );
            }
        }
    }

    private void validateUniqueMethod(String propName, String category, ReflectiveMethodHolder<?> rmh,
                                      Collection<? extends ReflectiveMethodHolder<?>> otherOams) throws LocusReflectiveException{
        //If no collection provided, no validation can occur. This is probably due to no methods for that property existing
        if(otherOams == null){
            return;
        }

        Method m1 = rmh.getMethod();
        for(ReflectiveMethodHolder<?> rmh2 : otherOams){
            Method m2= rmh2.getMethod();
            if(m1.getName().equals(m2.getName())){
                throw new LocusReflectiveException("Identical methods for single property in single category not allowed." + System.lineSeparator() +
                        "   Category: " + category + " | Property: " + propName + System.lineSeparator() +
                        "   Class: " + rmh.getSourceType().getName() + " | Method: " + m1.getName() +
                        "   Class: " + rmh2.getSourceType().getName() + " | Method: " + m2.getName()
                );
            }
        }
    }


}
