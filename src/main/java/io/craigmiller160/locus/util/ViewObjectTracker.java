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

package io.craigmiller160.locus.util;

import io.craigmiller160.utils.collection.MultiValueMap;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * A special implementation of MultiValueMap designed for
 * tracking views that have been instantiated. It uses the
 * Class type as the key, and a WeakReference to the instantiated
 * view as the value. Every time the collection of WeakReferences
 * is retrieved, it is reviewed for null values, which are then
 * removed. This prevents too much memory from being taken up by
 * useless WeakReferences.
 *
 * This design has the potential for a performance hit, but only
 * if individual view classes are being instantiated a LOT of times.
 *
 * Created by craig on 3/14/16.
 */
public class ViewObjectTracker extends MultiValueMap<Class<?>,WeakReference<?>> {

    @Override
    public Collection<WeakReference<?>> get(Object key){
        Collection<WeakReference<?>> values = super.get(key);
        Collection<WeakReference<?>> toRemove = getNewCollection();
        for(WeakReference<?> weakRef : values){
            if(weakRef.get() == null){
                toRemove.add(weakRef);
            }
        }

        for(WeakReference<?> weakRef : toRemove){
            values.remove(weakRef);
        }

        return values;
    }

}
