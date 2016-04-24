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

package io.craigmiller160.locus.util;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A class containing global constant values for
 * the Locus framework.
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable
 * state and is therefore completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
@ThreadSafe
public class LocusConstants {

    /**
     * The code indicating a getter method.
     */
    public static final int GETTER = 101;

    /**
     * The code indicating a setter method.
     */
    public static final int SETTER = 102;

    /**
     * The code indicating an adder method.
     */
    public static final int ADDER = 103;

    /**
     * The code indicating a remover method.
     */
    public static final int REMOVER = 104;

    /**
     * The default path to the Locus configuration file.
     */
    public static final String DEFAULT_CONFIG_FILE = "locus.xml";

    /**
     * The name of a model type.
     */
    public static final String MODEL_TYPE = "Model";

    /**
     * The name of a view type.
     */
    public static final String VIEW_TYPE = "View";

    /**
     * The name of a controller type.
     */
    public static final String CONTROLLER_TYPE = "Controller";

}
