package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class ExecuteStopEffect extends OneTimeEffectSystem {
    private IdProviderSystem idProviderSystem;
    private EventSystem eventSystem;

    public ExecuteStopEffect() {
        super("executeStop");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String[] cardIds = StringUtils.split(memory.getValue(gameEffect.getDataString("memory")));

        for (String cardId : cardIds) {
            Entity entity = idProviderSystem.getEntityById(cardId);
            CardInPlayComponent cardInPlay = entity.getComponent(CardInPlayComponent.class);
            if (cardInPlay != null)
                cardInPlay.setStopped(true);
            eventSystem.fireEvent(EntityUpdated.instance, entity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory"},
                new String[]{});
    }
}
