package com.gempukku.startrek.server.game.effect.player;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class PlayerCounterEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private EventSystem eventSystem;

    public PlayerCounterEffect() {
        super("setPlayerCounters");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        int amount = amountResolverSystem.resolveAmount(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("amount"));

        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        PlayerPublicStatsComponent playerCounter = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        playerCounter.setCounterCount(amount);

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }
}
