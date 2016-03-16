package io.craigmiller160.locus.util;

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
                values.add(weakRef);
            }
        }

        for(WeakReference<?> weakRef : toRemove){
            values.remove(weakRef);
        }

        return values;
    }

}
