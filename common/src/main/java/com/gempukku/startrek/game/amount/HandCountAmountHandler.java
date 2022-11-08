package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class HandCountAmountHandler extends AmountSystem {
    private PlayerResolverSystem playerResolverSystem;

    public HandCountAmountHandler() {
        super("handCount");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity entity = playerResolverSystem.resolvePlayer(sourceEntity, memory, parameters.get(0));
        PlayerPublicStatsComponent stats = entity.getComponent(PlayerPublicStatsComponent.class);
        return stats.getHandCount();
    }
}
