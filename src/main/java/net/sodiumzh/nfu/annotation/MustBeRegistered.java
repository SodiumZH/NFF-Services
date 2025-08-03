package net.sodiumzh.nfu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Label this class is a dedicated registry entry. Any instances of this class must be registered and accessed
 * through registry, and it should never be constructed during runtime without registration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MustBeRegistered {
}
