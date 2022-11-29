package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class ExecuteStopEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;

    public ExecuteStopEffect() {
        super("executeStop");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                CardInPlayComponent cardInPlay = entity.getComponent(CardInPlayComponent.class);
                if (cardInPlay != null)
                    cardInPlay.setStopped(true);
                eventSystem.fireEvent(EntityUpdated.instance, entity);
            }
        }, gameEffect.getDataString("filter"));
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
    }
}
