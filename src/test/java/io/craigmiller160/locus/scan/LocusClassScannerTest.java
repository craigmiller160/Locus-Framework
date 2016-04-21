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

package io.craigmiller160.locus.scan;

import io.craigmiller160.locus.TestUtils;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A JUnit test class to test the LocusClassScanner
 * class.
 *
 * Created by craigmiller on 4/21/16.
 */
public class LocusClassScannerTest {

    private static final String MODEL_ONE_PATH = "io.craigmiller160.locus.sample.ModelOne";

    private LocusStorage storage;
    private LocusClassScanner scanner;

    @Before
    public void before(){
        storage = TestUtils.setupStorage();
        scanner = new LocusClassScanner();
    }

    @Test
    public void testScanClass(){
        scanner.scan(MODEL_ONE_PATH, storage);
        int modelPropSetterCount = storage.getModelPropSetterCount();
        int modelPropGetterCount = storage.getModelPropSetterCount();

        assertEquals("Wrong number of model prop setters", 12, modelPropSetterCount);
        assertEquals("Wrong number of model prop getters", 12, modelPropGetterCount);
    }

}
