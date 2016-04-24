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
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Set;

/**
 * <p>A special class that simply serves to output a log
 * of all Locus configuration data. There are two main ways
 * this information is outputted. logLocusConfiguration()
 * used the logger, and the information will be outputted
 * to any logging targets configured. outputLocusConfigurationToConsole()
 * is specifically designed for more temporary debugging,
 * and will output all the information directly to the
 * console.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class is completely thread-safe.
 * Its only mutable state is the LocusStorage field, and that class
 * is properly synchronized and thread safe.</p>
 *
 * @author craigmiller
 * @version 1.2
 */
@ThreadSafe
class LocusDebug {

    /**
     * The LocusStorage.
     */
    private LocusStorage storage;

    /**
     * The Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LocusDebug.class);

    /*
     * Repetitive values are all constants for consistency.
     */

    private static final String CONFIG_OUTPUT_TITLE = "LOCUS PROPERTY CONFIGURATION";
    private static final String MODEL_OUTPUT_TITLE = "MODEL PROPERTIES";
    private static final String VIEW_OUTPUT_TITLE = "VIEW PROPERTIES";
    private static final String CONTROLLER_OUTPUT_TITLE = "CONTROLLERS";

    private static final String PROPERTY_HEADER = "Property";
    private static final String CLASS_HEADER = "Class";
    private static final String CLASSES_HEADER = "Classes";
    private static final String METHODS_HEADER = "Methods";
    private static final String NAME_HEADER = "Name";

    private static final String SETTER_METHOD = "Setters";
    private static final String GETTER_METHOD = "Getters";
    private static final String ADDER_METHOD = "Adders";
    private static final String REMOVER_METHOD = "Removers";

    //The platform-independent line separator
    private static final String LS = System.lineSeparator();

    /**
     * Create a new instance of LocusDebug.
     */
    LocusDebug(){
        this.storage = LocusStorage.getInstance();
    }

    /**
     * Create a new instance of LocusDebug, used
     * for testing purposes only.
     *
     * @param storage the LocusStorage.
     */
    LocusDebug(LocusStorage storage){
        this.storage = storage;
    }

    /**
     * Output all Locus configuration values using the logger
     * configured for the application.
     */
    public void logLocusConfiguration(){
        logger.info("Logging all Locus configuration details. Set logging to lowest level to view details for debugging.");

        logger.trace(CONFIG_OUTPUT_TITLE);
        logger.trace(getModelPropertyOutput());
        logger.trace(getViewPropertyOutput());
        logger.trace(getControllerOutput());
    }

    /**
     * Get the output String for all model property configuration values.
     *
     * @return the model property configuration.
     */
    private String getModelPropertyOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(MODEL_OUTPUT_TITLE).append(LS);

        //Get all property names
        Set<String> propertyNames = storage.getAllModelPropertyNames();
        for(String prop : propertyNames){
            builder.append(String.format(" %-7s: ", PROPERTY_HEADER)).append(prop).append(LS);

            Collection<ObjectAndMethod> setters = storage.getSettersForModelProp(prop);
            Collection<ObjectAndMethod> getters = storage.getGettersForModelProp(prop);
            Collection<ObjectAndMethod> adders = storage.getAddersForModelProp(prop);
            Collection<ObjectAndMethod> removers = storage.getRemoversForModelProp(prop);

            //If there are setters, add their info to the output
            if(setters != null && setters.size() > 0){
                builder.append(String.format("   %-7s: ", SETTER_METHOD)).append(LS);
                for(ObjectAndMethod oam : setters){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            //If there are getters, add their info to the output
            if(getters != null && getters.size() > 0){
                builder.append(String.format("   %-7s: ", GETTER_METHOD)).append(LS);
                for(ObjectAndMethod oam : getters){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            //If there are adders,add their info to the output
            if(adders != null && adders.size() > 0){
                builder.append(String.format("   %-7s: ", ADDER_METHOD)).append(LS);
                for(ObjectAndMethod oam : adders){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            //If there are removers, add their info to the output
            if(removers != null && removers.size() > 0){
                builder.append(String.format("   %-7s: ", REMOVER_METHOD)).append(LS);
                for(ObjectAndMethod oam : removers){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }
        }

        return builder.toString();
    }

    //TODO this will need to be updated to have information about view instances

    /**
     * Get the output String for all view property configuration values.
     *
     * @return the view property configuration.
     */
    private String getViewPropertyOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(VIEW_OUTPUT_TITLE).append(LS);

        //Get all view property names
        Set<String> propertyNames = storage.getAllViewPropNames();
        for(String prop : propertyNames){
            Collection<ClassAndMethod> setters = storage.getSettersForViewProp(prop);
            Collection<ClassAndMethod> adders = storage.getAddersForViewProp(prop);
            Collection<ClassAndMethod> removers = storage.getRemoversForViewProp(prop);

            //If there are setters, add them to output
            if(setters != null && setters.size() > 0){
                builder.append(String.format("   %-7s: ", SETTER_METHOD)).append(LS);
                for(ClassAndMethod cam : setters){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }

            //If there are adders, add them to output
            if(adders != null && adders.size() > 0){
                builder.append(String.format("   %-7s: ", ADDER_METHOD)).append(LS);
                for(ClassAndMethod cam : adders){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }

            //If there are removers, add them to output
            if(removers != null && removers.size() > 0){
                builder.append(String.format("   %-7s: ", REMOVER_METHOD)).append(LS);
                for(ClassAndMethod cam : removers){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }
        }

        return builder.toString();
    }

    //TODO information on controller callbacks needs to be added to this output

    /**
     * Get the output String for all controller configuration values.
     *
     * @return the controller configuration.
     */
    private String getControllerOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(CONTROLLER_OUTPUT_TITLE).append(LS);

        Set<String> controllerNames = storage.getAllControllerNames();
        for(String name : controllerNames){
            builder.append(String.format(" %-7s: ", NAME_HEADER)).append(name).append(LS);
            String classType = storage.getControllerType(name).getName();
            builder.append(String.format("   %-7s: ", CLASS_HEADER)).append(classType).append(LS);
        }

        return builder.toString();
    }

    /**
     * Output all Locus configuration values to the console.
     */
    public void outputLocusConfigurationToConsole(){
        System.out.println(CONFIG_OUTPUT_TITLE);
        System.out.println(getModelPropertyOutput());
        System.out.println(getViewPropertyOutput());
        System.out.println(getControllerOutput());
    }

}
