package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;

public class NotAboardShipFilterHandler extends CardFilterSystem {
    public NotAboardShipFilterHandler() {
        super("notAboardShip");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                if (faceDownCard != null)
                    return faceDownCard.getOnShipId() == null;
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
