package net.sodiumzh.nfu.entity.anger;

import java.util.Optional;
import java.util.UUID;

/**
 * A result for setting anger actions.
 * @param target The uuid of the target.
 * @param isHandled True if it really set an anger and made some impact. False if nothing happened.
 * @param reason The reason of this anger-setting action. Empty if it's not with a reason.
 */
public record MobSetAngerResult(UUID target, boolean isHandled, Optional<MobAngerReason> reason) {
}
