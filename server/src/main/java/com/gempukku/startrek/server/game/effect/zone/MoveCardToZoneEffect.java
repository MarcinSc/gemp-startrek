package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.zone.CardZone;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToZoneEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    public MoveCardToZoneEffect() {
        super("moveCardToZone");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        CardZone zone = CardZone.valueOf(gameEffect.getDataString("zone"));
        String fromZoneStr = gameEffect.getDataString("fromZone", null);
        CardZone fromZone = (fromZoneStr != null) ? CardZone.valueOf(fromZoneStr) : null;

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent card = cardEntity.getComponent(CardComponent.class);
                        CardZone oldZone = card.getCardZone();
                        if (fromZone == null || oldZone == fromZone) {
                            zoneOperations.removeFromCurrentZone(cardEntity);
                            zoneOperations.moveToNewZone(cardEntity, zone);
                        }
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter", "zone"},
                new String[]{"fromZone"});
        cardFilterResolverSystem.validate(effect.getString("filter"));
        CardZone.valueOf(effect.getString("zone"));
        String fromZone = effect.getString("fromZone");
        if (fromZone != null)
            CardZone.valueOf(fromZone);
    }
}
