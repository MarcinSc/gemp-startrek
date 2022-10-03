package com.gempukku.startrek.server.game.stack;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;

public class ExecuteStackedAction implements EntityEvent {
    private boolean finishedProcessing;

    public boolean isFinishedProcessing() {
        return finishedProcessing;
    }

    public void setFinishedProcessing(boolean finishedProcessing) {
        this.finishedProcessing = finishedProcessing;
    }
}
