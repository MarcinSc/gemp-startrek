package com.gempukku.startrek.game.ability;

public class OrderInterruptAbility implements CardAbility {
    private String condition;

    public OrderInterruptAbility(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
