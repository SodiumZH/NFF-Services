package net.sodiumstudio.nautils.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

/**
 * Labels that the default method is not recommended to override.
 * Continue overriding these methods may cause unexpected errors.
 * <p>表明该默认方法不建议重载。
 * 仍然重载这些方法可能导致意外的错误。
 */
@Target(METHOD)
public @interface DontOverride {

}
