package com.gempukku.libgdx.lib.graph.artemis.selection;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.collision.Ray;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;

import java.util.HashSet;
import java.util.Set;

public class SelectionSystem extends EntitySystem {
    private EventSystem eventSystem;
    private ShapePickingSystem shapePickingSystem;
    private CameraSystem cameraSystem;

    private boolean selecting;
    private SelectionDefinition selectionDefinition;

    private Set<Entity> selectedEntities = new HashSet<>();

    public void startSelection(SelectionDefinition selectionDefinition) {
        this.selecting = true;
        this.selectionDefinition = selectionDefinition;
    }

    public void updateSelection(SelectionDefinition selectionDefinition) {
        if (!selecting)
            throw new IllegalStateException("System not currently selecting");
        this.selectionDefinition = selectionDefinition;
    }

    public void finishSelection() {
        this.selecting = false;
        this.selectionDefinition = null;

        this.selectedEntities.clear();
    }

    public Iterable<Entity> getSelectedEntities() {
        return selectedEntities;
    }

    @Override
    protected void processSystem() {
        if (selecting && selectionDefinition.isSelectionTriggered()) {
            Ray pickRay = cameraSystem.getCamera().getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Entity pickedEntity = shapePickingSystem.pickEntity(pickRay,
                    selectionDefinition.getMask(), selectionDefinition.getEntityPredicate());
            if (pickedEntity != null) {
                boolean fireSelectionChaned = false;
                if (selectedEntities.contains(pickedEntity)) {
                    if (selectionDefinition.canDeselect(selectedEntities, pickedEntity)) {
                        selectedEntities.remove(pickedEntity);
                        fireSelectionChaned = true;
                    }
                } else {
                    if (selectionDefinition.canSelect(selectedEntities, pickedEntity)) {
                        selectedEntities.add(pickedEntity);
                        fireSelectionChaned = true;
                    }
                }
                if (fireSelectionChaned)
                    eventSystem.fireEvent(new SelectionChanged(), null);
            }
        }
    }
}
