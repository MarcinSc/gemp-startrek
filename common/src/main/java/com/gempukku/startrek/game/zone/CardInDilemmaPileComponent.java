package com.gempukku.startrek.game.zone;

import com.artemis.Component;

public class CardInDilemmaPileComponent extends Component {
    boolean faceUp;

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }
}
