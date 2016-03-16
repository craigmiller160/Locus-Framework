package io.craigmiller160.locus.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A class with JUnit test cases to test
 * the DOMConfigurationReader.
 *
 * Created by craig on 3/15/16.
 */
public class DOMConfigurationReaderTest {

    @Test
    public void testReadConfiguration(){
        ConfigurationReader configReader = new DOMConfigurationReader();
        LocusConfiguration locusConfig = configReader.readConfiguration("locus.xml");

        assertEquals("Wrong number of packages returned", locusConfig.getPackageCount(), 1);
        assertEquals("Wrong package name returned", locusConfig.getPackageNames().get(0), "io.craigmiller160.locus.sample");
    }

}
