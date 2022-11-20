package com.gempukku.startrek.game.filter;

import com.badlogic.gdx.utils.Array;

public interface CardFilterHandler {
    CardFilter resolveFilter(Array<String> parameters);

    void validate(Array<String> parameters);
}
