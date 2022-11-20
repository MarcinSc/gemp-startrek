package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class DeckCountAmountHandler extends AmountSystem {
    private PlayerResolverSystem playerResolverSystem;

    public DeckCountAmountHandler() {
        super("deckCount");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory, parameters.get(0));
        return playerEntity.getComponent(PlayerPublicStatsComponent.class).getDeckCount();
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        playerResolverSystem.validate(parameters.get(0));
    }
}
