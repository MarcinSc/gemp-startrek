package com.gempukku.startrek.server.game.effect.beam;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardsBeamed;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class BeamFromMissionEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;
    private EventSystem eventSystem;
    private GameEntityProvider gameEntityProvider;

    public BeamFromMissionEffect() {
        super("beamFromMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String shipFilter = gameEffect.getDataString("ship");
        String cardFilter = gameEffect.getDataString("filter");
        Entity shipEntity = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, shipFilter);
        Array<String> cardIds = new Array<>();
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, cardFilter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.attachToShip(shipEntity, entity);
                        cardIds.add(serverEntityIdSystem.getEntityId(entity));
                    }
                });
        eventSystem.fireEvent(new CardsBeamed(null, serverEntityIdSystem.getEntityId(shipEntity),
                cardIds), gameEntityProvider.getGameEntity());

    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"ship", "filter"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("ship"));
        cardFilteringSystem.validateFilter(effect.getString("filter"));
    }
}
