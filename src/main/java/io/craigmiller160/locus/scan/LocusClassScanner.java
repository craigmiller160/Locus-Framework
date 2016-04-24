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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>An implementation of LocusScanner to scan individual
 * classes denoted by their full, qualified path name.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable state
 * and is therefore completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
@ThreadSafe
public class LocusClassScanner extends AbstractLocusScanner {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusPackageScanner.class);

    /**
     * LocusClassScanner instances should be constructed from the
     * LocusScannerFactory class. This constructor is package-private
     * only to help facilitate testing.
     */
    LocusClassScanner(){}

    @Override
    public void scan(String className, LocusStorage storage) throws ReflectiveException {
        scan(className, storage, null);
    }

    @Override
    public void scan(String className, LocusStorage storage, ScannerExclusions exclusions) throws ReflectiveException {
        Class<?> clazz = findClass(className);
        logger.debug("Scanning class {}", className);

        //Based on the annotation, use the appropriate parsing method
        if(clazz.getAnnotation(LModel.class) != null){
            parseModelClass(clazz, storage, exclusions);
        }
        else if(clazz.getAnnotation(LView.class) != null){
            parseViewClass(clazz, storage, exclusions);
        }
        else if(clazz.getAnnotation(LController.class) != null){
            parseControllerClass(clazz, storage);
        }
        else{
            throw new ReflectiveException(String.format("Class must have a Locus annotation (LModel, LView, LController): %s", className));
        }
    }

    /**
     * Find a class with the provided name. If it is not found,
     * rethrow the exception as an unchecked LocusException.
     *
     * @param className the name of the class to find.
     * @return the class for the provided name.
     * @throws ReflectiveException if unable to find the class.
     */
    private Class<?> findClass(String className){
        Class<?> clazz = null;
        try{
            clazz = Class.forName(className);
        }
        catch(ClassNotFoundException ex){
            throw new ReflectiveException(String.format("Unable to find class: %s", className), ex);
        }
        return clazz;
    }
}
