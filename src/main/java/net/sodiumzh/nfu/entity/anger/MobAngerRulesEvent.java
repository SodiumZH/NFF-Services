package net.sodiumzh.nfu.entity.anger;

import net.minecraftforge.eventbus.api.Event;

/**
 * Posted on {@link MobAngerRules} is constructed, allowing modification through event.
 */
public class MobAngerRulesEvent extends Event {

    private final MobAngerRules rules;
    public MobAngerRulesEvent(MobAngerRules rules) {
        this.rules = rules;
    }

    public MobAngerRules getRules() {
        return rules;
    }
}
