package com.gempukku.startrek.server.game.effect.player;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ValidateUtil;
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
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory,
                gameEffect.getDataString("amount"));

        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory,
                gameEffect.getDataString("player"));
        PlayerPublicStatsComponent playerCounter = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        playerCounter.setCounterCount(amount);

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"amount", "player"},
                new String[]{});
        amountResolverSystem.validateAmount(effect.getString("amount"));
        playerResolverSystem.validatePlayer(effect.getString("player"));
    }
}
