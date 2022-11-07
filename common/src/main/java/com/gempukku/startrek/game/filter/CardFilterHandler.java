package com.gempukku.startrek.game.filter;

import com.badlogic.gdx.utils.Array;

public interface CardFilterHandler {
    CardFilter resolveFilter(String filterType, Array<String> parameters);
}
