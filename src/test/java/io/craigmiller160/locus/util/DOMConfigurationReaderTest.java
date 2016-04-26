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

import io.craigmiller160.locus.scan.ScannerExclusions;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A class with JUnit test cases to test
 * the DOMConfigurationReader.
 *
 * Created by craig on 3/15/16.
 */
public class DOMConfigurationReaderTest {

    private LocusConfiguration getConfig(String configFile) throws Exception{
        ConfigurationReader configReader = new DOMConfigurationReader();

        LocusConfiguration locusConfig = null;
        try(InputStream iStream = this.getClass().getClassLoader().getResourceAsStream(configFile)){
            locusConfig = configReader.readConfiguration(iStream);
        }
        catch(IOException ex){
            throw ex;
        }

        return locusConfig;
    }

    /**
     * Test reading a locus configuration file
     * with a "packages", and not a "classes", element.
     */
    @Test
    public void testReadPackageConfiguration() throws Exception{
        LocusConfiguration locusConfig = getConfig("locus.xml");

        assertEquals("Wrong number of packages returned", 1, locusConfig.getPackageCount());
        assertEquals("No classes should've been returned", 0, locusConfig.getClassNameCount());
        assertEquals("Wrong package name returned", "io.craigmiller160.locus.sample", locusConfig.getPackageNames().get(0));

        ScannerExclusions scannerExclusions = locusConfig.getScannerExclusions();
        Set<String> exclusions = scannerExclusions.getAllExclusions();
        Set<String> inclusions = scannerExclusions.getAllInclusions();

        assertTrue(exclusions.contains("org.foo"));
        assertTrue(inclusions.contains("org.foo.foo2"));

        String uiThreadExecutorClassName = locusConfig.getUIThreadExecutorClassName();
        assertNotNull("UiThreadExecutorClassName is null", uiThreadExecutorClassName);
        assertEquals("UIThreadExecutorClassName has the wrong value", "io.craigmiller160.locus.sample.SampleUIThreadExecutor",
                uiThreadExecutorClassName);
    }

    /**
     * Test reading a locus configuration file with
     * a "classes", and not a "packages", element.
     */
    @Test
    public void testReadClassConfiguration() throws Exception{
        LocusConfiguration locusConfig = getConfig("locus2.xml");

        assertEquals("No packages should've been returned", 0, locusConfig.getPackageCount());
        assertEquals("Wrong number of classes returned", 3, locusConfig.getClassNameCount());

        ScannerExclusions scannerExclusions = locusConfig.getScannerExclusions();
        Set<String> exclusions = scannerExclusions.getAllExclusions();
        Set<String> inclusions = scannerExclusions.getAllInclusions();

        assertTrue(exclusions.contains("org.foo"));
        assertTrue(inclusions.contains("org.foo.foo2"));

        String uiThreadExecutorClassName = locusConfig.getUIThreadExecutorClassName();
        assertNotNull("UiThreadExecutorClassName is null", uiThreadExecutorClassName);
        assertEquals("UIThreadExecutorClassName has the wrong value", "io.craigmiller160.locus.sample.SampleUIThreadExecutor",
                uiThreadExecutorClassName);
    }

}
