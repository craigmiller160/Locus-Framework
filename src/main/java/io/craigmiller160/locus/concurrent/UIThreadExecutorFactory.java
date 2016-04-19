package io.craigmiller160.locus.concurrent;

import io.craigmiller160.locus.LocusException;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * A factory to provide UIThreadExecutor instances.
 * It pulls the class type of the UIThreadExecutor
 * from the LocusStorage, and instantiates an
 * instance of it via its factory methods.
 *
 * Created by craig on 4/11/16.
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
