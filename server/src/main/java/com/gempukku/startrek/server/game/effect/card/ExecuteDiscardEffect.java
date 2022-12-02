package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

public class ExecuteDiscardEffect extends OneTimeEffectSystem {
    private IdProviderSystem idProviderSystem;
    private ZoneOperations zoneOperations;

    public ExecuteDiscardEffect() {
        super("executeDiscard");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String[] cardIds = StringUtils.split(memory.getValue(gameEffect.getDataString("memory")));

        for (String cardId : cardIds) {
            Entity entity = idProviderSystem.getEntityById(cardId);
            zoneOperations.moveFromCurrentZoneToDiscardPile(entity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory"},
                new String[]{});
    }
}
