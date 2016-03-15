package io.craigmiller160.locus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special annotation used to mark that this
 * class is a Controller in this framework.
 *
 * Unlike the Model or View annotations, Controller
 * requires a single parameter, a String representing
 * its name. This allows it to be called from the convenience
 * methods in the Locus class. It also has an optional parameter
 * that declares it to be a singleton. This means that only
 * one instance of this controller will ever be created.
 * By default, this is false, and a new instance will be
 * created every time a controller is retrieved from the
 * Locus class.
 *
 * Created by craig on 3/12/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {

    public String name();

    public boolean singleton() default false;

}
