package com.gempukku.startrek.game.ability;

import com.artemis.BaseSystem;

public abstract class ClientCardAbilityHandlerSystem extends BaseSystem implements ClientCardAbilityHandler {
    private ClientCardAbilitySystem clientCardAbilitySystem;
    private String[] abilityTypes;

    public ClientCardAbilityHandlerSystem(String... abilityTypes) {
        this.abilityTypes = abilityTypes;
    }

    @Override
    protected void initialize() {
        for (String abilityType : abilityTypes) {
            clientCardAbilitySystem.registerClientCardAbilityHandler(abilityType, this);
        }
    }

    @Override
    protected void processSystem() {

    }
}
