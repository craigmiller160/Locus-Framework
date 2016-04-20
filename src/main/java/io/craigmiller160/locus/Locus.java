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

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.locus.util.*;
import io.craigmiller160.utils.util.StringUtil;

import java.util.List;

/**
 * The central class of the Locus Framework.
 * This class provides easy, static access to
 * manipulate system resources through its three
 * subclasses.
 *
 * Created by craig on 3/12/16.
 */
public class Locus {

    public static final int GETTER = 101;
    public static final int SETTER = 102;
    public static final String DEFAULT_CONFIG = "locus.xml";

    private static final LocusStorage storage = LocusStorage.getInstance();
    private static final ConfigurationReader configReader = ConfigurationReaderFactory.newInstance().newConfigurationReader();
    private static final LocusScanner scanner = LocusScannerFactory.newInstance().newLocusScanner();

    /**
     * A boolean flag for whether or not Locus has already been
     * initialized.
     */
    private static volatile boolean initialized = false;

    /**
     * A special object to provide a lock for synchronizing
     * the initialization process, so it can't be called
     * by more than one thread at a time.
     */
    private static final Object initializeLock = new Object();

    public static LocusModel model = new LocusModel();

    public static LocusController controller = new LocusController();

    public static LocusView view = new LocusView();

    public static LocusDebug debug = new LocusDebug();


    /**
     * Initialize the Locus framework. This method will only
     * execute if it has not already been initialized.
     */
    @SuppressWarnings("unchecked")
    public static void initialize(){
        initialize(false);
    }

    /**
     * Initialize the Locus framework, with the option to
     * force a re-initialization.
     *
     * @param force if it should be re-initialized if already initialized.
     */
    @SuppressWarnings("unchecked")
    public static void initialize(boolean force){
        //Using this lock here to ensure that the initialization process can't be called by multiple threads simultaneously.
        synchronized (initializeLock){
            if(initialized && !force){
                return;
            }

            storage.clear();
            LocusConfiguration config = configReader.readConfiguration(DEFAULT_CONFIG);

            Class<? extends UIThreadExecutor> clazz = null;
            String uiThreadExecutorClassName = config.getUIThreadExecutorClassName();
            if(!StringUtil.isEmpty(uiThreadExecutorClassName)){
                try{
                    clazz = (Class<? extends UIThreadExecutor>) Class.forName(uiThreadExecutorClassName);
                }
                catch(ClassNotFoundException | ClassCastException ex){
                    throw new LocusException(String.format("\"%s\" is not a valid name for a class implementing the UIThreadExecutor interface", uiThreadExecutorClassName), ex);
                }
            }

            //If it's still null, it was not provided properly in the configuration
            if(clazz == null){
                clazz = NoUIThreadExecutor.class;
            }

            storage.setUIThreadExecutorType(clazz);

            List<String> packageNames = config.getPackageNames();
            for(String name : packageNames){
                scanner.scanPackage(name, storage, config.getScannerExclusions());
            }

            initialized = true;
        }
    }

}
