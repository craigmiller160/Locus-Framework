package io.craigmiller160.locus.concurrent;

import io.craigmiller160.locus.LocusException;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.ObjectCreator;

/**
 * A factory to provide UIThreadExecutor instances
 * in a static way. An instance of this class will
 * never exist publicly. Instead, the instance is
 * created internally, and in doing so the UIThreadExecutor
 * is set. Only one instance for the application may
 * ever be created.
 *
 * Once the instance is created, a reference to the
 * UIThreadExecutor can be retrieved statically from it.
 * If an instance of this factory class is not created first,
 * or if there is an attempt to create it multiple times,
 * exceptions will be thrown.
 *
 * Created by craig on 4/11/16.
 */
public class UIThreadExecutorFactory {

    /**
     * The UIThreadExecutor that this factory class
     * provides to the application.
     */
    private final UIThreadExecutor uiThreadExecutor;

    /**
     * The private instance of this factory class,
     * which is never leaked out.
     */
    private static UIThreadExecutorFactory instance;

    /**
     * Create an instance of this UIThreadExecutorFactory. This instance
     * is NOT returned by this method, rather it exists internally
     * within this class. Creating an instance requires a UIThreadExecutor
     * implementation class, which is instantiated and placed within
     * the private instance of this factory, to be retrieved later.
     *
     * @param uiThreadExecutor the class type of an implementation of the UIThreadExecutor interface.
     * @throws LocusReflectiveException if unable to instantiate the UIThreadExecutor
     *          implementation class.
     * @throws LocusException if unable to meet the criteria for creating
     *          a new instance of this factory class. For example, only
     *          one instance can be created.
     */
    public static void createInstance(Class<? extends UIThreadExecutor> uiThreadExecutor){
        if(instance == null){
            synchronized (UIThreadExecutorFactory.class){
                if(instance == null){
                    if(uiThreadExecutor == null){
                        throw new LocusException("Cannot create new UIThreadExecutorFactory with a null UIThreadExecutor");
                    }
                    UIThreadExecutor uteInstance = ObjectCreator.instantiateClass(uiThreadExecutor);
                    instance = new UIThreadExecutorFactory(uteInstance);
                }
                else{
                    throw new LocusException("An instance of UIThreadExecutorFactory already exists, another cannot be created");
                }
            }
        }
    }

    /**
     * Get the instance of the UIThreadExecutor for this
     * application.
     *
     * @return the UIThreadExecutor.
     */
    public static UIThreadExecutor getUIThreadExecutor(){
        if(instance == null){
            throw new LocusException("An instance must be created with a UIThreadExecutor class first");
        }
        return instance.get();
    }

    /**
     * Private constructor creates the private
     * instance of this factory class.
     *
     * @param uiThreadExecutor the UIThreadExecutor implementation.
     */
    private UIThreadExecutorFactory(UIThreadExecutor uiThreadExecutor){
        this.uiThreadExecutor = uiThreadExecutor;
    }

    /**
     * Private getter method returns the implementation
     * of UIThreadExecutor that this factory produces.
     *
     * @return the UIThreadExecutor implementation.
     */
    private UIThreadExecutor get(){
        return uiThreadExecutor;
    }

}
