package com.gempukku.startrek.server.game.effect.card;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class TurnMissionCardsFaceDownEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;

    public TurnMissionCardsFaceDownEffect() {
        super("turnMissionCardsFaceDown");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, "or(type(Personnel),type(Equipment))",
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        FaceUpCardInMissionComponent faceUp = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
                        if (faceUp != null) {
                            CardComponent card = cardEntity.getComponent(CardComponent.class);
                            FaceDownCardInMissionComponent faceDown = faceDownCardInMissionComponentMapper.create(cardEntity);
                            faceDown.setOwner(card.getOwner());
                            faceDown.setMissionIndex(faceUp.getMissionIndex());
                            faceDown.setMissionOwner(faceUp.getMissionOwner());
                            faceUpCardInMissionComponentMapper.remove(cardEntity);
                            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
                        }
                    }
                }
        );
    }
}
