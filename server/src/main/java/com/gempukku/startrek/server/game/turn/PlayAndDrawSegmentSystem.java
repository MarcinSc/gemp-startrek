package com.gempukku.startrek.server.game.turn;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.server.game.ExecuteStackedAction;

public class PlayAndDrawSegmentSystem extends BaseSystem {
    @EventListener
    public void executePlayAndDrawAction(ExecuteStackedAction action, Entity entity) {
        PlayAndDrawSegmentComponent playerAndDrawSegment = entity.getComponent(PlayAndDrawSegmentComponent.class);
        if (playerAndDrawSegment != null) {

        }
    }

    @Override
    protected void processSystem() {

    }
}
