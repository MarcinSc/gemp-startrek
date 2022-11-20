package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

public class OnMissionFilterHandler extends CardFilterSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public OnMissionFilterHandler() {
        super("onMission");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, parameters.get(0));
                int missionIndex = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
                FaceUpCardInMissionComponent faceUpCard = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
                if (faceUpCard != null)
                    return faceUpCard.getMissionOwner().equals(username) && faceUpCard.getMissionIndex() == missionIndex;
                FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                if (faceDownCard != null)
                    return faceDownCard.getMissionOwner().equals(username) && faceDownCard.getMissionIndex() == missionIndex;

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        playerResolverSystem.validatePlayer(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
