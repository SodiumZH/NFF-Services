package net.sodiumzh.nfu.annotation;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * A label for methods that are not recommended to manually call.
 * <p>表明该方法不建议手动调用。
 */
@Target(METHOD)
public @interface DontCallManually {

}
