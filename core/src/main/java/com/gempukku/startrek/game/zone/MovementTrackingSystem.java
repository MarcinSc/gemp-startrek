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
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;

public class MovementTrackingSystem extends BaseSystem {
    private IncomingUpdatesProcessor incomingUpdatesProcessor;
    private AudioSystem audioSystem;
    private CardRenderingSystem cardRenderingSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;

    private final Array<CardsBeamed> beamEventsToProcess = new Array<>();
    private final Array<ShipMoved> shipEventsToProcess = new Array<>();

    @EventListener
    public void cardsBeamed(CardsBeamed cardsBeamed, Entity entity) {
        beamEventsToProcess.add(cardsBeamed);
        audioSystem.playSound("fx", "transporter");
    }

    @EventListener
    public void shipMoved(ShipMoved shipMoved, Entity entity) {
        shipEventsToProcess.add(shipMoved);
    }

    @Override
    protected void processSystem() {
        for (CardsBeamed cardsBeamed : beamEventsToProcess) {
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
        beamEventsToProcess.clear();
        for (ShipMoved shipMoved : shipEventsToProcess) {
            shipMoved(shipMoved.getShipId(),
                    shipMoved.getMissionOwnerFrom(), shipMoved.getMissionIndexFrom(),
                    shipMoved.getMissionOwnerTo(), shipMoved.getMissionIndexTo());
        }
        shipEventsToProcess.clear();
    }

    private void shipMoved(
            String shipId,
            String missionOwnerFrom, int missionIndexFrom,
            String missionOwnerTo, int missionIndexTo) {
        Entity shipEntity = incomingUpdatesProcessor.getEntityById(shipId);
        String shipOwner = shipEntity.getComponent(CardComponent.class).getOwner();

        MissionCards missionCardsFrom = cardRenderingSystem.getPlayerCards(missionOwnerFrom).getMissionCards(missionIndexFrom);
        RenderedCardGroup fromCards;
        if (shipOwner.equals(missionOwnerFrom))
            fromCards = missionCardsFrom.getMissionOwnerCards();
        else
            fromCards = missionCardsFrom.getOpposingCards();
        Entity renderedShipEntity = fromCards.removeFaceUpCard(shipEntity, false);

        MissionCards missionCardsTo = cardRenderingSystem.getPlayerCards(missionOwnerTo).getMissionCards(missionIndexTo);
        RenderedCardGroup toCards;
        if (shipOwner.equals(missionOwnerTo))
            toCards = missionCardsTo.getMissionOwnerCards();
        else
            toCards = missionCardsTo.getOpposingCards();
        toCards.addFaceUpCard(shipEntity, renderedShipEntity);
    }

    private void beamToShip(String toShipId, Array<String> entityIds) {
        Entity shipEntity = incomingUpdatesProcessor.getEntityById(toShipId);
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);

        String cardOwner = shipEntity.getComponent(CardComponent.class).getOwner();
        boolean player = cardOwner.equals(cardInMission.getMissionOwner());
        MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
        for (String entityId : entityIds) {
            RenderedCardGroup cardGroup;
            if (player)
                cardGroup = missionCards.getMissionOwnerCards();
            else
                cardGroup = missionCards.getOpposingCards();

            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard = cardGroup.removeFaceUpCard(beamedEntity);
                cardGroup.addAttachedFaceUpCard(shipEntity, beamedEntity, renderedCard);
            } else {
                Entity renderedCard = cardGroup.removeFaceDownCard();
                cardGroup.addAttachedFaceDownCard(shipEntity, renderedCard);
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
            RenderedCardGroup cardGroup;
            if (player)
                cardGroup = missionCards.getMissionOwnerCards();
            else
                cardGroup = missionCards.getOpposingCards();

            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard = cardGroup.removeAttachedFaceUpCard(shipEntity, beamedEntity);
                cardGroup.addFaceUpCard(beamedEntity, renderedCard);
            } else {
                Entity renderedCard = cardGroup.removeAttachedFaceDownCard(shipEntity);
                cardGroup.addFaceDownCard(renderedCard);
            }
        }
    }

    private void beamBetweenShips(String fromShipId, String toShipId, Array<String> entityIds) {
        Entity fromShipEntity = incomingUpdatesProcessor.getEntityById(fromShipId);
        Entity toShipEntity = incomingUpdatesProcessor.getEntityById(toShipId);
        CardInMissionComponent cardInMission = fromShipEntity.getComponent(CardInMissionComponent.class);

        String cardOwner = fromShipEntity.getComponent(CardComponent.class).getOwner();
        boolean player = cardOwner.equals(cardInMission.getMissionOwner());
        MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
        for (String entityId : entityIds) {
            RenderedCardGroup cardGroup;
            if (player)
                cardGroup = missionCards.getMissionOwnerCards();
            else
                cardGroup = missionCards.getOpposingCards();
            Entity beamedEntity = incomingUpdatesProcessor.getEntityById(entityId);
            if (beamedEntity != null) {
                Entity renderedCard = cardGroup.removeAttachedFaceUpCard(fromShipEntity, beamedEntity);
                cardGroup.addAttachedFaceUpCard(toShipEntity, beamedEntity, renderedCard);
            } else {
                Entity renderedCard = cardGroup.removeAttachedFaceDownCard(fromShipEntity);
                cardGroup.addAttachedFaceDownCard(toShipEntity, renderedCard);
            }
        }
    }
}
