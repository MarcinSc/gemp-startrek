package com.gempukku.startrek.game.ability;

public class ClientInterruptAbility implements CardAbility {
    private String condition;

    public ClientInterruptAbility(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
