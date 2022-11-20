package com.gempukku.startrek.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateWithOthers;

@ReplicateWithOthers
public class FacedDilemmaComponent extends Component {
    private Array<String> facingPersonnel = new Array<>();

    public Array<String> getFacingPersonnel() {
        return facingPersonnel;
    }
}
