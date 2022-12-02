package com.gempukku.startrek.game.filter.source;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public abstract class CardSourceSystem extends BaseSystem implements CardSourceHandler {
    private CardFilteringSystem cardFilteringSystem;

    private String[] sourceTypes;

    public CardSourceSystem(String... sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

    @Override
    protected void initialize() {
        for (String sourceType : sourceTypes) {
            cardFilteringSystem.registerSourceHandler(sourceType, this);
        }
    }

    protected boolean isAccepted(Entity sourceEntity, Memory memory, Entity entity, CardFilter... filters) {
        for (CardFilter filter : filters) {
            if (!filter.accepts(sourceEntity, memory, entity))
                return false;
        }
        return true;
    }

    @Override
    protected void processSystem() {

    }
}
