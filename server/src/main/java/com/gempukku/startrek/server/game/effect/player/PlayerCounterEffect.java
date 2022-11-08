package com.gempukku.startrek.server.game.effect.player;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class PlayerCounterEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private EventSystem eventSystem;

    public PlayerCounterEffect() {
        super("setPlayerCounters");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        int amount = amountResolverSystem.resolveAmount(gameEffectEntity, memory,
                gameEffect.getDataString("amount"));

        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, memory,
                gameEffect.getDataString("player"));
        PlayerPublicStatsComponent playerCounter = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        playerCounter.setCounterCount(amount);

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }
}
