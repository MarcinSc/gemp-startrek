package com.gempukku.startrek.server.game.deck;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class PlayerDecklistComponent extends Component {
    private Array<String> cards = new Array<>();

    public Array<String> getCards() {
        return cards;
    }
}
