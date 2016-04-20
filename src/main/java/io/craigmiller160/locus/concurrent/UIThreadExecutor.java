/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.craigmiller160.locus.concurrent;

import java.util.concurrent.Callable;

/**
 * A special interface for using this framework
 * in multi-threaded environments. If an implementation
 * is provided in the framework configuration,
 * any locus actions to set or get values from a view
 * class will pass through these methods. This allows
 * client code to ensure that they are being executed
 * properly, no matter what UI toolkit is being used.
 *
 * How effective the thread safety of implementations
 * are is up to the client code that provides it.
 * Generally, to implement this properly, assume that
 * calls could either come from the UI Thread or from
 * a background thread, and structure the implementation
 * accordingly.
 *
 * SUGGESTIONS FOR IMPLEMENTATION:
 *
 * 1) Test to see if already on the UI thread before
 * implementing special logic.
 *
 * 2) All Exceptions must be RuntimeExceptions.
 * There is a provided class, ExceptionHandler, that
 * is perfect for parsing and re-throwing Exceptions
 * properly for this.
 *
 * 3) Keep the implementation stateless, ie no instance
 * fields, to maximize its thread safety. This is especially
 * important because this class will be instantiated
 * only once by the framework, and that single instnace
 * will be used for all cases.
 *
 * Created by craig on 4/9/16.
 */
public interface UIThreadExecutor {

    /**
     * Execute the provided task asynchronously on the UI thread.
     *
     * @param task the task.
     */
    void executeOnUIThread(Runnable task);

    /**
     * Execute the provided task synchronously on the UI thread,
     * and return a result.
     *
     * @param task the task.
     * @param <T> the type of the result.
     * @return the result of the task.
     */
    <T> T executeOnUIThreadWithResult(Callable<T> task);

}
