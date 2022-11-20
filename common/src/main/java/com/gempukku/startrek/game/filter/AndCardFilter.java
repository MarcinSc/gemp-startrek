package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public class AndCardFilter implements CardFilter {
    private Array<CardFilter> filters;

    public AndCardFilter(CardFilter... cardFilters) {
        filters = new Array<>();
        for (CardFilter cardFilter : cardFilters) {
            filters.add(cardFilter);
        }
    }

    public AndCardFilter(Array<CardFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
        for (CardFilter filter : filters) {
            if (!filter.accepts(sourceEntity, memory, cardEntity))
                return false;
        }
        return true;
    }
}
