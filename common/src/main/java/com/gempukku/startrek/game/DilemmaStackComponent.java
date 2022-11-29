package com.gempukku.startrek.game;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class DilemmaStackComponent extends Component {
    private int cardCount;

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }
}
