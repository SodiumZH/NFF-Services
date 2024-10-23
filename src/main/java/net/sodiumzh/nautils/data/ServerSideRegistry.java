package net.sodiumzh.nautils.data;

import net.sodiumzh.nautils.registries.NaUtilsRegistry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Labels class of which {@link NaUtilsRegistry}s should initialize (generate) values on server start phase instead of
 * common setup phase. This indicates that the registry elements' initialization requires server instance access.
 * <p>This also means this registry will not be initialized on client. On client, you can still call this registry,
 * but with some risks of issues.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerSideRegistry {
}
