/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.craigmiller160.locus;

import io.craigmiller160.locus.util.LocusStorage;

/**
 * A special callback class for the LocusController.
 * It wraps around an instance of a view, which it
 * is then able to reflective invoke getters on.
 *
 * Created by craig on 4/6/16.
 */
public class LocusControllerCallback {

    private Object callback;

    LocusControllerCallback(Object callback){
        this.callback = callback;
    }

    public Object getValue(String propName, Object...args){
        //TODO finish this
        return null;
    }

    public <T> T getValue(String propName, Class<?> resultType, Object...args){
        //TODO finish this
        return null;
    }

}
