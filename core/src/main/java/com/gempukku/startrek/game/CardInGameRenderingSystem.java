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
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDF3DTextComponent;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDFTextBlock;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.startrek.card.*;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.game.hand.CardInHandComponent;
import com.gempukku.startrek.game.mission.FaceUpCardInMissionComponent;

public class CardInGameRenderingSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private PlayerPositionSystem playerPositionSystem;
    private CameraSystem cameraSystem;
    private TransformSystem transformSystem;
    private CardLookupSystem cardLookupSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;

    private static final float[] deckRotation = new float[]{0f, -1f, 0.3f, 0.1f, -0.3f, -0.1f, 0f, 0.15f};

    private ObjectMap<PlayerPosition, PlayerCards> playerCardsMap = new ObjectMap<>();
    private ObjectMap<PlayerPosition, ZonesStatus> playerZonesStatusMap = new ObjectMap<>();

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
        world.getAspectSubscriptionManager().get(Aspect.all(FaceUpCardInMissionComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    faceUpCardInMissionInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {

                            }
                        }
                );
    }

    private void cardInHandInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String owner = card.getOwner();
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = createFullCard(cardId, cardDefinition);
        getPlayerCards(owner).addCardInHand(cardEntity, cardRepresentation);

        getZonesStatus(owner).setHandDrity(true);
    }

    private void faceUpCardInMissionInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        FaceUpCardInMissionComponent cardInMission = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
        String cardId = card.getCardId();
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = createSmallCard(cardId, cardDefinition);
        getPlayerCards(missionOwner).getMissionCards(missionIndex).setMissionCard(cardEntity, cardRepresentation);

        getZonesStatus(missionOwner).setMissionsDirty(true);
    }

    private Entity createSmallCard(String cardId, CardDefinition cardDefinition) {
        Entity cardRepresentation;
        if (cardDefinition.getType() == CardType.Mission) {
            cardRepresentation = spawnSystem.spawnEntity("game/mission-small.template");
            //cardRepresentation = spawnSystem.spawnEntity("game/mission-small-textboxes.template");
        } else {
            throw new GdxRuntimeException("Type of card not implemented: " + cardDefinition.getType());
        }
        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);

        TextureReference cardImageTexture = (TextureReference) cardTemplateSprite.getSprites().get(1).getProperties().get("Texture");
        String[] cardIdSplit = cardId.split("_");
        String cardPath = "cardImages/set" + cardIdSplit[0] + "/" + cardIdSplit[1] + ".png";
        cardImageTexture.setRegion(cardPath);

        TextureReference missionTypeTexture = (TextureReference) cardTemplateSprite.getSprites().get(2).getProperties().get("Texture");
        missionTypeTexture.setRegion(cardDefinition.getMissionType().name());

        SDF3DTextComponent sdfText = cardRepresentation.getComponent(SDF3DTextComponent.class);
        if (sdfText != null) {
            // Title
            SDFTextBlock titleBlock = sdfText.getTextBlocks().get(0);
            titleBlock.setText(cardDefinition.getTitle());
            // Points
            SDFTextBlock pointsBlock = sdfText.getTextBlocks().get(1);
            pointsBlock.setText(String.valueOf(cardDefinition.getPoints()));
            // Span
            SDFTextBlock spanBlock = sdfText.getTextBlocks().get(2);
            String span = cardDefinition.getQuadrant().getSymbol() + "[scale 0.8]" + cardDefinition.getSpan() + "[/scale]";
            spanBlock.setText(span);
            // Mission affiliations
            SDFTextBlock affiliationsBlock = sdfText.getTextBlocks().get(3);
            affiliationsBlock.setText(cardDefinition.getAffiliationsText());
        }

        return cardRepresentation;
    }

    private Entity createFullCard(String cardId, CardDefinition cardDefinition) {
        Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        Affiliation affiliation = cardDefinition.getAffiliation();
        String cardTemplate = getCardTemplate(affiliation);

        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);
        TextureReference cardTemplateTexture = (TextureReference) cardTemplateSprite.getSprites().get(0).getProperties().get("Texture");
        cardTemplateTexture.setRegion(cardTemplate);

        TextureReference cardImageTexture = (TextureReference) cardTemplateSprite.getSprites().get(1).getProperties().get("Texture");
        String[] cardIdSplit = cardId.split("_");
        String cardPath = "cardImages/set" + cardIdSplit[0] + "/" + cardIdSplit[1] + ".png";
        cardImageTexture.setRegion(cardPath);

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

            // Text
            SDFTextBlock textBlock = sdfText.getTextBlocks().get(4);
            textBlock.setText(createCardText(cardDefinition));

            // Icons
            Array<CardIcon> icons = cardDefinition.getIcons();
            int nonStaffIcon = 0;
            for (int iconIndex = 0; iconIndex < icons.size; iconIndex++) {
                CardIcon icon = icons.get(iconIndex);
                int spriteIconIndex;
                if (icon == CardIcon.Stf || icon == CardIcon.Cmd) {
                    spriteIconIndex = 2;
                } else {
                    spriteIconIndex = 3 + nonStaffIcon;
                    nonStaffIcon++;
                }
                SpriteDefinition spriteDefinition = cardTemplateSprite.getSprites().get(spriteIconIndex);
                TextureReference iconTextureReference = (TextureReference) spriteDefinition.getProperties().get("Texture");
                iconTextureReference.setRegion(icon.name());
            }

            //Stats
            SDFTextBlock integrityBlock = sdfText.getTextBlocks().get(5);
            integrityBlock.setText("I[scale 0.8]NTEGRITY[/scale] " + cardDefinition.getIntegrity());
            SDFTextBlock cunningBlock = sdfText.getTextBlocks().get(6);
            cunningBlock.setText("C[scale 0.8]UNNING[/scale] " + cardDefinition.getCunning());
            SDFTextBlock strengthBlock = sdfText.getTextBlocks().get(7);
            strengthBlock.setText("S[scale 0.8]TRENGTH[/scale] " + cardDefinition.getStrength());
        }
        return cardRepresentation;
    }

    private String getCardTemplate(Affiliation affiliation) {
        if (affiliation == Affiliation.Federation)
            return "federation-template";
        else if (affiliation == Affiliation.Bajoran)
            return "bajoran-template";
        return null;
    }

    private String createCardText(CardDefinition cardDefinition) {
        StringBuilder result = new StringBuilder();
        Array<String> skills = cardDefinition.getSkills();
        if (skills != null) {
            for (String skill : skills) {
                result.append("[width 0.5][letterSpacing 5][color ff0000]•[/color][/letterSpacing]").append(skill).append("[/width] ");
            }
            result.append("\n");
        }
        Array<String> keywords = cardDefinition.getKeywords();
        if (keywords != null) {
            for (String keyword : keywords) {
                result.append("[width 0.5]").append(keyword).append(".[/width] ");
            }
            result.append("\n");
        }
        Array<JsonValue> cardAbilities = cardDefinition.getAbilities();
        if (cardAbilities != null) {
            for (JsonValue ability : cardAbilities) {
                String text = ability.getString("text");
                result.append(text).append("\n");
            }
        }
        String lore = cardDefinition.getLore();
        if (lore != null) {
            result.append("[horAlign justified][paddingLeft 20][paddingRight 20][width 0.46][scale 0.8][font font/arial-italic.fnt]").append(lore).append("[/font][/scale][/width][/paddingRight][/paddingLeft][/horAlign]");
        }

        return result.toString();
    }

    private void layoutMissions(PlayerPosition playerPosition) {
        float vertTrans = 1.2f;
        float horTrans = 2f;
        float yTrans = 0.1f;
        float scale = 1.5f;
        PlayerCards playerCards = getPlayerCards(playerPosition);
        Matrix4 m4 = new Matrix4();
        for (int i = 0; i < 5; i++) {
            MissionCards missionCards = playerCards.getMissionCards(i);
            Entity missionCard = missionCards.getMissionCard();
            if (missionCard != null) {
                float verticalTranslate = (playerPosition == PlayerPosition.Lower) ? vertTrans : -vertTrans;
                float horizontalTranslate = (i - 2) * horTrans;
                float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

                m4.idt()
                        .translate(0, yTrans, verticalTranslate)
                        .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                        .translate(horizontalTranslate, 0, 0)
                        .scl(scale);

                transformSystem.setTransform(missionCard, m4);
            }
        }
    }

    private void layoutHand(PlayerPosition playerPosition) {
        PlayerCards playerCards = getPlayerCards(playerPosition);
        Array<Entity> cardsInHand = playerCards.getCardsInHand();

        Camera camera = cameraSystem.getCamera();
        float verticalScale = 0.85f;
        float distanceFromCamera = 3f;
        float cardSeparation = 0.15f;
        float cardScale = 0.4f;

        Vector3 basePlayerHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(distanceFromCamera))
                        .add(new Vector3(camera.up).scl(-verticalScale));
        Vector3 baseOpponentHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(distanceFromCamera))
                        .add(new Vector3(camera.up).scl(verticalScale));

        Vector3 playerHandPosition = (playerPosition == PlayerPosition.Lower) ? basePlayerHandPosition : baseOpponentHandPosition;

        int index = 0;
        int handSize = cardsInHand.size;
        for (Entity cardInHand : cardsInHand) {
            float indexBias = index - (handSize / 2f) + 0.5f;
            float rotateX = 10;
            float rotateY = (playerPosition == PlayerPosition.Lower) ? -indexBias * 1.5f : 180 + indexBias * 1.5f;
            float rotateZ = (playerPosition == PlayerPosition.Lower) ? -2 : 2;
            transformSystem.setTransform(cardInHand, new Matrix4()
                    .translate(playerHandPosition.x + cardSeparation * indexBias, playerHandPosition.y, playerHandPosition.z)// + 0.005f * Math.abs(indexBias))
                    .scale(cardScale, cardScale, cardScale)
                    .rotate(1, 0, 0, rotateX)
                    .rotate(0, 1, 0, rotateY)
                    .rotate(0, 0, 1, rotateZ));

            index++;
        }
    }

    private void cardInHandRemoved(int i) {
    }

    private PlayerCards getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    private PlayerCards getPlayerCards(PlayerPosition playerPosition) {
        PlayerCards playerCards = playerCardsMap.get(playerPosition);
        if (playerCards == null) {
            playerCards = new PlayerCards();
            playerCardsMap.put(playerPosition, playerCards);
        }
        return playerCards;
    }

    private ZonesStatus getZonesStatus(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getZonesStatus(playerPosition);
    }

    private ZonesStatus getZonesStatus(PlayerPosition playerPosition) {
        ZonesStatus result = playerZonesStatusMap.get(playerPosition);
        if (result == null) {
            result = new ZonesStatus();
            playerZonesStatusMap.put(playerPosition, result);
        }
        return result;
    }

    private Entity createFaceDownCard() {
        return spawnSystem.spawnEntity("game/card-facedown.template");
    }

    private Entity addFaceDownCardToHand(PlayerPosition playerPosition) {
        Entity cardRepresentation = createFaceDownCard();
        getPlayerCards(playerPosition).addCardInHand(null, cardRepresentation);
        return cardRepresentation;
    }

    private Entity addFaceDownCardToDeck(PlayerPosition playerPosition) {
        Entity cardRepresentation = createFaceDownCard();
        getPlayerCards(playerPosition).addCardInDeck(null, cardRepresentation);
        return cardRepresentation;
    }

    private Entity addFaceDownCardToDilemmaPile(PlayerPosition playerPosition) {
        Entity cardRepresentation = createFaceDownCard();
        getPlayerCards(playerPosition).addCardInDilemmaPile(null, cardRepresentation);
        return cardRepresentation;
    }

    @Override
    protected void processSystem() {
        setupUnknownPlayersHands();
        setupPlayersDecks();
        setupPlayersDillemaPiles();

        for (ObjectMap.Entry<PlayerPosition, ZonesStatus> playerZonesStatus : playerZonesStatusMap) {
            ZonesStatus zonesStatus = playerZonesStatus.value;
            if (zonesStatus.isHandDrity()) {
                layoutHand(playerZonesStatus.key);
            }
            if (zonesStatus.isMissionsDirty()) {
                layoutMissions(playerZonesStatus.key);
            }
            zonesStatus.cleanZones();
        }
    }

    private void setupPlayersDecks() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            int deckCount = getRenderedDeckSize(publicStats.getDeckCount());
            PlayerCards playerCards = playerCardsMap.get(playerPosition);
            int renderedCount = playerCards.getCardInDeckCount();
            if (renderedCount > deckCount) {
                int destroyCount = renderedCount - deckCount;
                for (int i = 0; i < destroyCount; i++) {
                    Entity removedCard = playerCards.removeOneCardInDeck();
                    world.deleteEntity(removedCard);
                }
            } else if (renderedCount < deckCount) {
                float xTranslate = 4f;
                float zTranslate = 3.3f;
                int createCount = deckCount - renderedCount;
                for (int i = 0; i < createCount; i++) {
                    Entity cardRepresentation = addFaceDownCardToDeck(playerPosition);
                    int cardIndex = playerCards.getCardInDeckCount() - 1;

                    float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
                    yRotation += 3 * deckRotation[cardIndex];
                    float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

                    transformSystem.setTransform(cardRepresentation,
                            new Matrix4().idt()
                                    .translate(xTranslate, 0f + cardIndex * 0.005f, zMove)
                                    // A bit crooked
                                    .rotate(0, 1, 0, yRotation));
                }
            }
        }
    }

    private void setupPlayersDillemaPiles() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            int deckCount = getRenderedDeckSize(publicStats.getDilemmaCount());
            PlayerCards playerCards = playerCardsMap.get(playerPosition);
            int renderedCount = playerCards.getCardInDilemmaCount();
            if (renderedCount > deckCount) {
                int destroyCount = renderedCount - deckCount;
                for (int i = 0; i < destroyCount; i++) {
                    Entity removedCard = playerCards.removeOneCardInDilemmaPile();
                    world.deleteEntity(removedCard);
                }
            } else if (renderedCount < deckCount) {
                float xTranslate = -4f;
                float zTranslate = 3.3f;
                int createCount = deckCount - renderedCount;
                for (int i = 0; i < createCount; i++) {
                    Entity cardRepresentation = addFaceDownCardToDilemmaPile(playerPosition);
                    int cardIndex = playerCards.getCardInDilemmaCount() - 1;

                    float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
                    yRotation += 3 * deckRotation[cardIndex];
                    float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

                    transformSystem.setTransform(cardRepresentation,
                            new Matrix4().idt()
                                    .translate(xTranslate, 0f + cardIndex * 0.005f, zMove)
                                    // A bit crooked
                                    .rotate(0, 1, 0, yRotation));
                }
            }
        }
    }

    private int getRenderedDeckSize(int realDeckSize) {
        if (realDeckSize > 20)
            return 8;
        if (realDeckSize > 10)
            return 7;
        if (realDeckSize > 5)
            return 6;
        return realDeckSize;
    }

    private void setupUnknownPlayersHands() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            if (!username.equals(authenticationHolderSystem.getUsername())) {
                PlayerPosition playerPosition = player.value;
                Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
                PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
                int handCount = publicStats.getHandCount();
                PlayerCards playerCards = playerCardsMap.get(playerPosition);
                int renderedCount = playerCards.getCardInHandCount();
                if (renderedCount > handCount) {
                    int destroyCount = renderedCount - handCount;
                    for (int i = 0; i < destroyCount; i++) {
                        Entity removedCard = playerCards.removeOneCardInHand();
                        world.deleteEntity(removedCard);
                    }
                    getZonesStatus(playerPosition).setHandDrity(true);
                } else if (renderedCount < handCount) {
                    int createCount = handCount - renderedCount;
                    for (int i = 0; i < createCount; i++) {
                        addFaceDownCardToHand(playerPosition);
                    }
                    getZonesStatus(playerPosition).setHandDrity(true);
                }
            }
        }
    }
}
