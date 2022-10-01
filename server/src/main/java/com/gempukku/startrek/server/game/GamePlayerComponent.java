package com.gempukku.startrek.server.game;

import com.artemis.Component;

public class GamePlayerComponent extends Component {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
