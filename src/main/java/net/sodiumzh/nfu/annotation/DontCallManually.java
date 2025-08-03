package net.sodiumzh.nfu.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * A label for methods that are not recommended to manually call.
 * <p>表明该方法不建议手动调用。
 */
@Target(METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface DontCallManually {

}
