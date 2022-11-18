package com.gempukku.startrek.game.render.zone;

public class PlayerZonesStatus {
    private boolean handDrity;
    private boolean coreDirty;
    private boolean brigDirty;
    private boolean deckDirty;
    private boolean dilemmaPileDirty;

    public boolean isHandDrity() {
        return handDrity;
    }

    public void setHandDrity() {
        handDrity = true;
    }

    public boolean isCoreDirty() {
        return coreDirty;
    }

    public void setCoreDirty() {
        coreDirty = true;
    }

    public boolean isBrigDirty() {
        return brigDirty;
    }

    public void setBrigDirty() {
        brigDirty = true;
    }

    public boolean isDeckDirty() {
        return deckDirty;
    }

    public void setDeckDirty() {
        this.deckDirty = true;
    }

    public boolean isDilemmaPileDirty() {
        return dilemmaPileDirty;
    }

    public void setDilemmaPileDirty() {
        dilemmaPileDirty = true;
    }

    public void cleanZones() {
        handDrity = false;
        coreDirty = false;
        brigDirty = false;
        deckDirty = false;
        dilemmaPileDirty = false;
    }
}
