package com.gempukku.startrek.server.game.deck;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class PlayerDeckComponent extends Component {
    private String owner;
    private Array<Integer> cards = new Array<>();

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Array<Integer> getCards() {
        return cards;
    }
}
