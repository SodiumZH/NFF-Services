package net.sodiumstudio.nautils.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

/**
 * A label for methods that are not recommended to manually call.
 * <p>表明该方法不建议手动调用。
 */
@Target(METHOD)
public @interface DontCallManually {

}
