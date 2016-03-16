package io.craigmiller160.locus.util;

/**
 * Special factory class to provide the instance of the
 * ConfigurationReader being used by the framework.
 * It allows the reader's implementation to be
 * abstracted away from its API.
 *
 * Created by craig on 3/15/16.
 */
public class ConfigurationReaderFactory {

    /**
     * Get a new instance of this factory class.
     *
     * @return a new instance of ConfigurationReaderyFactory.
     */
    public static ConfigurationReaderFactory getInstance(){
        return new ConfigurationReaderFactory();
    }

    private ConfigurationReaderFactory(){}

    /**
     * Create a new implementation of ConfigurationReader.
     *
     * @return an implementation of ConfigurationReader.
     */
    public ConfigurationReader newConfigurationReader(){
        return new DOMConfigurationReader();
    }
    
}
