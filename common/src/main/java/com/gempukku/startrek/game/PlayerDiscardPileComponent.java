package com.gempukku.startrek.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class PlayerDiscardPileComponent extends Component {
    private Array<String> cards = new Array<>();

    public Array<String> getCards() {
        return cards;
    }
}
