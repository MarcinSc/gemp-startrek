package com.gempukku.startrek.server.game.effect.beam;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.zone.CardsBeamed;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class BeamToMissionEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;
    private EventSystem eventSystem;

    public BeamToMissionEffect() {
        super("beamToMission");
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
                        zoneOperations.unattachFromShip(shipEntity, entity);
                        cardIds.add(serverEntityIdSystem.getEntityId(entity));
                    }
                });
        eventSystem.fireEvent(new CardsBeamed(serverEntityIdSystem.getEntityId(shipEntity), null,
                cardIds), LazyEntityUtil.findEntityWithComponent(world, GameComponent.class));
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"ship", "filter"},
                new String[]{});
        cardFilterResolverSystem.validateFilter(effect.getString("ship"));
        cardFilterResolverSystem.validateFilter(effect.getString("filter"));
    }
}
