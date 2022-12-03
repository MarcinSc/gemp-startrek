package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.event.CardHidden;
import com.gempukku.startrek.game.event.CardRevealed;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;
import com.gempukku.startrek.game.template.CardTemplates;

public class RevealCardsTrackingSystem extends BaseSystem {
    private CardRenderingSystem cardRenderingSystem;
    private CardLookupSystem cardLookupSystem;
    private IdProviderSystem idProviderSystem;
    private SpawnSystem spawnSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;

    @EventListener
    public void cardRevealed(CardRevealed cardRevealed, Entity entity) {
        String cardId = cardRevealed.getCardId();
        Entity cardEntity = idProviderSystem.getEntityById(cardId);
        Entity renderedCard = cardRenderingSystem.findRenderedCard(cardEntity);
        if (renderedCard == null) {
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            CardZone zone = card.getCardZone();
            if (zone == CardZone.Mission) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);

                CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
                renderedCard = CardTemplates.createRenderedCard(cardDefinition, zone, spawnSystem);

                if (cardInPlay.getAttachedToId() != null) {
                    Entity attachedToCardEntity = idProviderSystem.getEntityById(cardInPlay.getAttachedToId());
                    Entity oldFaceDown = cardRenderingSystem.replaceFaceDownAttachedCard(attachedToCardEntity, cardEntity, renderedCard);
                    world.deleteEntity(oldFaceDown);
                } else {
                    MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
                    boolean missionOwner = card.getOwner().equals(cardInMission.getMissionOwner());
                    RenderedCardGroup cardGroup;
                    if (missionOwner)
                        cardGroup = missionCards.getMissionOwnerCards();
                    else
                        cardGroup = missionCards.getOpposingCards();
                    Entity oldFaceDown = cardGroup.removeFaceDownCard();
                    cardGroup.addFaceUpCard(cardEntity, renderedCard);
                    world.deleteEntity(oldFaceDown);
                }
            }
        }
    }

    @EventListener
    public void cardsHidden(CardHidden cardHidden, Entity entity) {
        String cardId = cardHidden.getCardId();
        Entity cardEntity = idProviderSystem.getEntityById(cardId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone zone = card.getCardZone();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);

        Entity renderedCard = cardRenderingSystem.findRenderedCard(cardEntity);
        boolean owner = authenticationHolderSystem.equals(card.getOwner());
        if (renderedCard != null && !CardZoneUtil.isCardFaceUp(zone, cardDefinition.getType(), owner)) {
            if (zone == CardZone.Mission) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);

                renderedCard = CardTemplates.createSmallFaceDownCard(spawnSystem);
                if (cardInPlay.getAttachedToId() != null) {
                    Entity attachedToCardEntity = idProviderSystem.getEntityById(cardInPlay.getAttachedToId());
                    Entity oldFaceUp = cardRenderingSystem.replaceFaceUpAttachedCard(attachedToCardEntity, cardEntity, renderedCard);
                    world.deleteEntity(oldFaceUp);
                } else {
                    MissionCards missionCards = cardRenderingSystem.getPlayerCards(cardInMission.getMissionOwner()).getMissionCards(cardInMission.getMissionIndex());
                    boolean missionOwner = card.getOwner().equals(cardInMission.getMissionOwner());
                    RenderedCardGroup cardGroup;
                    if (missionOwner)
                        cardGroup = missionCards.getMissionOwnerCards();
                    else
                        cardGroup = missionCards.getOpposingCards();
                    Entity oldFaceUp = cardGroup.removeFaceUpCard(cardEntity);
                    cardGroup.addFaceDownCard(renderedCard);
                    world.deleteEntity(oldFaceUp);
                }

            }
        }
    }

    @Override
    protected void processSystem() {

    }
}
