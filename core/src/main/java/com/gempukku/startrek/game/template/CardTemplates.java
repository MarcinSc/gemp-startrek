package com.gempukku.startrek.game.template;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.startrek.card.*;
import com.gempukku.startrek.game.zone.CardZone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardTemplates {
    private static Pattern stepStartPattern = Pattern.compile("(\\[s [^]]+])");

    public static Entity createRenderedCard(CardDefinition cardDefinition, CardZone cardZone, SpawnSystem spawnSystem) {
        boolean smallZone = isSmallZone(cardZone);
        if (smallZone)
            return createSmallCard(cardDefinition, cardZone, spawnSystem);
        else
            return createFullCard(cardDefinition, spawnSystem);
    }

    private static Entity createSmallCard(CardDefinition cardDefinition, CardZone cardZone, SpawnSystem spawnSystem) {
        Entity renderedEntity = spawnSystem.spawnEntity(getSmallTemplate(cardDefinition));

        TextureReference cardImageTexture = CardBoxesLayout.getTextureReference(renderedEntity, CardBoxesLayout.getImageTextureIndex(cardDefinition, cardZone));
        cardImageTexture.setRegion(cardDefinition.getCardImagePath());

        int missionTypeIndex = CardBoxesLayout.getMissionTypeTextureIndex(cardDefinition, cardZone);
        if (missionTypeIndex > -1) {
            CardBoxesLayout.getTextureReference(renderedEntity, missionTypeIndex).
                    setRegion(cardDefinition.getMissionType().name());
        }

        int affiliationTextureIndex = CardBoxesLayout.getAffiliationTextureIndex(cardDefinition, cardZone);
        if (affiliationTextureIndex > -1) {
            CardBoxesLayout.getTextureReference(renderedEntity, affiliationTextureIndex).
                    setRegion(cardDefinition.getAffiliation().getIcon().name());
        }

        // Icons
        Array<CardIcon> icons = cardDefinition.getIcons();
        if (icons != null) {
            int nonStaffIcon = 0;
            for (int iconIndex = 0; iconIndex < icons.size; iconIndex++) {
                CardIcon icon = icons.get(iconIndex);
                int spriteIconIndex;
                if (icon == CardIcon.Stf || icon == CardIcon.Cmd) {
                    spriteIconIndex = 0;
                } else {
                    spriteIconIndex = 1 + nonStaffIcon;
                    nonStaffIcon++;
                }
                int iconTextureIndex = CardBoxesLayout.getIconTextureIndex(cardDefinition, spriteIconIndex, cardZone);
                if (iconTextureIndex > -1) {
                    CardBoxesLayout.getTextureReference(renderedEntity, iconTextureIndex).
                            setRegion(icon.name());
                }
            }
        }

        CardBoxesLayout.getTextBlock(renderedEntity, CardBoxesLayout.getTitleTextIndex(cardDefinition, cardZone)).
                setText(cardDefinition.getTitle());

        int pointsIndex = CardBoxesLayout.getPointsTextIndex(cardDefinition, cardZone);
        if (pointsIndex > -1) {
            CardBoxesLayout.getTextBlock(renderedEntity, pointsIndex).
                    setText(String.valueOf(cardDefinition.getPoints()));
        }

        int spanIndex = CardBoxesLayout.getSpanTextIndex(cardDefinition, cardZone);
        if (spanIndex > -1) {
            String span = cardDefinition.getQuadrant().getSymbol() + "[scale 0.8]" + cardDefinition.getSpan() + "[/scale]";
            CardBoxesLayout.getTextBlock(renderedEntity, spanIndex).
                    setText(span);
        }

        int affiliationsIndex = CardBoxesLayout.getAffiliationsTextIndex(cardDefinition, cardZone);
        if (affiliationsIndex > -1) {
            CardBoxesLayout.getTextBlock(renderedEntity, affiliationsIndex).
                    setText(cardDefinition.getAffiliationsText());
        }

        int personnelStatsIndex = CardBoxesLayout.getPersonnelStatsTextIndex(cardDefinition, cardZone);
        if (personnelStatsIndex > -1) {
            // Stats
            String stats = cardDefinition.getIntegrity() + " / " + cardDefinition.getCunning() + " / " + cardDefinition.getStrength();
            CardBoxesLayout.getTextBlock(renderedEntity, personnelStatsIndex).
                    setText(stats);
        }

        int shipStatsIndex = CardBoxesLayout.getShipStatsTextIndex(cardDefinition, cardZone);
        if (shipStatsIndex > -1) {
            String stats = cardDefinition.getRange() + " / " + cardDefinition.getWeapons() + " / " + cardDefinition.getShields();
            CardBoxesLayout.getTextBlock(renderedEntity, shipStatsIndex).
                    setText(stats);
        }

        return renderedEntity;
    }

    private static boolean isSmallZone(CardZone cardZone) {
        return cardZone == CardZone.Mission || cardZone == CardZone.Brig || cardZone == CardZone.Core;
    }

    private static boolean isMission(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Mission;
    }

    private static boolean isNoun(CardDefinition cardDefinition) {
        CardType type = cardDefinition.getType();
        return type == CardType.Personnel || type == CardType.Ship;
    }

    private static String getSmallTemplate(CardDefinition cardDefinition) {
        if (isMission(cardDefinition))
            return "game/card/mission-small.template";
        else if (isNoun(cardDefinition))
            return "game/card/noun-small.template";
        else
            return "game/card/verb-small.template";
    }

    public static Entity createFaceDownCard(SpawnSystem spawnSystem) {
        return spawnSystem.spawnEntity("game/card/card-facedown.template");
    }

    public static Entity createSmallFaceDownCard(SpawnSystem spawnSystem) {
        return spawnSystem.spawnEntity("game/card/card-small-facedown.template");
    }

    public static Entity createFullCard(CardDefinition cardDefinition, SpawnSystem spawnSystem) {
        CardType cardType = cardDefinition.getType();

        if (cardType == CardType.Personnel || cardType == CardType.Ship) {
            return createAffiliatedFullCard(cardDefinition, spawnSystem, cardType);
        } else if (cardType == CardType.Equipment || cardType == CardType.Event
                || cardType == CardType.Interrupt || cardType == CardType.Dilemma) {
            return createUnaffiliatedFullCard(cardDefinition, spawnSystem, cardType);
        }
        throw new GdxRuntimeException("Unable to create a full card for card type: " + cardType);
    }

    public static Entity createEffect(CardDefinition cardDefinition, int abilityIndex, SpawnSystem spawnSystem) {
        // TODO temporary
        CardType cardType = cardDefinition.getType();

        if (cardType == CardType.Personnel || cardType == CardType.Ship) {
            return createAffiliatedEffect(cardDefinition, spawnSystem, cardType, abilityIndex);
        } else if (cardType == CardType.Equipment || cardType == CardType.Event
                || cardType == CardType.Interrupt || cardType == CardType.Dilemma) {
            return createUnaffiliatedEffect(cardDefinition, spawnSystem, cardType, abilityIndex);
        }
        throw new GdxRuntimeException("Unable to create a full card for card type: " + cardType);
    }

    private static Entity createUnaffiliatedEffect(CardDefinition cardDefinition, SpawnSystem spawnSystem, CardType cardType,
                                                   int abilityIndex) {
        Entity cardRepresentation = spawnSystem.spawnEntity("game/card/card-full-unaffiliated.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        String cardTemplate = getUnaffiliatedCardTemplate(cardType);

        TextureReference cardTemplateTexture = CardBoxesLayout.getTextureReference(cardRepresentation, CardBoxesLayout.getTemplateTextureIndex(cardDefinition, CardZone.Stack));
        cardTemplateTexture.setRegion(cardTemplate);

        TextureReference cardImageTexture = CardBoxesLayout.getTextureReference(cardRepresentation, CardBoxesLayout.getImageTextureIndex(cardDefinition, CardZone.Stack));
        cardImageTexture.setRegion(cardDefinition.getCardImagePath());

        TextComponent text = cardRepresentation.getComponent(TextComponent.class);
        if (text != null) {
            // Title
            TextBlock titleBlock = text.getTextBlocks().get(0);
            String titleText = (cardDefinition.isUnique() ? "• " : "") + cardDefinition.getTitle();
            titleBlock.setText(titleText);

            if (cardType != CardType.Interrupt) {
                // Cost
                TextBlock costBlock = text.getTextBlocks().get(1);
                costBlock.setText(String.valueOf(cardDefinition.getCost()));
            } else {
                TextBlock costBlock = text.getTextBlocks().get(1);
                costBlock.setText("");
            }

            // Type
            TextBlock typeBlock = text.getTextBlocks().get(2);
            typeBlock.setText(cardType.name());

            // Text
            TextBlock textBlock = text.getTextBlocks().get(3);
            textBlock.setText(createEffectText(cardDefinition, abilityIndex));
        }
        return cardRepresentation;
    }

    private static Entity createUnaffiliatedFullCard(CardDefinition cardDefinition, SpawnSystem spawnSystem, CardType cardType) {
        Entity cardRepresentation = spawnSystem.spawnEntity("game/card/card-full-unaffiliated.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        String cardTemplate = getUnaffiliatedCardTemplate(cardType);

        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);
        TextureReference cardTemplateTexture = (TextureReference) cardTemplateSprite.getSprites().get(0).getProperties().get("Texture");
        cardTemplateTexture.setRegion(cardTemplate);

        TextureReference cardImageTexture = (TextureReference) cardTemplateSprite.getSprites().get(1).getProperties().get("Texture");
        cardImageTexture.setRegion(cardDefinition.getCardImagePath());

        TextComponent text = cardRepresentation.getComponent(TextComponent.class);
        if (text != null) {
            // Title
            TextBlock titleBlock = text.getTextBlocks().get(0);
            String titleText = (cardDefinition.isUnique() ? "• " : "") + cardDefinition.getTitle();
            titleBlock.setText(titleText);

            if (cardType != CardType.Interrupt) {
                // Cost
                TextBlock costBlock = text.getTextBlocks().get(1);
                costBlock.setText(String.valueOf(cardDefinition.getCost()));
            } else {
                TextBlock costBlock = text.getTextBlocks().get(1);
                costBlock.setText("");
            }

            // Type
            TextBlock typeBlock = text.getTextBlocks().get(2);
            typeBlock.setText(cardType.name());

            // Text
            TextBlock textBlock = text.getTextBlocks().get(3);
            textBlock.setText(createCardText(cardDefinition));
        }
        return cardRepresentation;
    }

    private static Entity createAffiliatedEffect(CardDefinition cardDefinition, SpawnSystem spawnSystem, CardType cardType,
                                                 int abilityIndex) {
        Entity cardRepresentation = spawnSystem.spawnEntity("game/card/card-full-affiliated.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        Affiliation affiliation = cardDefinition.getAffiliation();
        String cardTemplate = getAffiliatedCardTemplate(affiliation);

        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);
        TextureReference cardTemplateTexture = (TextureReference) cardTemplateSprite.getSprites().get(0).getProperties().get("Texture");
        cardTemplateTexture.setRegion(cardTemplate);

        TextureReference cardImageTexture = (TextureReference) cardTemplateSprite.getSprites().get(1).getProperties().get("Texture");
        cardImageTexture.setRegion(cardDefinition.getCardImagePath());

        TextComponent text = cardRepresentation.getComponent(TextComponent.class);
        if (text != null) {
            // Title
            TextBlock titleBlock = text.getTextBlocks().get(0);
            String titleText = (cardDefinition.isUnique() ? "• " : "") + cardDefinition.getTitle();
            titleBlock.setText(titleText);

            // Subtitle
            String subtitle = cardDefinition.getSubtitle();
            if (subtitle != null) {
                TextBlock subtitleBlock = text.getTextBlocks().get(1);
                subtitleBlock.setText(subtitle);
            }

            // Cost
            TextBlock costBlock = text.getTextBlocks().get(2);
            costBlock.setText(String.valueOf(cardDefinition.getCost()));

            if (cardType == CardType.Personnel) {
                // Species
                TextBlock raceBlock = text.getTextBlocks().get(3);
                raceBlock.setText(cardDefinition.getSpecies().toString());
            } else if (cardType == CardType.Ship) {
                // Class
                TextBlock classBlock = text.getTextBlocks().get(3);
                classBlock.setText("[font font/arial-italic.fnt]" + cardDefinition.getShipClass() + "[/font] Class");
            }

            // Text
            TextBlock textBlock = text.getTextBlocks().get(4);
            textBlock.setText(createEffectText(cardDefinition, abilityIndex));

            // Icons
            Array<CardIcon> icons = cardDefinition.getIcons();
            if (icons != null) {
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
            }

            if (cardType == CardType.Personnel) {
                // Stats
                TextBlock integrityBlock = text.getTextBlocks().get(5);
                integrityBlock.setText("I[scale 0.8]NTEGRITY[/scale] " + cardDefinition.getIntegrity());
                TextBlock cunningBlock = text.getTextBlocks().get(6);
                cunningBlock.setText("C[scale 0.8]UNNING[/scale] " + cardDefinition.getCunning());
                TextBlock strengthBlock = text.getTextBlocks().get(7);
                strengthBlock.setText("S[scale 0.8]TRENGTH[/scale] " + cardDefinition.getStrength());
            } else if (cardType == CardType.Ship) {
                // Stats
                TextBlock rangeBlock = text.getTextBlocks().get(5);
                rangeBlock.setText("R[scale 0.8]ANGE[/scale] " + cardDefinition.getRange());
                TextBlock weaponsBlock = text.getTextBlocks().get(6);
                weaponsBlock.setText("W[scale 0.8]EAPONS[/scale] " + cardDefinition.getWeapons());
                TextBlock shieldsBlock = text.getTextBlocks().get(7);
                shieldsBlock.setText("S[scale 0.8]HIELDS[/scale] " + cardDefinition.getShields());
            }
        }
        return cardRepresentation;
    }

    private static Entity createAffiliatedFullCard(CardDefinition cardDefinition, SpawnSystem spawnSystem, CardType cardType) {
        Entity cardRepresentation = spawnSystem.spawnEntity("game/card/card-full-affiliated.template");
        //Entity cardRepresentation = spawnSystem.spawnEntity("game/card-full-textboxes.template");

        Affiliation affiliation = cardDefinition.getAffiliation();
        String cardTemplate = getAffiliatedCardTemplate(affiliation);

        SpriteComponent cardTemplateSprite = cardRepresentation.getComponent(SpriteComponent.class);
        TextureReference cardTemplateTexture = (TextureReference) cardTemplateSprite.getSprites().get(0).getProperties().get("Texture");
        cardTemplateTexture.setRegion(cardTemplate);

        TextureReference cardImageTexture = (TextureReference) cardTemplateSprite.getSprites().get(1).getProperties().get("Texture");
        cardImageTexture.setRegion(cardDefinition.getCardImagePath());

        TextComponent text = cardRepresentation.getComponent(TextComponent.class);
        if (text != null) {
            // Title
            TextBlock titleBlock = text.getTextBlocks().get(0);
            String titleText = (cardDefinition.isUnique() ? "• " : "") + cardDefinition.getTitle();
            titleBlock.setText(titleText);

            // Subtitle
            String subtitle = cardDefinition.getSubtitle();
            if (subtitle != null) {
                TextBlock subtitleBlock = text.getTextBlocks().get(1);
                subtitleBlock.setText(subtitle);
            }

            // Cost
            TextBlock costBlock = text.getTextBlocks().get(2);
            costBlock.setText(String.valueOf(cardDefinition.getCost()));

            if (cardType == CardType.Personnel) {
                // Species
                TextBlock raceBlock = text.getTextBlocks().get(3);
                raceBlock.setText(cardDefinition.getSpecies().toString());
            } else if (cardType == CardType.Ship) {
                // Class
                TextBlock classBlock = text.getTextBlocks().get(3);
                classBlock.setText("[font font/arial-italic.fnt]" + cardDefinition.getShipClass() + "[/font] Class");
            }

            // Text
            TextBlock textBlock = text.getTextBlocks().get(4);
            textBlock.setText(createCardText(cardDefinition));

            // Icons
            Array<CardIcon> icons = cardDefinition.getIcons();
            if (icons != null) {
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
            }

            if (cardType == CardType.Personnel) {
                // Stats
                TextBlock integrityBlock = text.getTextBlocks().get(5);
                integrityBlock.setText("I[scale 0.8]NTEGRITY[/scale] " + cardDefinition.getIntegrity());
                TextBlock cunningBlock = text.getTextBlocks().get(6);
                cunningBlock.setText("C[scale 0.8]UNNING[/scale] " + cardDefinition.getCunning());
                TextBlock strengthBlock = text.getTextBlocks().get(7);
                strengthBlock.setText("S[scale 0.8]TRENGTH[/scale] " + cardDefinition.getStrength());
            } else if (cardType == CardType.Ship) {
                // Stats
                TextBlock rangeBlock = text.getTextBlocks().get(5);
                rangeBlock.setText("R[scale 0.8]ANGE[/scale] " + cardDefinition.getRange());
                TextBlock weaponsBlock = text.getTextBlocks().get(6);
                weaponsBlock.setText("W[scale 0.8]EAPONS[/scale] " + cardDefinition.getWeapons());
                TextBlock shieldsBlock = text.getTextBlocks().get(7);
                shieldsBlock.setText("S[scale 0.8]HIELDS[/scale] " + cardDefinition.getShields());
            }
        }
        return cardRepresentation;
    }

    private static String getUnaffiliatedCardTemplate(CardType cardType) {
        if (cardType == CardType.Equipment)
            return "equipment-template";
        else if (cardType == CardType.Event)
            return "event-template";
        else if (cardType == CardType.Interrupt)
            return "interrupt-template";
        throw new GdxRuntimeException("Unable to resolve unaffiliated template: " + cardType);
    }

    private static String getAffiliatedCardTemplate(Affiliation affiliation) {
        if (affiliation == Affiliation.Federation)
            return "federation-template";
        else if (affiliation == Affiliation.Bajoran)
            return "bajoran-template";
        else if (affiliation == Affiliation.NonAligned)
            return "nonAligned-template";
        throw new GdxRuntimeException("Unable to resolve affiliated template: " + affiliation);
    }

    private static String createEffectText(CardDefinition cardDefinition, int abilityIndex) {
        return createEffectText(cardDefinition, false, abilityIndex, -1);
    }

    public static String createStepEffectText(CardDefinition cardDefinition, int abilityIndex, int step) {
        return createEffectText(cardDefinition, true, abilityIndex, step);
    }

    private static String createEffectText(CardDefinition cardDefinition, boolean useSteps, int abilityIndex, int step) {
        StringBuilder result = new StringBuilder();
        result.append("[b]Effect:[/b]\n");
        Array<JsonValue> cardAbilities = cardDefinition.getAbilities();
        if (cardAbilities != null) {
            JsonValue ability = cardAbilities.get(abilityIndex);
            String text = ability.getString("text");
            if (useSteps)
                text = replaceSteps(text, step);
            else
                text = removeSteps(text);
            result.append(text).append("\n");
        }
        return result.toString();
    }

    private static String createCardText(CardDefinition cardDefinition) {
        return createCardText(cardDefinition, false, -1, -1);
    }

    public static String createStepCardText(CardDefinition cardDefinition, int abilityIndex, int step) {
        return createCardText(cardDefinition, true, abilityIndex, step);
    }

    private static String createCardText(CardDefinition cardDefinition, boolean useSteps, int abilityIndex, int step) {
        StringBuilder result = new StringBuilder();
        Array<PersonnelSkill> skills = cardDefinition.getSkills();
        if (skills != null) {
            for (PersonnelSkill skill : PersonnelSkill.values()) {
                int count = 0;
                for (PersonnelSkill charSkill : skills) {
                    if (charSkill == skill)
                        count++;
                }
                if (count > 0) {
                    String countText = (count > 1) ? (count + " ") : "";
                    result.append("[width 0.5][letterSpacing 5][color ff0000]•[/color][/letterSpacing]").append(countText).append(skill).append("[/width] ");
                }
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
            for (int i = 0; i < cardAbilities.size; i++) {
                JsonValue ability = cardAbilities.get(i);
                String text = ability.getString("text");
                if (useSteps && abilityIndex == i)
                    text = replaceSteps(text, step);
                else
                    text = removeSteps(text);
                result.append(text).append("\n");
            }
        }
        String lore = cardDefinition.getLore();
        if (lore != null) {
            result.append("[horAlign justified][paddingLeft 20][paddingRight 20][width 0.46][scale 0.8][font font/arial-italic.fnt]").append(lore).append("[/font][/scale][/width][/paddingRight][/paddingLeft][/horAlign]");
        }

        return result.toString();
    }

    private static String replaceSteps(String text, int step) {
        text = text.replace("[/s]", "[/color]");
        Matcher matcher = stepStartPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        int consumed = 0;
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                sb.append(text, consumed, matcher.start(i));
                String textInTag = matcher.group(i).substring(3);
                textInTag = textInTag.substring(0, textInTag.length() - 1).trim();
                if (Integer.parseInt(textInTag) == step) {
                    sb.append("[color 0000ff]");
                } else {
                    sb.append("[color 000000]");
                }
                consumed = matcher.end(i);
            }
        }
        sb.append(text, consumed, text.length());
        return sb.toString();
    }

    private static String removeSteps(String text) {
        text = text.replace("[/s]", "");
        Matcher matcher = stepStartPattern.matcher(text);
        return matcher.replaceAll("");
    }
}
