package com.gempukku.startrek.hall;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class GameHallPlayerComponent extends Component implements OwnedComponent {
    private String owner;
    private String avatar;
    private boolean waitingForGame;
    private String chosenStarterDeck;
    private String chosenPlayerDeck;
    private int maximumDeckCount;

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getChosenPlayerDeck() {
        return chosenPlayerDeck;
    }

    public void setChosenPlayerDeck(String chosenPlayerDeck) {
        this.chosenPlayerDeck = chosenPlayerDeck;
    }

    public int getMaximumDeckCount() {
        return maximumDeckCount;
    }

    public void setMaximumDeckCount(int maximumDeckCount) {
        this.maximumDeckCount = maximumDeckCount;
    }
}
