package com.gempukku.libgdx.lib.graph.artemis.selection;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Predicate;

import java.util.Set;

public interface SelectionDefinition {
    boolean isSelectionTriggered();

    boolean canDeselect(Set<Entity> selectedEntities, Entity selected);

    boolean canSelect(Set<Entity> selectedEntities, Entity newSelected);

    String getMask();

    Predicate<Entity> getEntityPredicate();
}
