package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public interface CardFilter {
    boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity);
}
