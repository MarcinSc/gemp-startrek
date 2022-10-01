package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.PlayerCounterComponent;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class PlayerCounterEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private EventSystem eventSystem;

    public PlayerCounterEffect() {
        super("setPlayerCounters");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        int amount = amountResolverSystem.resolveAmount(gameEffect.getData().get("amount"));

        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffect.getData().getString("player"));
        PlayerCounterComponent playerCounter = playerEntity.getComponent(PlayerCounterComponent.class);
        playerCounter.setCounterCount(amount);

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        removeEffectFromStack(gameEffectEntity);
    }
}
