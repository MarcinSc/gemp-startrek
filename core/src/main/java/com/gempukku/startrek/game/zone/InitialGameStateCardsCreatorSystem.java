package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.PlayerDiscardPileComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;

import java.util.function.Consumer;

public class InitialGameStateCardsCreatorSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private CardLookupSystem cardLookupSystem;
    private CardRenderingSystem cardRenderingSystem;
    private IncomingUpdatesProcessor incomingUpdatesProcessor;

    private ComponentMapper<OrderComponent> orderComponentMapper;

    private boolean initializedState = false;
    private boolean processState = false;

    private void cardInHandInserted(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZoneUtil.addCardInHand(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    private void cardInCoreInserted(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZoneUtil.addCardInCore(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    private void cardInBrigInserted(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZoneUtil.addCardInBrig(cardEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    private void objectOnStackInserted(Entity objectEntity) {
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        String objectType = objectOnStack.getType();
        // This class deals only with cards, not effects
        // Effects are done in GameStateCardsTrackingSystem
        if (objectType.equals("card")) {
            CardComponent card = objectEntity.getComponent(CardComponent.class);
            CardZoneUtil.addCardOnStack(objectEntity, card, cardLookupSystem, spawnSystem, cardRenderingSystem,
                    orderComponentMapper);
        }
    }

    private void processTopLevelCardInMission(Entity cardEntity) {
        CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
        if (cardInPlay.getAttachedToId() == null) {
            CardInMissionComponent cardInMisison = cardEntity.getComponent(CardInMissionComponent.class);
            CardZoneUtil.addCardInMission(cardEntity, cardInMisison.getMissionOwner(), cardInMisison.getMissionIndex(),
                    cardLookupSystem, spawnSystem, cardRenderingSystem);
        }
    }

    private void processAttachedCardInMission(Entity cardEntity) {
        CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
        if (cardInPlay.getAttachedToId() != null) {

            Entity attachedToCard = incomingUpdatesProcessor.getEntityById(cardInPlay.getAttachedToId());
            CardZoneUtil.addAttachedCardInMission(cardEntity, attachedToCard, cardLookupSystem, spawnSystem, cardRenderingSystem);
        }
    }

    private void processPlayerDiscardPile(Entity playerEntity) {
        PlayerDiscardPileComponent discardPile = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<String> cards = discardPile.getCards();
        if (cards.size > 0) {
            String topDiscardPileCardId = cards.peek();
            Entity topDiscardPileCardEntity = incomingUpdatesProcessor.getEntityById(topDiscardPileCardId);
            CardComponent card = topDiscardPileCardEntity.getComponent(CardComponent.class);
            CardZoneUtil.setTopDiscardPileCard(topDiscardPileCardEntity, card, cardLookupSystem, spawnSystem,
                    cardRenderingSystem);
        }
    }

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        if (!initializedState) {
            processState = true;
            initializedState = true;
        }
    }

    private void processServerState() {
        LazyEntityUtil.forEachEntityWithComponent(world, CardInHandComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        cardInHandInserted(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, CardInMissionComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        processTopLevelCardInMission(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, CardInCoreComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        cardInCoreInserted(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, CardInBrigComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        cardInBrigInserted(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, ObjectOnStackComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        objectOnStackInserted(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, CardInMissionComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        processAttachedCardInMission(entity);
                    }
                });
        LazyEntityUtil.forEachEntityWithComponent(world, PlayerDiscardPileComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        processPlayerDiscardPile(entity);
                    }
                });
    }

    @Override
    protected void processSystem() {
        if (processState) {
            processServerState();
            processState = false;
        }
    }
}
