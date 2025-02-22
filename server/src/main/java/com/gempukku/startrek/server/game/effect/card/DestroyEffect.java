package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class DestroyEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public DestroyEffect() {
        super("destroy");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        cardFilteringSystem.forEachCard(sourceEntity, memory, gameEffect.getDataString("from"),
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.moveFromCurrentZoneToDiscardPile(cardEntity);
                    }
                }, gameEffect.getDataString("filter")
        );
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"from", "filter"},
                new String[]{});
        cardFilteringSystem.validateSource(effect.getString("from"));
        cardFilteringSystem.validateFilter(effect.getString("filter"));
    }
}
