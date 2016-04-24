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


import io.craigmiller160.utils.reflect.ExceptionHandler;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Callable;

/**
 * <p>The default implementation of the UIThreadExecutor interface.
 * This is a non-concurrent implementation, it executes the provided
 * tasks on the same thread they came from.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable
 * state and is therefore completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.1
 */
@ThreadSafe
public class NoUIThreadExecutor implements UIThreadExecutor {

    @Override
    public void executeOnUIThread(Runnable task) {
        task.run();
    }

    @Override
    public <T> T executeOnUIThreadWithResult(Callable<T> task) {
        T result = null;
        try{
            result = task.call();
        }
        catch(Exception ex){
            ExceptionHandler.parseAndRethrowException(ex);
        }

        return result;
    }
}
