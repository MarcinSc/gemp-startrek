package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.animation.AnimationDirectorSystem;
import com.gempukku.libgdx.lib.artemis.animation.animator.WaitAnimator;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;

public class GameStateCardsTrackingSystem extends BaseSystem {
    private CardLookupSystem cardLookupSystem;
    private SpawnSystem spawnSystem;
    private CardRenderingSystem cardRenderingSystem;
    private AnimationDirectorSystem animationDirectorSystem;

    private ComponentMapper<OrderComponent> orderComponentMapper;

    @EventListener
    public void cardZoneChanged(CardChangedZones cardChangedZones, Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);

        CardZone oldZone = cardChangedZones.getPreviousZone();
        CardZone newZone = card.getCardZone();

        Entity renderedCard = cardRenderingSystem.removeRenderedCard(cardEntity, oldZone);
        if (isBigCardZone(oldZone) == isBigCardZone(newZone)) {
            moveCardToZone(cardEntity, renderedCard, card, oldZone, newZone);
        } else {
            world.deleteEntity(renderedCard);
            createAndAddCardToZone(cardEntity, card, newZone);
        }
    }

    private void moveCardToZone(Entity cardEntity, Entity renderedCard, CardComponent card,
                                CardZone oldZone, CardZone zone) {
        if (zone == CardZone.Hand)
            CardZoneUtil.moveCardToHand(cardEntity, renderedCard, card, cardRenderingSystem);
        if (zone == CardZone.Brig)
            CardZoneUtil.moveCardToBrig(cardEntity, renderedCard, cardRenderingSystem);
        if (zone == CardZone.Core)
            CardZoneUtil.moveCardToCore(cardEntity, renderedCard, card, cardRenderingSystem);
        if (zone == CardZone.Stack) {
            CardZoneUtil.moveObjectToStack(cardEntity, renderedCard, cardRenderingSystem);
            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(20f));
        }

        FaceUpCardInMissionComponent faceUpCardInMission = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
        if (faceUpCardInMission != null) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
            CardZoneUtil.moveCardToMission(cardEntity, faceUpCardInMission.getMissionOwner(), faceUpCardInMission.getMissionIndex(),
                    renderedCard, card, cardDefinition, cardRenderingSystem);
        }
        FaceDownCardInMissionComponent faceDownCardInMission = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
        if (faceDownCardInMission != null) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
            CardZoneUtil.moveCardToMission(cardEntity, faceDownCardInMission.getMissionOwner(), faceDownCardInMission.getMissionIndex(),
                    renderedCard, card, cardDefinition, cardRenderingSystem);
        }
    }

    private void createAndAddCardToZone(Entity cardEntity, CardComponent card, CardZone zone) {
        if (zone == CardZone.Hand)
            CardZoneUtil.addCardInHand(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Brig)
            CardZoneUtil.addCardInBrig(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Core)
            CardZoneUtil.addCardInCore(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Stack)
            CardZoneUtil.addObjectOnStack(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem,
                    orderComponentMapper);

        if (cardEntity.getComponent(FaceUpCardInMissionComponent.class) != null)
            CardZoneUtil.addFaceUpCardInMission(cardEntity, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (cardEntity.getComponent(FaceDownCardInMissionComponent.class) != null)
            CardZoneUtil.addFaceDownCardInMission(cardEntity, cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    private boolean isBigCardZone(CardZone zone) {
        if (zone == CardZone.Hand || zone == CardZone.Stack)
            return true;
        return false;
    }

    @Override
    protected void processSystem() {

    }
}
