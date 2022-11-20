package com.gempukku.startrek.game.decision;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectSet;

public interface SelectionCallback {
    void selectionChanged(ObjectSet<Entity> selected);
}
