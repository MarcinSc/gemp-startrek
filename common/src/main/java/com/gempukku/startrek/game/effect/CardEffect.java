package com.gempukku.startrek.game.effect;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public interface CardEffect {
    boolean isPlayable(Entity cardSource, Memory memory);
}
