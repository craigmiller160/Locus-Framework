package io.craigmiller160.locus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special annotation used to mark that a class
 * is supposed to be a Model in this framework.
 *
 * Created by craig on 3/12/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Model {
}
