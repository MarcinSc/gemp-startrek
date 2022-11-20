package com.gempukku.startrek.game.ability;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.JsonValue;

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
    public void validateAbility(JsonValue cardAbility) {
        // TODO - remove the method - each Handler should implement it
    }

    @Override
    protected void processSystem() {

    }
}
