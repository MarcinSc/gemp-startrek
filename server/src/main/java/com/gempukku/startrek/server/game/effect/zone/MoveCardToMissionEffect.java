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
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.hand.CardInHandComponent;
import com.gempukku.startrek.game.mission.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.mission.FaceUpCardInMissionComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToMissionEffect extends OneTimeEffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private ComponentMapper<CardInPlayStatusComponent> cardInPlayStatusComponentMapper;

    public MoveCardToMissionEffect() {
        super("moveCardToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        int missionId = Integer.parseInt(memory.getValue(gameEffect.getDataString("missionMemory")));
        boolean faceUp = conditionResolverSystem.resolveBoolean(sourceEntity, memory, gameEffect.getDataString("faceUp"));
        Entity missionEntity = world.getEntity(missionId);
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent card = cardEntity.getComponent(CardComponent.class);
                        removeCardFromCurrentZone(cardEntity, card.getCardZone());
                        card.setCardZone(CardZone.MISSIONS);
                        if (faceUp) {
                            FaceUpCardInMissionComponent cardInMission = faceUpCardInMissionComponentMapper.create(cardEntity);
                            cardInMission.setMissionOwner(mission.getOwner());
                            cardInMission.setMissionIndex(mission.getMissionIndex());
                        } else {
                            FaceDownCardInMissionComponent cardInMission = faceDownCardInMissionComponentMapper.create(cardEntity);
                            cardInMission.setOwner(card.getOwner());
                            cardInMission.setMissionOwner(mission.getOwner());
                            cardInMission.setMissionIndex(mission.getMissionIndex());
                        }
                        cardInPlayStatusComponentMapper.create(cardEntity);
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
}
