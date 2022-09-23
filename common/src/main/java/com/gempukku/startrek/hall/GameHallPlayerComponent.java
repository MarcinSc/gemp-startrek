package com.gempukku.startrek.hall;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class GameHallPlayerComponent extends Component implements OwnedComponent {
    private String owner;
    private String portrait;
    private boolean waitingForGame;
    private String chosenStarterDeck;
    private int maximumDeckCount;

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public boolean isWaitingForGame() {
        return waitingForGame;
    }

    public void setWaitingForGame(boolean waitingForGame) {
        this.waitingForGame = waitingForGame;
    }

    public String getChosenStarterDeck() {
        return chosenStarterDeck;
    }

    public void setChosenStarterDeck(String chosenStarterDeck) {
        this.chosenStarterDeck = chosenStarterDeck;
    }

    public int getMaximumDeckCount() {
        return maximumDeckCount;
    }

    public void setMaximumDeckCount(int maximumDeckCount) {
        this.maximumDeckCount = maximumDeckCount;
    }
}
