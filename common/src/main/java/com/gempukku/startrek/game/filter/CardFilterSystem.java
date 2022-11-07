package com.gempukku.startrek.game.filter;

import com.artemis.BaseSystem;

public abstract class CardFilterSystem extends BaseSystem implements CardFilterHandler {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private String[] filterTypes;

    public CardFilterSystem(String... filterTypes) {
        this.filterTypes = filterTypes;
    }

    @Override
    protected void initialize() {
        for (String filterType : filterTypes) {
            cardFilterResolverSystem.registerFilterHandler(filterType, this);
        }
    }

    @Override
    protected void processSystem() {

    }
}
