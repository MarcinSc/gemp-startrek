package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

public class MissionIndexFilterHandler extends CardFilterSystem {
    private AmountResolverSystem amountResolverSystem;

    public MissionIndexFilterHandler() {
        super("missionIndex");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                int index = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(0));
                FaceUpCardInMissionComponent faceUpCard = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
                if (faceUpCard != null)
                    return faceUpCard.getMissionIndex() == index;
                FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                if (faceDownCard != null)
                    return faceDownCard.getMissionIndex() == index;
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        amountResolverSystem.validateAmount(parameters.get(0));
    }
}
