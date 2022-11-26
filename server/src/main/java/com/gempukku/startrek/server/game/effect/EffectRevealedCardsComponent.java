package com.gempukku.startrek.server.game.effect;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class EffectRevealedCardsComponent extends Component {
    private Array<String> revealedCards = new Array<>();

    public Array<String> getRevealedCards() {
        return revealedCards;
    }
}
