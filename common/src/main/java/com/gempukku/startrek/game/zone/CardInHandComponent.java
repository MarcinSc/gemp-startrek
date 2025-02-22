package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class CardInHandComponent extends Component implements OwnedComponent {
    private String owner;

    @Override
    public boolean isOwnedBy(String username) {
        return owner.equals(username);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
