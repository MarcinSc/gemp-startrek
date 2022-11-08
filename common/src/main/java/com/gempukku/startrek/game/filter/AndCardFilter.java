package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AndCardFilter implements CardFilter {
    private Array<CardFilter> filters;

    public AndCardFilter(Array<CardFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
        for (CardFilter filter : filters) {
            if (!filter.accepts(sourceEntity, memory, cardEntity))
                return false;
        }
        return true;
    }
}
