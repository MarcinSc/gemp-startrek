package com.gempukku.startrek.game.render.zone;

public class CommonZonesStatus {
    private boolean stackDirty;

    public boolean isStackDirty() {
        return stackDirty;
    }

    public void setStackDirty() {
        this.stackDirty = true;
    }

    public void cleanup() {
        stackDirty = false;
    }
}
