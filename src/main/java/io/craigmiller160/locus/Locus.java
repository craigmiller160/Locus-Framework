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
import io.craigmiller160.locus.scan.LocusScanner;
import io.craigmiller160.locus.scan.LocusScannerFactory;
import io.craigmiller160.locus.util.ConfigurationReader;
import io.craigmiller160.locus.util.ConfigurationReaderFactory;
import io.craigmiller160.locus.util.LocusConfiguration;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static io.craigmiller160.locus.util.LocusConstants.DEFAULT_CONFIG_FILE;

/**
 * The central class of the Locus Framework.
 * This class provides easy, static access to
 * manipulate system resources through its three
 * subclasses.
 *
 * This class is completely thread-safe.
 *
 * IMPORTANT: The Locus configuration file, by default, is
 * loaded using ClassLoader.getResourceAsStream(). This only
 * works on pure-Java implementations. Other Java-based
 * constructs, such as Android, may not work with this
 * approach. Initialization methods that accept InputStreams
 * instead of String paths should be used in these cases
 * to initialize the framework.
 *
 * Created by craig on 3/12/16.
 */
@ThreadSafe
public class Locus {

    private static final Logger logger = LoggerFactory.getLogger(Locus.class);
    private static final LocusStorage storage = LocusStorage.getInstance();
    private static final ConfigurationReader configReader = ConfigurationReaderFactory.newInstance().newConfigurationReader();
    private static final LocusScanner packageScanner = LocusScannerFactory.newPackageScanner();
    private static final LocusScanner classScanner = LocusScannerFactory.newClassScanner();

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

    /**
     * Handles all operations affecting registered models.
     */
    public static LocusModel model = new LocusModel();

    /**
     * Handles all operations affecting registered controllers.
     */
    public static LocusController controller = new LocusController();

    /**
     * Handles all operations affecting regsitered views.
     */
    public static LocusView view = new LocusView();

    /**
     * Handles all operations related to debugging this
     * framework.
     */
    public static LocusDebug debug = new LocusDebug();


    /**
     * Initialize the Locus framework with a configuration
     * file with the default name (locus.xml), at the root
     * of the classpath. This method will only
     * execute if it has not already been initialized.
     */
    public static void initialize(){
        initialize(false);
    }

    /**
     * Initialize the Locus frameworkwith a configuration
     * file with the default name (locus.xml), at the root
     * of the classpath. This method has the option to
     * force a re-initialization.
     *
     * @param force if it should be re-initialized if already initialized.
     */
    public static void initialize(boolean force){
        initialize(DEFAULT_CONFIG_FILE, force);
    }

    /**
     * Initialize Locus with a configuration file
     * specified by the provided String path. The path
     * should be a relative path from the root of the
     * classpath. This method will only execute if it
     * has not already been initialized.
     *
     * @param configFilePath the path to the configuration file.
     */
    public static void initialize(String configFilePath){
        initialize(configFilePath, false);
    }

    /**
     * Initialize Locus with a the configuration
     * provided in the InputStream. This method will only execute if it
     * has not already been initialized.
     *
     * @param configSource the configuration.
     */
    public static void initialize(InputStream configSource){
        initialize(configSource, false);
    }

    /**
     * Initialize Locus with a configuration file
     * specified by the provided String path. The path
     * should be a relative path from the root of the
     * classpath. This method has the option to
     * force a re-initialization.
     *
     * @param configFilePath the path to the configuration file.
     * @param force if it should be re-initialized if already initialized.
     */
    @SuppressWarnings("unchecked")
    public static void initialize(String configFilePath, boolean force){
        logger.trace("Locus configuration file provided: {}", configFilePath);
        InputStream iStream = null;
        try{
            iStream = Locus.class.getClassLoader().getResourceAsStream(configFilePath);
            if(iStream == null){
                throw new LocusException(String.format("No configuration file found as specified path: %s", configFilePath));
            }

            initialize(iStream, force);
        }
        finally{
            if(iStream != null){
                try{
                    iStream.close();
                }
                catch(IOException ex){
                    logger.error("Unable to close configuration reading input stream", ex);
                }
            }
        }
    }

    /**
     * Initialize Locus with the configuration
     * provided in the InputStream. This method has the option to
     * force a re-initialization.
     *
     * @param configSource the configuration
     * @param force if it should be re-initialized if already initialized.
     */
    public static void initialize(InputStream configSource, boolean force){
        //Using this lock here to ensure that the initialization process can't be called by multiple threads simultaneously.
        synchronized (initializeLock){
            if(initialized && !force){
                logger.trace("Attempt to initialize already-initialized Locus framework rejected. " +
                        "To force reinitialization, using initialize(...) method with boolean force argument set to true.");
                return;
            }
            else if(initialized && force){
                logger.trace("Forcing re-initialization of the Locus framework");
            }

            logger.debug("Initializing Locus Framework");

            //Clear any pre-existing values
            storage.clear();

            //Read the configuration file
            LocusConfiguration config = configReader.readConfiguration(configSource);

            //Identify the UIThreadExecutor, if a value has been provided
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

            //If the UIThreadExecutor class is null, set it to the default
            if(clazz == null){
                clazz = NoUIThreadExecutor.class;
            }

            //Set the UIThreadExecutor in the LocusStorage
            logger.trace("Setting UIThreadExecutor type: {}", clazz.getName());
            storage.setUIThreadExecutorType(clazz);

            //The locus.xsd schema ensures that package names and class names cannot be used together

            //Scan the provided packages and organize them in the storage
            List<String> packageNames = config.getPackageNames();
            for(String name : packageNames){
                packageScanner.scan(name, storage, config.getScannerExclusions());
            }

            //Scan the provided classes and organize them in the storage
            List<String> classNames = config.getClassNames();
            for(String name : classNames){
                classScanner.scan(name, storage, config.getScannerExclusions());
            }

            //Set the initialized flag to true
            initialized = true;

            logger.info("Locus Framework initialized");
        }
    }

}
