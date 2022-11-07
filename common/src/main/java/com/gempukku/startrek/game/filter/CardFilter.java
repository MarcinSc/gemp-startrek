package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public interface CardFilter {
    boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity);
}
