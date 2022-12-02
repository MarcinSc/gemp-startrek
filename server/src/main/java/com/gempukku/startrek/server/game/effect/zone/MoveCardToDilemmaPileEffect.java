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

public class MoveCardToDilemmaPileEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    public MoveCardToDilemmaPileEffect() {
        super("moveCardToDilemmaPile");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String filter = gameEffect.getDataString("filter");
        String fromZoneStr = gameEffect.getDataString("fromZone", null);
        CardZone fromZone = (fromZoneStr != null) ? CardZone.valueOf(fromZoneStr) : null;

        cardFilteringSystem.forEachCard(sourceEntity, memory, new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent card = cardEntity.getComponent(CardComponent.class);
                        CardZone oldZone = card.getCardZone();
                        if (fromZone == null || oldZone == fromZone) {
                            zoneOperations.moveFromCurrentZoneToBottomOfDilemmaPile(cardEntity, true);
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
        String fromZone = effect.getString("fromZone", null);
        if (fromZone != null)
            CardZone.valueOf(fromZone);
    }
}
