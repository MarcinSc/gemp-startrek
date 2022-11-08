package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class OrCardFilter implements CardFilter {
    private Array<CardFilter> filters;

    public OrCardFilter(Array<CardFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
        for (CardFilter filter : filters) {
            if (filter.accepts(sourceEntity, memory, cardEntity))
                return true;
        }
        return false;
    }
}
