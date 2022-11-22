package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.audio.AudioSystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;

public class BeamingTrackingSystem extends BaseSystem {
    private IncomingUpdatesProcessor incomingUpdatesProcessor;
    private AudioSystem audioSystem;
    private CardRenderingSystem cardRenderingSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;

    private Array<CardsBeamed> eventsToProcess = new Array<>();

    @EventListener
    public void cardsBeamed(CardsBeamed cardsBeamed, Entity entity) {
        eventsToProcess.add(cardsBeamed);
        audioSystem.playSound("fx", "transporter");
    }

    @Override
    protected void processSystem() {
        for (CardsBeamed cardsBeamed : eventsToProcess) {
            String fromShipId = cardsBeamed.getFromShipId();
            String toShipId = cardsBeamed.getToShipId();
            Array<String> entityIds = cardsBeamed.getEntityIds();

            if (fromShipId == null) {
                beamToShip(toShipId, entityIds);
            } else if (toShipId == null) {
                beamToMission(fromShipId, entityIds);
            } else {
                beamBetweenShips(fromShipId, toShipId, entityIds);
            }
        }
        eventsToProcess.clear();
    }

    private void beamToShip(String toShipId, Array<String> entityIds) {
        Entity shipEntity = incomingUpdatesProcessor.getEntityById(toShipId);
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);

        String cardOwner = shipEntity.getComponent(CardComponent.class).getOwner();
        boolean player = cardOwner.equals(cardInMission.getMissionOwner());
        MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
        for (String entityId : entityIds) {
            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard;
                if (player) {
                    renderedCard = missionCards.removePlayerTopLevelCardInMission(beamedEntity);
                } else {
                    renderedCard = missionCards.removeOpponentTopLevelCardInMission(beamedEntity);
                }
                missionCards.addAttachedCard(shipEntity, beamedEntity, renderedCard);
            } else {
                Entity renderedCard;
                if (player) {
                    renderedCard = missionCards.removeFaceDownPlayerCard();
                } else {
                    renderedCard = missionCards.removeFaceDownOpponentCard();
                }
                missionCards.addAttachedCard(shipEntity, null, renderedCard);
            }
        }
    }

    private void beamToMission(String fromShipId, Array<String> entityIds) {
        Entity shipEntity = incomingUpdatesProcessor.getEntityById(fromShipId);
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);

        String cardOwner = shipEntity.getComponent(CardComponent.class).getOwner();
        boolean player = cardOwner.equals(cardInMission.getMissionOwner());
        MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
        for (String entityId : entityIds) {
            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard = missionCards.removeAttachedCard(shipEntity, beamedEntity);
                if (player) {
                    missionCards.addPlayerTopLevelCardInMission(beamedEntity, renderedCard);
                } else {
                    missionCards.addOpponentTopLevelCardInMission(beamedEntity, renderedCard);
                }
            } else {
                Entity renderedCard = missionCards.removeFaceDownAttachedCard(shipEntity);
                if (player)
                    missionCards.addPlayerTopLevelCardInMission(null, renderedCard);
                else
                    missionCards.addOpponentTopLevelCardInMission(null, renderedCard);
            }
        }
    }

    private void beamBetweenShips(String fromShipId, String toShipId, Array<String> entityIds) {
        Entity fromShipEntity = incomingUpdatesProcessor.getEntityById(fromShipId);
        Entity toShipEntity = incomingUpdatesProcessor.getEntityById(toShipId);
        CardInMissionComponent cardInMission = fromShipEntity.getComponent(CardInMissionComponent.class);

        MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
        for (String entityId : entityIds) {
            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard = missionCards.removeAttachedCard(fromShipEntity, beamedEntity);
                missionCards.addAttachedCard(toShipEntity, beamedEntity, renderedCard);
            } else {
                Entity renderedCard = missionCards.removeFaceDownAttachedCard(fromShipEntity);
                missionCards.addAttachedCard(toShipEntity, null, renderedCard);
            }
        }
    }
}
