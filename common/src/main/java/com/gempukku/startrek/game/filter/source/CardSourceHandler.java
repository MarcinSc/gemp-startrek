package com.gempukku.startrek.game.filter.source;

import com.badlogic.gdx.utils.Array;

public interface CardSourceHandler {
    CardSource resolveSource(Array<String> parameters);

    void validate(Array<String> parameters);
}
