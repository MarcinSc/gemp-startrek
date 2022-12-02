package com.gempukku.startrek.server.game.effect.reveal;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.event.CardRevealed;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.EffectRevealedCardsComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

import java.util.function.Consumer;

public class RevealCardsEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;
    private ExecutionStackSystem executionStackSystem;
    private EventSystem eventSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;

    public RevealCardsEffect() {
        super("revealCards");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        EffectRevealedCardsComponent revealedCards = executionStackSystem.getTopMostStackEntityWithComponent(EffectRevealedCardsComponent.class).getComponent(EffectRevealedCardsComponent.class);
        String filter = gameEffect.getDataString("filter");
        cardFilteringSystem.forEachCard(sourceEntity, memory, gameEffect.getDataString("from"),
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        if (faceDownCardInMissionComponentMapper.has(entity)) {
                            faceUpCardInMissionComponentMapper.create(entity);
                            faceDownCardInMissionComponentMapper.remove(entity);
                            eventSystem.fireEvent(EntityUpdated.instance, entity);

                            eventSystem.fireEvent(new CardRevealed(idProviderSystem.getEntityId(entity)), entity);

                            revealedCards.getRevealedCards().add(idProviderSystem.getEntityId(entity));
                        }
                    }
                }, filter);
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
