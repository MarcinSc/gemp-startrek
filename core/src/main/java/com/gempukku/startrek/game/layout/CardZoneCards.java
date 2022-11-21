package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;

public interface CardZoneCards {
    int getLineCount();

    Array<Entity> getTopLevelCards(int lineIndex);

    Array<Entity> getAttachedCards(Entity entity);

    int getAttachedCardCount(Entity entity);

    TextStyle getCardTextStyle(int lineIndex, int cardIndex);
}
