package com.gempukku.startrek.game.render;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
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
import com.gempukku.startrek.game.zone.CardZone;

public class CardRenderingSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CameraSystem cameraSystem;
    private TransformSystem transformSystem;

    private final ObjectMap<PlayerPosition, PlayerZones> playerCardsMap = new ObjectMap<>();
    private final CommonZones commonZones = new CommonZones();

    private boolean stateChanged = false;

    public PlayerZones getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    public PlayerZones getPlayerCards(PlayerPosition playerPosition) {
        PlayerZones playerZones = playerCardsMap.get(playerPosition);
        if (playerZones == null) {
            playerZones = new PlayerZones();
            playerCardsMap.put(playerPosition, playerZones);
        }
        return playerZones;
    }

    public Entity removeRenderedCard(Entity cardEntity, CardZone oldZone) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.removeCard(cardEntity, oldZone);
            if (renderedCard != null)
                return renderedCard;
        }
        Entity renderedCard = commonZones.removeObject(cardEntity, oldZone);
        if (renderedCard != null)
            return renderedCard;
        return null;
    }

    public Entity findRenderedCard(Entity cardEntity) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.findRenderedCard(cardEntity);
            if (renderedCard != null)
                return renderedCard;
        }
        Entity renderedCard = commonZones.findRenderedCard(cardEntity);
        if (renderedCard != null)
            return renderedCard;
        return null;
    }

    public CommonZones getCommonZones() {
        return commonZones;
    }

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        stateChanged = true;
    }

    @Override
    protected void processSystem() {
        if (stateChanged) {
            stateChanged = false;

            Camera camera = cameraSystem.getCamera();

            if (commonZones.isStackDirty()) {
                StackLayout.layoutStack(commonZones, camera, transformSystem);
                commonZones.cleanup();
            }

            for (ObjectMap.Entry<PlayerPosition, PlayerZones> playerZonesStatus : playerCardsMap) {
                PlayerPosition playerPosition = playerZonesStatus.key;
                PlayerZones playerZones = playerZonesStatus.value;
                if (playerZones.isHandDirty()) {
                    HandLayout.layoutHand(playerZones, playerPosition,
                            camera, transformSystem);
                }
                for (int i = 0; i < 5; i++) {
                    if (playerZones.isMissionDirty(i)) {
                        MissionsLayout.layoutMission(playerZones, i, playerPosition, transformSystem);
                    }
                }
                if (playerZones.isCoreDirty()) {
                    CoreLayout.layoutCore(playerZones, playerPosition, transformSystem);
                }
                if (playerZones.isDeckDirty()) {
                    PileLayout.layoutPlayerDeck(playerZones, playerPosition, transformSystem);
                }
                if (playerZones.isDilemmaPileDirty()) {
                    PileLayout.layoutPlayerDilemmaPile(playerZones, playerPosition, transformSystem);
                }
                if (playerZones.isDiscardPileDirty())
                    PileLayout.layoutPlayerDiscardPile(playerZones, playerPosition, transformSystem);
                playerZones.cleanup();
            }
        }
    }
}
