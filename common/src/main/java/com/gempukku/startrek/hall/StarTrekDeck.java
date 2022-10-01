package com.gempukku.startrek.hall;

import com.badlogic.gdx.utils.Array;

public class StarTrekDeck {
    private String deckName;
    private String deckId;
    private Array<String> missions = new Array<>();
    private Array<String> dillemas = new Array<>();
    private Array<String> drawDeck = new Array<>();

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public Array<String> getMissions() {
        return missions;
    }

    public Array<String> getDillemas() {
        return dillemas;
    }

    public Array<String> getDrawDeck() {
        return drawDeck;
    }
}
