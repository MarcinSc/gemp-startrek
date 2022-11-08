package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardInPlayStatusComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.hand.CardInHandComponent;
import com.gempukku.startrek.game.mission.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToZoneEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<CardInPlayStatusComponent> cardInPlayStatusComponentMapper;

    public MoveCardToZoneEffect() {
        super("moveCardToZone");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        CardZone zone = CardZone.valueOf(gameEffect.getDataString("zone"));

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent card = cardEntity.getComponent(CardComponent.class);
                        CardZone oldZone = card.getCardZone();
                        removeCardFromCurrentZone(cardEntity, oldZone);
                        addCardToNewZone(cardEntity, zone, card.getOwner());
                        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
                    }
                });
    }

    private void removeCardFromCurrentZone(Entity cardEntity, CardZone zone) {
        if (zone == CardZone.HAND) {
            cardInHandComponentMapper.remove(cardEntity);
        } else if (zone == CardZone.MISSIONS) {
            faceUpCardInMissionComponentMapper.remove(cardEntity);
        }
        cardInPlayStatusComponentMapper.remove(cardEntity);
    }

    private void addCardToNewZone(Entity cardEntity, CardZone zone, String owner) {
        if (zone == CardZone.HAND) {
            CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
            cardInHand.setOwner(owner);
        }
    }
}
