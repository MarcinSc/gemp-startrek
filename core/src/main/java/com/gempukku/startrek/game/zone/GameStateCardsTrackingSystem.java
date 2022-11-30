package com.gempukku.startrek.game.zone;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.libgdx.lib.artemis.animation.AnimationDirectorSystem;
import com.gempukku.libgdx.lib.artemis.animation.animator.WaitAnimator;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.EffectComponent;
import com.gempukku.startrek.game.card.SpecialActionLookupSystem;
import com.gempukku.startrek.game.event.CardChangedZones;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;
import com.gempukku.startrek.game.template.CardTemplates;

public class GameStateCardsTrackingSystem extends BaseSystem {
    private static final float CARD_ON_STACK_PAUSE = 1f;

    private CardLookupSystem cardLookupSystem;
    private SpecialActionLookupSystem specialActionLookupSystem;
    private SpawnSystem spawnSystem;
    private CardRenderingSystem cardRenderingSystem;
    private AnimationDirectorSystem animationDirectorSystem;
    private IncomingUpdatesProcessor incomingUpdatesProcessor;
    private AuthenticationHolderSystem authenticationHolderSystem;

    private ComponentMapper<OrderComponent> orderComponentMapper;

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(CardComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {

                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, size = entities.size(); i < size; i++) {
                                    cardRemoved(entities.get(i));
                                }
                            }
                        });
        world.getAspectSubscriptionManager().get(Aspect.all(EffectComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, size = entities.size(); i < size; i++) {
                                    effectInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, size = entities.size(); i < size; i++) {
                                    effectRemoved(entities.get(i));
                                }
                            }
                        });
    }

    private void effectInserted(int entityId) {
        Entity effectEntity = world.getEntity(entityId);
        EffectComponent effect = effectEntity.getComponent(EffectComponent.class);
        String specialAction = effect.getSpecialAction();
        if (specialAction == null) {
            CardZoneUtil.addEffectOnStack(effectEntity, effect, cardLookupSystem, spawnSystem, cardRenderingSystem,
                    orderComponentMapper);
            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(CARD_ON_STACK_PAUSE));
        } else {
            CardZoneUtil.addSpecialActionOnStack(effectEntity, specialAction, specialActionLookupSystem, spawnSystem,
                    cardRenderingSystem, orderComponentMapper);
            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(CARD_ON_STACK_PAUSE));
        }
    }

    private void effectRemoved(int entityId) {
        Entity effectEntity = world.getEntity(entityId);
        Entity renderedCard = cardRenderingSystem.removeFaceUpCard(effectEntity, CardZone.Stack);
        if (renderedCard != null) {
            world.deleteEntity(renderedCard);
        }
    }

    private void cardRemoved(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity renderedCard = cardRenderingSystem.removeFaceUpCard(cardEntity, card.getCardZone());
        if (renderedCard != null) {
            world.deleteEntity(renderedCard);
        }
    }

    @EventListener
    public void cardZoneChanged(CardChangedZones cardChangedZones, Entity entity) {
        String cardId = cardChangedZones.getCardId();
        Entity cardEntity = incomingUpdatesProcessor.getEntityById(cardId);

        String cardOwner = cardChangedZones.getCardOwner();
        CardZone fromZone = cardChangedZones.getFromZone();
        CardZone toZone = cardChangedZones.getToZone();
        String missionOwner = cardChangedZones.getMissionOwner();
        int missionIndex = cardChangedZones.getMissionIndex();

        if (cardEntity != null) {
            // Moving card either to or from a zone where the card is known
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            boolean owner = authenticationHolderSystem.getUsername().equals(card.getOwner());
            CardType type = cardLookupSystem.getCardDefinition(cardEntity).getType();

            boolean faceUpInFromZone = CardZoneUtil.isCardFaceUp(fromZone, type, owner);
            boolean faceUpInToZone = CardZoneUtil.isCardFaceUp(toZone, type, owner);

            if (faceUpInFromZone && faceUpInToZone) {
                Entity renderedCard = cardRenderingSystem.removeFaceUpCard(cardEntity, fromZone);
                if (renderedCard != null) {
                    if (CardZoneUtil.isBigCard(fromZone) == CardZoneUtil.isBigCard(toZone)) {
                        moveCardToZone(cardEntity, renderedCard, card, toZone, missionOwner, missionIndex);
                    } else {
                        world.deleteEntity(renderedCard);
                        createAndAddCardToZone(cardEntity, card, toZone, missionOwner, missionIndex);
                    }
                } else {
                    createAndAddCardToZone(cardEntity, card, toZone, missionOwner, missionIndex);
                }
            } else if (faceUpInFromZone) {
                Entity renderedCard = cardRenderingSystem.removeFaceUpCard(cardEntity, fromZone);
                world.deleteEntity(renderedCard);
                if (CardZoneUtil.isCardRendered(toZone))
                    createFaceDownCardAndAddToZone(cardOwner, toZone, missionOwner, missionIndex);
            } else if (faceUpInToZone) {
                if (CardZoneUtil.isCardRendered(fromZone)) {
                    Entity renderedCard = removeFaceDownCard(cardOwner, fromZone, missionOwner, missionIndex);
                    world.deleteEntity(renderedCard);
                }
                createAndAddCardToZone(cardEntity, card, toZone, missionOwner, missionIndex);
            } else {
                throw new GdxRuntimeException("Unexpected state, card is known, but not face up for the player");
            }
        } else {
            // Moving card between unknown zones
            boolean renderedInFromZone = CardZoneUtil.isCardRendered(fromZone);
            boolean renderedInToZone = CardZoneUtil.isCardRendered(toZone);
            if (renderedInFromZone && renderedInToZone) {
                if (CardZoneUtil.isBigCard(fromZone) == CardZoneUtil.isBigCard(toZone)) {
                    Entity removedCard = removeFaceDownCard(cardOwner, fromZone, missionOwner, missionIndex);
                    moveFaceDownCardToZone(cardOwner, toZone, removedCard, missionOwner, missionIndex);
                } else {
                    Entity removedCard = removeFaceDownCard(cardOwner, fromZone, missionOwner, missionIndex);
                    world.deleteEntity(removedCard);
                    createFaceDownCardAndAddToZone(cardOwner, toZone, missionOwner, missionIndex);
                }
            } else if (renderedInFromZone) {
                Entity removedCard = removeFaceDownCard(cardOwner, fromZone, missionOwner, missionIndex);
                world.deleteEntity(removedCard);
            } else if (renderedInToZone) {
                createFaceDownCardAndAddToZone(cardOwner, fromZone, missionOwner, missionIndex);
            }
        }
    }

    private void moveCardToZone(Entity cardEntity, Entity renderedCard, CardComponent card,
                                CardZone zone, String missionOwner, int missionIndex) {
        if (zone == CardZone.Hand)
            CardZoneUtil.moveCardToHand(cardEntity, renderedCard, card, cardRenderingSystem);
        if (zone == CardZone.Brig)
            CardZoneUtil.moveCardToBrig(cardEntity, renderedCard, cardRenderingSystem);
        if (zone == CardZone.Core)
            CardZoneUtil.moveCardToCore(cardEntity, renderedCard, card, cardRenderingSystem);
        if (zone == CardZone.Stack) {
            CardZoneUtil.moveObjectToStack(cardEntity, renderedCard, cardRenderingSystem);
            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(CARD_ON_STACK_PAUSE));
        }
        if (zone == CardZone.Mission) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
            CardZoneUtil.moveCardToMission(cardEntity, missionOwner, missionIndex,
                    renderedCard, card, cardDefinition, cardRenderingSystem);
        }
        if (zone == CardZone.DiscardPile) {
            Entity oldRenderedCard = CardZoneUtil.moveCardAsTopDiscardPileCard(cardEntity, renderedCard, cardRenderingSystem);
            if (oldRenderedCard != null)
                world.deleteEntity(oldRenderedCard);
        }
    }

    private Entity removeFaceDownCard(String cardOwner, CardZone zone, String missionOwner, int missionIndex) {
        if (zone == CardZone.Hand) {
            return cardRenderingSystem.getPlayerCards(cardOwner).getCardsInHand().removeFaceDownCard();
        }
        if (zone == CardZone.Mission) {
            MissionCards missionCards = cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex);
            boolean isMissionOwner = missionOwner.equals(cardOwner);
            if (isMissionOwner) {
                return missionCards.getMissionOwnerCards().removeFaceDownCard();
            } else {
                return missionCards.getOpposingCards().removeFaceDownCard();
            }
        }
        throw new GdxRuntimeException("Unable to remove face down card from unknown zone");
    }

    private void createFaceDownCardAndAddToZone(String cardOwner, CardZone zone, String missionOwner, int missionIndex) {
        if (zone == CardZone.Hand) {
            Entity faceDownCard = CardTemplates.createFaceDownCard(spawnSystem);
            moveFaceDownCardToZone(cardOwner, zone, faceDownCard, missionOwner, missionIndex);
        }
        if (zone == CardZone.Mission) {
            Entity faceDownCard = CardTemplates.createSmallFaceDownCard(spawnSystem);
            moveFaceDownCardToZone(cardOwner, zone, faceDownCard, missionOwner, missionIndex);
        }
    }

    private void moveFaceDownCardToZone(String cardOwner, CardZone zone, Entity renderedCard, String missionOwner, int missionIndex) {
        if (zone == CardZone.Hand) {
            cardRenderingSystem.getPlayerCards(cardOwner).getCardsInDeck().addFaceDownCard(renderedCard);
        }
        if (zone == CardZone.Mission) {
            MissionCards missionCards = cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex);
            boolean isMissionOwner = missionOwner.equals(cardOwner);
            if (isMissionOwner)
                missionCards.getMissionOwnerCards().addFaceDownCard(renderedCard);
            else
                missionCards.getOpposingCards().addFaceDownCard(renderedCard);
        }
    }

    private void createAndAddCardToZone(Entity cardEntity, CardComponent card, CardZone zone, String missionOwner, int missionIndex) {
        if (zone == CardZone.Hand)
            CardZoneUtil.addCardInHand(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Brig)
            CardZoneUtil.addCardInBrig(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Core)
            CardZoneUtil.addCardInCore(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
        if (zone == CardZone.Stack) {
            CardZoneUtil.addCardOnStack(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem,
                    orderComponentMapper);
            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(CARD_ON_STACK_PAUSE));
        }
        if (zone == CardZone.Mission) {
            CardZoneUtil.addCardInMission(cardEntity, missionOwner, missionIndex, cardLookupSystem, spawnSystem, cardRenderingSystem);
        }
        if (zone == CardZone.DiscardPile) {
            Entity oldRenderedCard = CardZoneUtil.setTopDiscardPileCard(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
            if (oldRenderedCard != null)
                world.deleteEntity(oldRenderedCard);
        }
    }

    @Override
    protected void processSystem() {

    }
}
