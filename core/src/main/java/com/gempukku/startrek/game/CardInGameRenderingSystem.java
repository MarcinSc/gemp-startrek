package com.gempukku.startrek.game;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDF3DTextComponent;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDFTextBlock;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;

public class CardInGameRenderingSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private PlayerPositionSystem playerPositionSystem;
    private CameraSystem cameraSystem;
    private TransformSystem transformSystem;
    private CardLookupSystem cardLookupSystem;

    private ObjectMap<PlayerPosition, PlayerCards> playerCardsMap = new ObjectMap<>();

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(CardInHandComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInHandInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInHandRemoved(entities.get(i));
                                }
                            }
                        }
                );
    }

    private void cardInHandInserted(int i) {
        Entity card = world.getEntity(i);
        CardInHandComponent cardInHand = card.getComponent(CardInHandComponent.class);
        String owner = cardInHand.getOwner();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardInHand.getCardId());

        Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");
        SDF3DTextComponent sdfText = cardRepresentation.getComponent(SDF3DTextComponent.class);
        if (sdfText != null) {
            // Title
            SDFTextBlock titleBlock = sdfText.getTextBlocks().get(0);
            String titleText = (cardDefinition.isUnique() ? "• " : "") + cardDefinition.getTitle();
            titleBlock.setText(titleText);

            // Subtitle
            String subtitle = cardDefinition.getSubtitle();
            if (subtitle != null) {
                SDFTextBlock subtitleBlock = sdfText.getTextBlocks().get(1);
                subtitleBlock.setText(subtitle);
            }

            // Cost
            SDFTextBlock costBlock = sdfText.getTextBlocks().get(2);
            costBlock.setText(String.valueOf(cardDefinition.getCost()));

            // Species
            SDFTextBlock raceBlock = sdfText.getTextBlocks().get(3);
            raceBlock.setText(cardDefinition.getSpecies().toString());
        }
        getPlayerCards(owner).addCardInHand(card, cardRepresentation);

        layoutHand(owner);
    }

    private void layoutHand(String username) {
        PlayerCards playerCards = getPlayerCards(username);
        Array<Entity> cardsInHand = playerCards.getCardsInHand();

        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        if (playerPosition == PlayerPosition.Lower) {
            Camera camera = cameraSystem.getCamera();
            float verticalScale = 0.8f;
            // Temporary
            verticalScale = 0f;
            float distanceFromCamera = 3f;
            float cardSeparation = 0.15f;
            Vector3 basePlayerHandPosition =
                    new Vector3(camera.position)
                            .add(new Vector3(camera.direction).scl(distanceFromCamera))
                            .add(new Vector3(camera.up).scl(-verticalScale));
            Vector3 baseOpponentHandPosition = new Vector3(camera.position).add(camera.direction).add(new Vector3(camera.up).scl(verticalScale));

            int index = 0;
            int handSize = cardsInHand.size;
            for (Entity cardInHand : cardsInHand) {
                float indexBias = index - (handSize / 2f) + 0.5f;
                float cardScale = 0.5f;
                transformSystem.setTransform(cardInHand, new Matrix4()
                        .translate(basePlayerHandPosition.x + cardSeparation * indexBias, basePlayerHandPosition.y, basePlayerHandPosition.z)// + 0.005f * Math.abs(indexBias))
                        .scale(cardScale, cardScale, cardScale)
                        .rotate(1, 0, 0, 20)
                        .rotate(0, 1, 0, -indexBias * 1.5f)
                        .rotate(0, 0, 1, -2));

                index++;
            }
        }
    }

    private void cardInHandRemoved(int i) {
    }

    private PlayerCards getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        PlayerCards playerCards = playerCardsMap.get(playerPosition);
        if (playerCards == null) {
            playerCards = new PlayerCards();
            playerCardsMap.put(playerPosition, playerCards);
        }
        return playerCards;
    }

    @Override
    protected void processSystem() {

    }
}
