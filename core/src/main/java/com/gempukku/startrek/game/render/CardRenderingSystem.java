package com.gempukku.startrek.game.render;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.layout.*;
import com.gempukku.startrek.game.render.zone.CommonZones;
import com.gempukku.startrek.game.render.zone.PlayerZones;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;
import com.gempukku.startrek.game.zone.CardZone;

public class CardRenderingSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CameraSystem cameraSystem;
    private TransformSystem transformSystem;

    private final ObjectMap<Entity, Entity> serverToRenderedMap = new ObjectMap<>();
    private final ObjectMap<Entity, RenderedCardGroup> attachedCards = new ObjectMap<>();

    private final ObjectMap<PlayerPosition, PlayerZones> playerCardsMap = new ObjectMap<>();
    private final CommonZones commonZones = new CommonZones(serverToRenderedMap);

    private boolean stateChanged = false;

    public PlayerZones getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    public PlayerZones getPlayerCards(PlayerPosition playerPosition) {
        PlayerZones playerZones = playerCardsMap.get(playerPosition);
        if (playerZones == null) {
            playerZones = new PlayerZones(serverToRenderedMap);
            playerCardsMap.put(playerPosition, playerZones);
        }
        return playerZones;
    }

    public Entity removeFaceUpCard(Entity cardEntity, CardZone oldZone) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.removeFaceUpCard(cardEntity, oldZone);
            if (renderedCard != null)
                return renderedCard;
        }
        Entity renderedCard = commonZones.removeFaceUpCard(cardEntity, oldZone);
        if (renderedCard != null)
            return renderedCard;
        return null;
    }

    public Entity findRenderedCard(Entity cardEntity) {
        return serverToRenderedMap.get(cardEntity);
    }

    public CommonZones getCommonZones() {
        return commonZones;
    }

    public RenderedCardGroup getAttachedCards(Entity renderedEntity) {
        return attachedCards.get(renderedEntity);
    }

    public Array<Entity> getAttachedRenderedcards(Entity renderedEntity) {
        RenderedCardGroup attachedCards = getAttachedCards(renderedEntity);
        if (attachedCards == null)
            return new Array<>();
        return attachedCards.getRenderedCards();
    }

    public void addFaceUpAttachedCard(Entity attachedToCardEntity, Entity cardEntity, Entity renderedEntity) {
        RenderedCardGroup attachedGroup = getOrCreateAttachedGroup(attachedToCardEntity);
        attachedGroup.addFaceUpCard(cardEntity, renderedEntity);
    }

    public Entity removeFaceUpAttachedCard(Entity attachedToCardEntity, Entity cardEntity) {
        RenderedCardGroup attachedGroup = getOrCreateAttachedGroup(attachedToCardEntity);
        Entity renderedEntity = attachedGroup.removeFaceUpCard(cardEntity);
        if (attachedGroup.isEmpty()) {
            attachedCards.remove(attachedToCardEntity);
        }
        return renderedEntity;
    }

    public void addFaceDownAttachedCard(Entity attachedToCardEntity, Entity renderedEntity) {
        RenderedCardGroup attachedGroup = getOrCreateAttachedGroup(attachedToCardEntity);
        attachedGroup.addFaceDownCard(renderedEntity);
    }

    public Entity removeFaceDownAttachedCard(Entity attachedToCardEntity) {
        RenderedCardGroup attachedGroup = getOrCreateAttachedGroup(attachedToCardEntity);
        Entity renderedEntity = attachedGroup.removeFaceDownCard();
        if (attachedGroup.isEmpty()) {
            attachedCards.remove(attachedToCardEntity);
        }
        return renderedEntity;
    }

    private RenderedCardGroup getOrCreateAttachedGroup(Entity attachedToCardEntity) {
        Entity attachedToRenderedCard = findRenderedCard(attachedToCardEntity);
        RenderedCardGroup attachedGroup = attachedCards.get(attachedToRenderedCard);
        if (attachedGroup == null) {
            attachedGroup = new RenderedCardGroup(serverToRenderedMap);
            attachedCards.put(attachedToRenderedCard, attachedGroup);
        }
        return attachedGroup;
    }

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        stateChanged = true;
    }

    @Override
    protected void processSystem() {
        if (stateChanged) {
            stateChanged = false;

            Camera camera = cameraSystem.getCamera("main");

            if (commonZones.isStackDirty()) {
                StackLayout.layoutStack(commonZones, camera, transformSystem);
                commonZones.cleanup();
            }

            for (ObjectMap.Entry<PlayerPosition, PlayerZones> playerZonesStatus : playerCardsMap) {
                PlayerPosition playerPosition = playerZonesStatus.key;
                PlayerZones playerZones = playerZonesStatus.value;
                if (playerZones.isHandDirty()) {
                    HandLayout.layoutHand(playerZones.getCardsInHand(), playerPosition,
                            camera, transformSystem);
                }
                for (int i = 0; i < 5; i++) {
                    if (playerZones.isMissionDirty(i)) {
                        MissionsLayout.layoutMission(this, playerZones, i, playerPosition, transformSystem);
                    }
                }
                if (playerZones.isCoreDirty()) {
                    CoreLayout.layoutCore(this, playerZones.getCardsInCore(), playerPosition, transformSystem);
                }
                if (playerZones.isDeckDirty()) {
                    PileLayout.layoutPlayerDeck(playerZones.getCardsInDeck(), playerPosition, transformSystem);
                }
                if (playerZones.isDilemmaPileDirty()) {
                    PileLayout.layoutPlayerDilemmaPile(playerZones.getCardsInDilemmaPile(), playerPosition, transformSystem);
                }
                if (playerZones.isDiscardPileDirty())
                    PileLayout.layoutPlayerDiscardPile(playerZones, playerPosition, transformSystem);
                playerZones.cleanup();
            }
        }
    }
}
