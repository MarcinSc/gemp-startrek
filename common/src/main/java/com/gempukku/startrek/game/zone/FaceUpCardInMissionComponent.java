package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class FaceUpCardInMissionComponent extends Component {
    private int rangeUsed;
    private int faceDownCardsCount;

    public int getRangeUsed() {
        return rangeUsed;
    }

    public void setRangeUsed(int rangeUsed) {
        this.rangeUsed = rangeUsed;
    }

    public int getFaceDownCardsCount() {
        return faceDownCardsCount;
    }

    public void setFaceDownCardsCount(int faceDownCardsCount) {
        this.faceDownCardsCount = faceDownCardsCount;
    }
}
