package com.gempukku.startrek.server.game;

import com.gempukku.startrek.hall.StarTrekDeck;

public class PlayerGameInfo {
    private String username;
    private String displayName;
    private String avatar;
    private StarTrekDeck deck;

    public PlayerGameInfo(String username, String displayName, String avatar, StarTrekDeck deck) {
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.deck = deck;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public StarTrekDeck getDeck() {
        return deck;
    }
}
