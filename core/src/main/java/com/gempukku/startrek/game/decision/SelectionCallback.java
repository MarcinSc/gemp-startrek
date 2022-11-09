package com.gempukku.startrek.game.decision;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

public interface SelectionCallback {
    void selectionChanged(Array<Entity> selected);
}
