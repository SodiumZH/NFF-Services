package net.sodiumzh.nfu.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Labels that the default method is not recommended to override.
 * Continue overriding these methods may cause unexpected errors.
 */
@Target(METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface DontOverride {

// TODO Implement as a compile-time check which throws error if the user is overriding the method with this annotation without adding {@link @SuppressWarnings("override")}

}

