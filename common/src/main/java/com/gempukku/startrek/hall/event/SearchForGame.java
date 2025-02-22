package com.gempukku.startrek.hall.event;

import com.gempukku.libgdx.network.EventFromClient;
import com.gempukku.libgdx.network.SendToServer;

@SendToServer
public class SearchForGame extends EventFromClient {
    private String starterDeckId;
    private String playerDeckId;

    public SearchForGame() {
    }

    public SearchForGame(String starterDeckId) {
        this.starterDeckId = starterDeckId;
    }

    public void setStarterDeckId(String starterDeckId) {
        this.starterDeckId = starterDeckId;
    }

    public String getStarterDeckId() {
        return starterDeckId;
    }

    public void setPlayerDeckId(String playerDeckId) {
        this.playerDeckId = playerDeckId;
    }

    public String getPlayerDeckId() {
        return playerDeckId;
    }
}
