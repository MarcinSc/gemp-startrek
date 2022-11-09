package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class CardInBrigComponent extends Component {
    private String brigOwner;

    public String getBrigOwner() {
        return brigOwner;
    }

    public void setBrigOwner(String brigOwner) {
        this.brigOwner = brigOwner;
    }
}
