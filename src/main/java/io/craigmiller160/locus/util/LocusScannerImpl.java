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
        logger.debug("Scanning package \"" + packageName + "\" for annotated classes");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
        );

        parseModelClasses(reflections, storage, scannerExclusions);
        parseControllerClasses(reflections, storage, scannerExclusions);
        parseViewClasses(reflections, storage, scannerExclusions);
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
                    String propName = m.getName().substring(3, m.getName().length());
                    ClassAndMethod cam = new ClassAndMethod(viewType, m);
                    logger.debug("Adding view property setter to storage. Property: " + propName + " | Setter: " + cam.toString());
                    storage.addViewPropSetter(propName, cam);
                }
                else if(m.getName().startsWith("get") && isClassAllowed(m.getDeclaringClass(), scannerExclusions)){
                    String propName = m.getName().substring(3, m.getName().length());
                    ClassAndMethod cam = new ClassAndMethod(viewType, m);
                    validateUniqueMethod(propName, VIEW_CATEGORY, cam, storage.getGettersForViewProp(propName));
                    logger.debug("Adding view property getter to storage. Property: " + propName + " | Getter: " + cam.toString());
                    storage.addViewPropGetter(propName, cam);
                }
            }
        }
    }

    private void parseControllerClasses(Reflections reflections, LocusStorage storage, ScannerExclusions scannerExclusions) throws LocusReflectiveException{
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for(Class<?> controllerType : controllers){
            Controller con = controllerType.getAnnotation(Controller.class);
            String name = con.name();
            boolean singleton = con.singleton();
            validateUniqueController(name, controllerType, storage.getAllControllerTypes());
            logger.debug("Adding controller type to storage. Name: " + name + " | Class: " + controllerType);
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
                    String propName = m.getName().substring(3, m.getName().length());
                    ObjectAndMethod oam = new ObjectAndMethod(model, m);
                    validateUniqueMethod(propName, MODEL_CATEGORY, oam, storage.getSettersForModelProp(propName));
                    logger.debug("Adding model property setter to storage. Property: " + propName + " | Setter: " + oam.toString());
                    storage.addModelPropSetter(propName, oam);
                }
                else if(m.getName().startsWith("get") && isClassAllowed(m.getDeclaringClass(), scannerExclusions)){
                    String propName = m.getName().substring(3, m.getName().length());
                    ObjectAndMethod oam = new ObjectAndMethod(model, m);
                    validateUniqueMethod(propName, MODEL_CATEGORY, oam, storage.getGettersForModelProp(propName));
                    logger.debug("Adding model property getter to storage. Property: " + propName + " | Getter: " + oam.toString());
                    storage.addModelPropGetter(propName, oam);
                }
            }
        }
    }

    private boolean isClassAllowed(Class<?> clazz, ScannerExclusions scannerExclusions){
        return scannerExclusions == null || scannerExclusions.isClassAllowed(clazz);
    }

    private void validateUniqueController(String controllerName, Class<?> controllerType,
                                          Map<String,Class<?>> controllerTypes) throws LocusReflectiveException{
        //If no map provided, no validation can occur. This is probably due to no controllers existing yet
        if(controllerTypes == null){
            return;
        }

        Set<String> controllerNames = controllerTypes.keySet();
        for(String otherName : controllerNames){
            if(controllerName.equals(otherName)){
                throw new LocusReflectiveException("Identically named controllers are not allowed." + System.lineSeparator() +
                        "   Name: " + controllerName + " | Type: " + controllerType.getName()  + System.lineSeparator() +
                        "   Name: " + otherName + " | Type: " + controllerTypes.get(otherName).getName()
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
            if(MethodUtils.isDuplicateMethod(m1, m2)){
                throw new LocusReflectiveException("Identical methods for single property in single category not allowed. Please be aware that identical methods from a superclass can cause this exception." + System.lineSeparator() +
                        "   Category: " + category + " | Property: " + propName + System.lineSeparator() +
                        "   Class: " + rmh.getSourceType().getName() + " | Method: " + m1.getName() + " | Params: " + Arrays.toString(m1.getParameterTypes()) + System.lineSeparator() +
                        "   Class: " + rmh2.getSourceType().getName() + " | Method: " + m2.getName() + " | Params: " + Arrays.toString(m2.getParameterTypes())
                );
            }
        }
    }


}
