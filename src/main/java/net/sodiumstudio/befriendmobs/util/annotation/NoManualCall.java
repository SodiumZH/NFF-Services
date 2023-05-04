package net.sodiumstudio.befriendmobs.util.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

/**
 * A label for methods that are not recommended to manually call.
 */
@Target(METHOD)
public @interface NoManualCall {

}
