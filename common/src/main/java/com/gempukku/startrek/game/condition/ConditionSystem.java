package com.gempukku.startrek.game.condition;

import com.artemis.BaseSystem;

public abstract class ConditionSystem extends BaseSystem implements ConditionHandler {
    private ConditionResolverSystem conditionResolverSystem;

    private final String[] conditionTypes;

    public ConditionSystem(String... conditionTypes) {
        this.conditionTypes = conditionTypes;
    }

    @Override
    protected void initialize() {
        for (String conditionType : conditionTypes) {
            conditionResolverSystem.registerConditionHandler(conditionType, this);
        }
    }

    @Override
    protected void processSystem() {

    }
}
