package com.gempukku.startrek.game.ability;

import com.artemis.BaseSystem;

public abstract class CardAbilityHandlerSystem extends BaseSystem implements CardAbilityHandler {
    private CardAbilitySystem cardAbilitySystem;
    private String[] abilityTypes;

    public CardAbilityHandlerSystem(String... abilityTypes) {
        this.abilityTypes = abilityTypes;
    }

    @Override
    protected void initialize() {
        for (String abilityType : abilityTypes) {
            cardAbilitySystem.registerCardAbilityHandler(abilityType, this);
        }
    }

    @Override
    protected void processSystem() {

    }
}
