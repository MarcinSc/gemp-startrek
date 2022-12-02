package com.gempukku.startrek.server.game.effect.beam;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.event.CardsBeamed;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class BeamToMissionEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;
    private EventSystem eventSystem;
    private GameEntityProvider gameEntityProvider;

    public BeamToMissionEffect() {
        super("beamToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String shipFilter = gameEffect.getDataString("ship");
        String cardFilter = gameEffect.getDataString("filter");
        Entity shipEntity = cardFilteringSystem.findFirstCard(sourceEntity, memory, "inPlay", shipFilter);
        Array<String> cardIds = new Array<>();
        cardFilteringSystem.forEachCard(sourceEntity, memory, "inPlay", new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.unattachFromShip(shipEntity, entity);
                        cardIds.add(serverEntityIdSystem.getEntityId(entity));
                    }
                }, cardFilter
        );
        eventSystem.fireEvent(new CardsBeamed(serverEntityIdSystem.getEntityId(shipEntity), null,
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
