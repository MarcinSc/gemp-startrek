package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public class OrCardFilter implements CardFilter {
    private Array<CardFilter> filters;

    public OrCardFilter(CardFilter... cardFilters) {
        filters = new Array<>();
        for (CardFilter cardFilter : cardFilters) {
            filters.add(cardFilter);
        }
    }

    public OrCardFilter(Array<CardFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
        for (CardFilter filter : filters) {
            if (filter.accepts(sourceEntity, memory, cardEntity))
                return true;
        }
        return false;
    }
}
