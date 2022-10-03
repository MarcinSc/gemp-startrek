package com.gempukku.startrek.server.game.deck;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class PlayerDeckComponent extends Component {
    private Array<Integer> cards = new Array<>();

    public Array<Integer> getCards() {
        return cards;
    }
}
