package com.gempukku.startrek.game.ability;

public class EventAbility implements CardAbility {
    private String condition;

    public EventAbility(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
