package com.gempukku.startrek.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class PlanningDilemmaStackComponent extends Component implements OwnedComponent {
    private Array<String> cardDefIds = new Array<>();
    private String owner;

    @Override
    public boolean isOwnedBy(String username) {
        return username.equals(owner);
    }


}
