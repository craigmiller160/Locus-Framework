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

import java.util.Collection;
import java.util.Set;

/**
 * A special class that simply serves to output a log
 * of all Locus configuration data at the TRACE level,
 * so it can be viewed by folks debugging the application.
 *
 * Created by craigmiller on 4/6/16.
 */
class LocusDebug {

    private LocusStorage storage;
    private static final Logger logger = LoggerFactory.getLogger(LocusDebug.class);

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

    private static final String LS = System.lineSeparator();

    LocusDebug(){
        this.storage = LocusStorage.getInstance();
    }

    //Testing constructor only
    LocusDebug(LocusStorage storage){
        this.storage = storage;
    }

    public void logLocusConfiguration(){
        logger.info("Logging all Locus configuration details. Set logging to lowest level to view details for debugging.");

        logger.trace(CONFIG_OUTPUT_TITLE);
        logger.trace(getModelPropertyOutput());
        logger.trace(getViewPropertyOutput());
        logger.trace(getControllerOutput());
    }

    private String getModelPropertyOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(MODEL_OUTPUT_TITLE).append(LS);

        Set<String> propertyNames = storage.getAllModelPropertyNames();
        for(String prop : propertyNames){
            builder.append(String.format(" %-7s: ", PROPERTY_HEADER)).append(prop).append(LS);

            Collection<ObjectAndMethod> setters = storage.getSettersForModelProp(prop);
            Collection<ObjectAndMethod> getters = storage.getGettersForModelProp(prop);
            Collection<ObjectAndMethod> adders = storage.getAddersForModelProp(prop);
            Collection<ObjectAndMethod> removers = storage.getRemoversForModelProp(prop);

            if(setters != null && setters.size() > 0){
                builder.append(String.format("   %-7s: ", SETTER_METHOD)).append(LS);
                for(ObjectAndMethod oam : setters){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            if(getters != null && getters.size() > 0){
                builder.append(String.format("   %-7s: ", GETTER_METHOD)).append(LS);
                for(ObjectAndMethod oam : getters){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            if(adders != null && adders.size() > 0){
                builder.append(String.format("   %-7s: ", ADDER_METHOD)).append(LS);
                for(ObjectAndMethod oam : adders){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }

            if(removers != null && removers.size() > 0){
                builder.append(String.format("   %-7s: ", REMOVER_METHOD)).append(LS);
                for(ObjectAndMethod oam : removers){
                    builder.append(String.format("     %s", oam.getMethod().toString())).append(LS);
                }
            }
        }

        return builder.toString();
    }

    private String getViewPropertyOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(VIEW_OUTPUT_TITLE).append(LS);

        Set<String> propertyNames = storage.getAllViewPropNames();
        for(String prop : propertyNames){
            Collection<ClassAndMethod> setters = storage.getSettersForViewProp(prop);
            Collection<ClassAndMethod> adders = storage.getAddersForViewProp(prop);
            Collection<ClassAndMethod> removers = storage.getRemoversForViewProp(prop);

            if(setters != null && setters.size() > 0){
                builder.append(String.format("   %-7s: ", SETTER_METHOD)).append(LS);
                for(ClassAndMethod cam : setters){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }

            if(adders != null && adders.size() > 0){
                builder.append(String.format("   %-7s: ", ADDER_METHOD)).append(LS);
                for(ClassAndMethod cam : adders){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }

            if(removers != null && removers.size() > 0){
                builder.append(String.format("   %-7s: ", REMOVER_METHOD)).append(LS);
                for(ClassAndMethod cam : removers){
                    builder.append(String.format("     %s", cam.getMethod().toString())).append(LS);
                }
            }
        }

        return builder.toString();
    }

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

    public void outputLocusConfigurationToConsole(){
        System.out.println(CONFIG_OUTPUT_TITLE);
        System.out.println(getModelPropertyOutput());
        System.out.println(getViewPropertyOutput());
        System.out.println(getControllerOutput());
    }

}
