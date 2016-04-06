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

import io.craigmiller160.locus.reflect.ObjectAndMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A special class that simply serves to output a log
 * of all Locus configuration data at the TRACE level,
 * so it can be viewed by folks debugging the application.
 *
 * Created by craigmiller on 4/6/16.
 */
public class LocusConfigurationLogger {

    private static final LocusStorage storage = LocusStorage.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(LocusConfigurationLogger.class);

    private static final String CONFIG_OUTPUT_TITLE = "LOCUS PROPERTY CONFIGURATION";
    private static final String MODEL_OUTPUT_TITLE = "MODEL PROPERTIES";
    private static final String VIEW_OUTPUT_TITLE = "VIEW PROPERTIES";
    private static final String CONTROLLER_OUTPUT_TITLE = "CONTROLLERS";

    public static void logLocusConfiguration(){
        logger.info("Logging all Locus configuration details. Set logging to lowest level to view details for debugging.");

        logger.trace(CONFIG_OUTPUT_TITLE);
        logger.trace(getModelPropertyOutput());
        logger.trace(getViewPropertyOutput());
        logger.trace(getControllerOutput());
    }

    private static String getModelPropertyOutput(){
        StringBuilder builder = new StringBuilder();

        builder.append(MODEL_OUTPUT_TITLE);

        Map<String,ObjectAndMethod> modelSetters = storage.getAllModelPropSetters();
        Map<String,ObjectAndMethod> modelGetters = storage.getAllModelPropGetters();



        return builder.toString();
    }

    private static String getViewPropertyOutput(){
        //TODO finish this
        return null;
    }

    private static String getControllerOutput(){
        //TODO finish this
        return null;
    }

    public static void outputLocusConfigurationToConsole(){
        System.out.println(CONFIG_OUTPUT_TITLE);
        System.out.println(getModelPropertyOutput());
        System.out.println(getViewPropertyOutput());
        System.out.println(getControllerOutput());
    }

}
