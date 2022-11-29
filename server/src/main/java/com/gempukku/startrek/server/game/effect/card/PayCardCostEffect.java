package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class PayCardCostEffect extends OneTimeEffectSystem {
    private AmountResolverSystem amountResolverSystem;
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;
    private ServerEntityIdSystem serverEntityIdSystem;

    public PayCardCostEffect() {
        super("payCardCost");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String cardId = memory.getValue(gameEffect.getDataString("cardMemory"));
        Entity cardEntity = serverEntityIdSystem.findfromId(cardId);

        int costToPlay = amountResolverSystem.resolveAmount(cardEntity, memory, "costToPlay");

        Entity ownerEntity = playerResolverSystem.resolvePlayer(cardEntity, memory, "owner");
        PlayerPublicStatsComponent stats = ownerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setCounterCount(stats.getCounterCount() - costToPlay);

        eventSystem.fireEvent(EntityUpdated.instance, ownerEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"cardMemory"},
                new String[]{});
    }
}
