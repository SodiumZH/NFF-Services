package net.sodiumstudio.befriendmobs.util.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

/**
 * Labels that the default method is not recommended to override.
 * Continue overriding these methods may cause unexpected errors.
 */
@Target(METHOD)
public @interface DontOverride {

}
