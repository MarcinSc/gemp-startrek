package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

public class UnstoppedFilterHandler extends CardFilterSystem {
    public UnstoppedFilterHandler() {
        super("unstopped");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                FaceUpCardInMissionComponent faceUpCard = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
                if (faceUpCard != null)
                    return !faceUpCard.isStopped();
                FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                if (faceDownCard != null)
                    return !faceDownCard.isStopped();
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
