package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class DeckCountConditionHandler extends ConditionSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public DeckCountConditionHandler() {
        super("deckCount");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory, parameters.get(0));
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        return publicStats.getDeckCount() == amount;
    }
}
