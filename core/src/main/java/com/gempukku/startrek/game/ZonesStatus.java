package com.gempukku.startrek.game;

public class ZonesStatus {
    private boolean handDrity;
    private boolean coreDirty;
    private boolean missionsDirty;

    public boolean isHandDrity() {
        return handDrity;
    }

    public void setHandDrity(boolean handDrity) {
        this.handDrity = handDrity;
    }

    public boolean isCoreDirty() {
        return coreDirty;
    }

    public void setCoreDirty(boolean coreDirty) {
        this.coreDirty = coreDirty;
    }

    public boolean isMissionsDirty() {
        return missionsDirty;
    }

    public void setMissionsDirty(boolean missionsDirty) {
        this.missionsDirty = missionsDirty;
    }

    public void cleanZones() {
        handDrity = false;
        coreDirty = false;
        missionsDirty = false;
    }
}
