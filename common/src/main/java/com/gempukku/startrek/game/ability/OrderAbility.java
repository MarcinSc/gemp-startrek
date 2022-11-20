package com.gempukku.startrek.game.ability;

public class OrderAbility implements CardAbility {
    private String condition;

    public OrderAbility(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
