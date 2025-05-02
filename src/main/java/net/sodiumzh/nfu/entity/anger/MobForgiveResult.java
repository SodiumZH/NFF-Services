package net.sodiumzh.nfu.entity.anger;

import java.util.UUID;

/**
 * A result for forgiving actions.
 * @param target The uuid of the target.
 * @param isHandled True if it really removed an anger target False if nothing happened.
 * @param isManual True if it's caused by manually call {@link CMobAngerHandler#forgive}. False if it's because timer
 *                 expired.
 */
public record MobForgiveResult(UUID target, boolean isHandled, boolean isManual) {
}
