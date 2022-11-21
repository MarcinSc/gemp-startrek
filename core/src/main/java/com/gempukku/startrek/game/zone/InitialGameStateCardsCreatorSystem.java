package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;

import java.util.function.Consumer;

public class InitialGameStateCardsCreatorSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private CardLookupSystem cardLookupSystem;
    private CardRenderingSystem cardRenderingSystem;

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

    private void cardInMissionInserted(Entity cardEntity) {
        CardZoneUtil.addCardInMission(cardEntity, cardLookupSystem, spawnSystem, cardRenderingSystem);
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
                        cardInMissionInserted(entity);
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
    }

    @Override
    protected void processSystem() {
        if (processState) {
            processServerState();
            processState = false;
        }
    }
}
