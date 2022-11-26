package com.gempukku.startrek.server.game.effect.reveal;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardHidden;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.EffectRevealedCardsComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class FlipRevealedCardsEffects extends OneTimeEffectSystem {
    private ExecutionStackSystem executionStackSystem;
    private IdProviderSystem idProviderSystem;
    private EventSystem eventSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;

    public FlipRevealedCardsEffects() {
        super("flipRevealedCards");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        EffectRevealedCardsComponent revealedCards = executionStackSystem.getTopMostStackEntityWithComponent(EffectRevealedCardsComponent.class).getComponent(EffectRevealedCardsComponent.class);
        for (String revealedCard : revealedCards.getRevealedCards()) {
            Entity entity = idProviderSystem.getEntityById(revealedCard);
            if (faceUpCardInMissionComponentMapper.has(entity)) {
                eventSystem.fireEvent(new CardHidden(idProviderSystem.getEntityId(entity)), entity);

                faceUpCardInMissionComponentMapper.remove(entity);
                FaceDownCardInMissionComponent faceDown = faceDownCardInMissionComponentMapper.create(entity);
                faceDown.setOwner(entity.getComponent(CardComponent.class).getOwner());

                eventSystem.fireEvent(EntityUpdated.instance, entity);
            }
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{});
    }
}
