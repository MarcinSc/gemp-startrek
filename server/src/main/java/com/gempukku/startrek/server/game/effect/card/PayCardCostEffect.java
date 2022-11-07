package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class PayCardCostEffect extends OneTimeEffectSystem {
    private AmountResolverSystem amountResolverSystem;
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public PayCardCostEffect() {
        super("payCardCost");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        int cardId = Integer.parseInt(memory.get(gameEffect.getDataString("cardMemory")));
        Entity cardEntity = world.getEntity(cardId);

        int costToPlay = amountResolverSystem.resolveAmount(cardEntity, memory, "costToPlay");

        Entity ownerEntity = playerResolverSystem.resolvePlayer(cardEntity, memory, "owner");
        PlayerPublicStatsComponent stats = ownerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setCounterCount(stats.getCounterCount() - costToPlay);

        eventSystem.fireEvent(EntityUpdated.instance, ownerEntity);
    }
}
