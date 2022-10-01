package com.gempukku.startrek.server.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class GamePlayerComponent extends Component {
    private String name;
    private Array<String> cards = new Array<>();

    public Array<String> getCards() {
        return cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
