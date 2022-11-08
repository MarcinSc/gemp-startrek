package com.gempukku.startrek.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class PlayerDiscardPileComponent extends Component {
    private Array<Integer> cards = new Array<>();

    public Array<Integer> getCards() {
        return cards;
    }
}
