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

package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.annotations.LController;
import io.craigmiller160.locus.annotations.LModel;
import io.craigmiller160.locus.annotations.LView;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ReflectiveException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

/**
 * <p>A LocusScanner implementation designed for scanning
 * packages for annotated classes.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable state
 * and is therefore completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
@ThreadSafe
public class LocusPackageScanner extends AbstractLocusScanner{

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusPackageScanner.class);

    /**
     * Create a new LocusPackageScanner. This constructor should
     * only be directly called for testing, and otherwise this
     * class should be created by the LocusScannerFactory.
     *
     * @see io.craigmiller160.locus.scan.LocusScannerFactory
     */
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

    /**
     * Iterate through any model classes found via the package scanning,
     * and parse them.
     *
     * @param reflections the Reflections object to use to find the model classes.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     */
    private void parseModelClasses(Reflections reflections, LocusStorage storage, ScannerExclusions exclusions){
        Set<Class<?>> models = reflections.getTypesAnnotatedWith(LModel.class);
        for(Class<?> modelType : models){
            parseModelClass(modelType, storage, exclusions);
        }
    }

    /**
     * Iterate through any controller classes found via the package scanning,
     * and parse them.
     *
     * @param reflections the Reflections object to use to find the model classes.
     * @param storage the LocusStorage.
     */
    private void parseControllerClasses(Reflections reflections, LocusStorage storage){
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(LController.class);
        for(Class<?> controllerType : controllers) {
            parseControllerClass(controllerType, storage);
        }
    }

    /**
     * Iterate through any view classes found via the package scanning,
     * and parse them.
     *
     * @param reflections the Reflections object to use to find the model classes.
     * @param storage the LocusStorage.
     * @param exclusions the ScannerExclusions.
     */
    private void parseViewClasses(Reflections reflections, LocusStorage storage, ScannerExclusions exclusions){
        Set<Class<?>> views = reflections.getTypesAnnotatedWith(LView.class);
        for(Class<?> viewType : views){
            parseViewClass(viewType, storage, exclusions);
        }
    }
}
