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
import com.gempukku.startrek.card.Affiliation;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardIcon;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.hand.CardInHandComponent;

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
        Entity cardEntity = world.getEntity(i);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardInHandComponent cardInHand = cardEntity.getComponent(CardInHandComponent.class);
        String owner = cardInHand.getOwner();
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        Affiliation affiliation = cardDefinition.getAffiliation();
        String cardTemplate = getCardTemplate(affiliation);

        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);
        TextureReference textureReference = (TextureReference) cardTemplateSprite.getSprites().get(0).getProperties().get("Texture");
        textureReference.setRegion(cardTemplate);

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
            for (int iconIndex = 0; iconIndex < icons.size; iconIndex++) {
                SpriteDefinition spriteDefinition = cardTemplateSprite.getSprites().get(1 + iconIndex);
                TextureReference iconTextureReference = (TextureReference) spriteDefinition.getProperties().get("Texture");
                iconTextureReference.setRegion(icons.get(iconIndex).name());
            }
        }
        getPlayerCards(owner).addCardInHand(cardEntity, cardRepresentation);

        layoutHand(owner);
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
            // Temporary
            cardSeparation = 0.3f;
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
                // Temporary
                cardScale = 1f;
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
