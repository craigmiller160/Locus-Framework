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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void logLocusConfiguration(){
        logger.info("Logging all Locus configuration details. Set logging to lowest level to view details for debugging.");

        //TODO log each model class that has been scanned, the properties it has, and the types of methods (eg getters/setters) each property has
        //TODO log each view class that has been scanned, the properties it has, and the types of methods (eg getters/setters) each property has.
        //TODO log each controller class, and its corresponding name
    }

}
