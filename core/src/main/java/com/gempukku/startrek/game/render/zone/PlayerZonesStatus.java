package com.gempukku.startrek.game.render.zone;

public class PlayerZonesStatus {
    private boolean handDrity;
    private boolean coreDirty;
    private boolean brigDirty;

    public boolean isHandDrity() {
        return handDrity;
    }

    public void setHandDrity() {
        this.handDrity = true;
    }

    public boolean isCoreDirty() {
        return coreDirty;
    }

    public void setCoreDirty() {
        this.coreDirty = true;
    }

    public boolean isBrigDirty() {
        return brigDirty;
    }

    public void setBrigDirty() {
        this.brigDirty = true;
    }

    public void cleanZones() {
        handDrity = false;
        coreDirty = false;
        brigDirty = false;
    }
}
