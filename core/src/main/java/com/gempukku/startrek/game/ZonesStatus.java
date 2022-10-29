package com.gempukku.startrek.game;

public class ZonesStatus {
    private boolean handDrity;
    private boolean missionsDirty;

    public boolean isHandDrity() {
        return handDrity;
    }

    public void setHandDrity(boolean handDrity) {
        this.handDrity = handDrity;
    }

    public boolean isMissionsDirty() {
        return missionsDirty;
    }

    public void setMissionsDirty(boolean missionsDirty) {
        this.missionsDirty = missionsDirty;
    }

    public void cleanZones() {
        handDrity = false;
        missionsDirty = false;
    }
}
