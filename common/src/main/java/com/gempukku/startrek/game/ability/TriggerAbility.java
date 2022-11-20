package com.gempukku.startrek.game.ability;

public class TriggerAbility implements CardAbility {
    private String triggerType;
    private boolean optional;
    private String condition;

    public TriggerAbility(String triggerType, boolean optional, String condition) {
        this.triggerType = triggerType;
        this.optional = optional;
        this.condition = condition;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getCondition() {
        return condition;
    }
}
