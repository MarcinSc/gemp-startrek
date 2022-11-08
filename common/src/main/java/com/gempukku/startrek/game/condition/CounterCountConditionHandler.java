package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class CounterCountConditionHandler extends ConditionSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public CounterCountConditionHandler() {
        super("counterCount");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory, parameters.get(0));
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        return publicStats.getCounterCount() == amount;
    }
}
