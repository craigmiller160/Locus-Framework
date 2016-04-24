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

import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * <p>A factory to provide UIThreadExecutor instances.
 * It pulls the class type of the UIThreadExecutor
 * from the LocusStorage, and instantiates an
 * instance of it via its factory methods.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
public class UIThreadExecutorFactory {

    /**
     * The LocusStorage instance.
     */
    private final LocusStorage storage;

    /**
     * Create a UIThreadExecutorFactory. This constructor
     * is used for creating a new instance via the factory
     * method newInstance().
     */
    private UIThreadExecutorFactory(){
        this.storage = LocusStorage.getInstance();
    }

    /**
     * Create a UIThreadExecutorFactory. This constructor
     * is only used for creating an instance of this class
     * for testing.
     *
     * @param storage the LocusStorage for testing.
     */
    UIThreadExecutorFactory(LocusStorage storage){
        this.storage = storage;
    }

    /**
     * Create a new instance of this factory class.
     *
     * @return a new instance of UIThreadExecutorFactory.
     */
    public static UIThreadExecutorFactory newInstance(){
        return new UIThreadExecutorFactory();
    }

    /**
     * Get the UIThreadExecutor implementation produced by this
     * factory class.
     *
     * @return the UIThreadExector implementation.
     */
    public UIThreadExecutor getUIThreadExecutor(){
        Class<? extends UIThreadExecutor> clazz = storage.getUIThreadExecutorType();
        return ObjectCreator.instantiateClass(clazz);
    }

}
