package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardZone;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToCoreEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    public MoveCardToCoreEffect() {
        super("moveCardToCore");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        String fromZoneStr = gameEffect.getDataString("fromZone", null);
        CardZone fromZone = (fromZoneStr != null) ? CardZone.valueOf(fromZoneStr) : null;

        cardFilteringSystem.forEachCard(sourceEntity, memory, new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent card = cardEntity.getComponent(CardComponent.class);
                        CardZone oldZone = card.getCardZone();
                        if (fromZone == null || oldZone == fromZone) {
                            zoneOperations.moveFromCurrentZoneToCore(cardEntity);
                        }
                    }
                }, filter
        );
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter"},
                new String[]{"fromZone"});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        CardZone.valueOf(effect.getString("zone"));
        String fromZone = effect.getString("fromZone");
        if (fromZone != null)
            CardZone.valueOf(fromZone);
    }
}
