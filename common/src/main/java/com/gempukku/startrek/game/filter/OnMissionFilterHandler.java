package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class OnMissionFilterHandler extends CardFilterSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public OnMissionFilterHandler() {
        super("inMission");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, parameters.get(0));
                int missionIndex = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                if (cardInMission != null)
                    return cardInMission.getMissionOwner().equals(username) && cardInMission.getMissionIndex() == missionIndex;

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
