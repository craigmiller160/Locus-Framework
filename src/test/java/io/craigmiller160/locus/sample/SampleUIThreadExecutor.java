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

package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.concurrent.UIThreadExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by craigmiller on 4/10/16.
 */
public class SampleUIThreadExecutor implements UIThreadExecutor {

    public static final String THREAD_NAME = "UIThread";

    @Override
    public void executeOnUIThread(Runnable task) {
        Thread t = new Thread(task);
        t.setName(THREAD_NAME);
        t.start();
    }

    @Override
    public <T> T executeOnUIThreadWithResult(Callable<T> task){
        FutureTask<T> ft = new FutureTask<>(task);
        Thread t = new Thread(ft);
        t.setName(THREAD_NAME);
        t.start();

        T result = null;
        try{
            result = ft.get();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

        return result;
    }
}
