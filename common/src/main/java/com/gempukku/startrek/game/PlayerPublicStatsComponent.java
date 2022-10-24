package com.gempukku.startrek.game;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class PlayerPublicStatsComponent extends Component {
    private int counterCount;
    private int pointCount;
    private int handCount;
    private int deckCount;
    private int dilemmaCount;

    public int getCounterCount() {
        return counterCount;
    }

    public void setCounterCount(int counterCount) {
        this.counterCount = counterCount;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public int getHandCount() {
        return handCount;
    }

    public void setHandCount(int handCount) {
        this.handCount = handCount;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    public int getDilemmaCount() {
        return dilemmaCount;
    }

    public void setDilemmaCount(int dilemmaCount) {
        this.dilemmaCount = dilemmaCount;
    }
}
