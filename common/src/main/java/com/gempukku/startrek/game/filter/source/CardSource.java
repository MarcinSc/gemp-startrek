package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.function.Consumer;

public interface CardSource {
    void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters);

    Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters);

    boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters);

    int getCount(Entity sourceEntity, Memory memory, CardFilter... filters);
}
