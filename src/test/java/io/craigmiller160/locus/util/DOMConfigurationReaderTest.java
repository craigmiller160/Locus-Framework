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
