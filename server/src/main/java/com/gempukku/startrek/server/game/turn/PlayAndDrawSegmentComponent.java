package com.gempukku.startrek.server.game.turn;

import com.artemis.Component;

public class PlayAndDrawSegmentComponent extends Component {
    private int counterCount;

    public int getCounterCount() {
        return counterCount;
    }

    public void setCounterCount(int counterCount) {
        this.counterCount = counterCount;
    }
}
