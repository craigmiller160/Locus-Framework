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
