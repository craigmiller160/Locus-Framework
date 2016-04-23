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

package io.craigmiller160.locus.concurrent;

import io.craigmiller160.locus.TestUtils;
import io.craigmiller160.locus.sample.SampleUIThreadExecutor;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test class for testing the
 * UIThreadExecutorFactory class.
 *
 * Created by craigmiller on 4/12/16.
 */
public class UIThreadExecutorFactoryTest {

    private UIThreadExecutorFactory factory;
    private LocusStorage storage;

    /**
     * Create the factory instance to be tested.
     */
    private void createFactory(){
        factory = new UIThreadExecutorFactory(storage);
    }

    /**
     * Run setup methods before each test.
     */
    @Before
    public void begin(){
        storage = TestUtils.setupStorage();
        storage.setUIThreadExecutorType(SampleUIThreadExecutor.class);
        createFactory();
    }

    /**
     * Test getting the correct UIThreadExecutor instance
     * from the factory.
     */
    @Test
    public void testGetUIThreadExecutor(){
        UIThreadExecutor executor = factory.getUIThreadExecutor();

        assertNotNull("UIThreadExecutor instance is null", executor);
        assertEquals("UIThreadExecutor is the wrong type", SampleUIThreadExecutor.class, executor.getClass());
    }

}
