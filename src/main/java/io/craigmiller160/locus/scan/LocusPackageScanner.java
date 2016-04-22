/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.annotations.LController;
import io.craigmiller160.locus.annotations.LModel;
import io.craigmiller160.locus.annotations.LView;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.locus.util.ScannerExclusions;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * A LocusScanner implementation designed for scanning
 * packages for annotated classes.
 *
 * Created by craig on 4/20/16.
 */
public class LocusPackageScanner extends AbstractLocusScanner{

    private static final Logger logger = LoggerFactory.getLogger(LocusPackageScanner.class);

    LocusPackageScanner(){}

    @Override
    public void scan(String packageName, LocusStorage storage) throws ReflectiveException {
        scan(packageName, storage, null);
    }

    @Override
    public void scan(String packageName, LocusStorage storage, ScannerExclusions scannerExclusions) throws ReflectiveException {
        logger.debug("Scanning of package \"{}\" for annotated classes", packageName);
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
        );

        parseModelClasses(reflections, storage, scannerExclusions);
        parseControllerClasses(reflections, storage);
        parseViewClasses(reflections, storage, scannerExclusions);
    }

    private void parseModelClasses(Reflections reflections, LocusStorage storage, ScannerExclusions exclusions){
        Set<Class<?>> models = reflections.getTypesAnnotatedWith(LModel.class);
        for(Class<?> modelType : models){
            parseModelClass(modelType, storage, exclusions);
        }
    }

    private void parseControllerClasses(Reflections reflections, LocusStorage storage){
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(LController.class);
        for(Class<?> controllerType : controllers) {
            parseControllerClass(controllerType, storage);
        }
    }

    private void parseViewClasses(Reflections reflections, LocusStorage storage, ScannerExclusions exclusions){
        Set<Class<?>> views = reflections.getTypesAnnotatedWith(LView.class);
        for(Class<?> viewType : views){
            parseViewClass(viewType, storage, exclusions);
        }
    }
}