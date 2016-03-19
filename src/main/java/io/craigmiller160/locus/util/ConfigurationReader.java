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

/**
 * The ConfigurationReader handles accessing the xml
 * configuration file for the framework, and returning
 * the configuration values to the application.
 *
 * Created by craig on 3/15/16.
 */
public interface ConfigurationReader {

    /**
     * Read the XML configuration file pointed to by the provided
     * file name and return its values in a LocusConfiguration
     * object.
     *
     * @param fileName the name of the XML file to read.
     * @return the configuration for the framework.
     * @throws LocusParsingException if unable to find, read, or parse
     *          the content of the XML configuration file.
     */
    LocusConfiguration readConfiguration(String fileName) throws LocusParsingException;

}
