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

package io.craigmiller160.locus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>A special annotation used to mark that this
 * class is a Controller in this framework.</p>
 *
 * <p>Unlike the LModel or LView annotations, LController
 * requires a single parameter, a String representing
 * its name. This allows it to be called from the convenience
 * methods in the Locus class. It also has an optional parameter
 * that declares it to be a singleton. This means that only
 * one instance of this controller will ever be created.
 * By default, this is false, and a new instance will be
 * created every time a controller is retrieved from the
 * Locus class.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LController {

    /**
     * The name of the controller. This attribute is required,
     * because the name is how the controller will ultimately
     * be retrieved from the storage.
     *
     * @return the name of the controller.
     */
    String name();

    /**
     * If the controller is a singleton or not. Being a
     * singleton means that only one instance of the
     * controller will ever be created, and every time
     * it is retrieved that one instance will be shared.
     *
     * @return if the controller is a singleton.
     */
    boolean singleton() default false;

}
