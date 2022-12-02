package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class ExecuteKillEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;
    private EventSystem eventSystem;

    public ExecuteKillEffect() {
        super("executeKill");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                zoneOperations.moveFromCurrentZoneToDiscardPile(entity);
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
